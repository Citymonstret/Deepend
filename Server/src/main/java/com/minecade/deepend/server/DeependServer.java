/*
 * Copyright 2016 Minecade
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.minecade.deepend.server;

import com.lexicalscope.jewel.cli.CliFactory;
import com.lexicalscope.jewel.cli.Option;
import com.minecade.deepend.ConnectionFactory;
import com.minecade.deepend.DeependApplication;
import com.minecade.deepend.DeependConstants;
import com.minecade.deepend.SimpleConnectionFactory;
import com.minecade.deepend.channels.ChannelHandler;
import com.minecade.deepend.channels.ChannelManager;
import com.minecade.deepend.connection.DeependConnection;
import com.minecade.deepend.data.DataManager;
import com.minecade.deepend.logging.Logger;
import com.minecade.deepend.nativeprot.NativeBuf;
import com.minecade.deepend.pipeline.DeependContext;
import com.minecade.deepend.prot.ProtocolDecoder;
import com.minecade.deepend.prot.ProtocolEncoder;
import com.minecade.deepend.reflection.Field;
import com.minecade.deepend.resources.DeependBundle;
import com.minecade.deepend.server.channels.MainChannel;
import com.minecade.deepend.server.exceptions.ServerException;
import com.minecade.deepend.storage.StorageBase;
import com.minecade.deepend.util.JavaConstants;
import com.minecade.deepend.util.StringUtils;
import com.minecade.deepend.values.ValueFactory;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.Synchronized;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * The main server implementation
 */
public class DeependServer implements Runnable
{

    @Getter
    private static final ConnectionFactory connectionFactory = new SimpleConnectionFactory();

    private static final ProtocolDecoder decoder = new ProtocolDecoder();
    private static final ProtocolEncoder encoder = ProtocolEncoder.getEncoder();
    private static final ChannelHandler handler = new MainChannel();
    @Getter
    private final int port;
    @Getter
    protected StorageBase storageBase;
    private boolean internalShutdown;

    @SneakyThrows
    public DeependServer(@NonNull final String[] iargs, @NonNull final DeependServerApplication application)
    {
        // This will replace the logger name, which is a constant (using reflection magic)
        Logger.setup( new Field( DeependConstants.class )
                .withProperties( Field.FieldProperty.ACCESS_GRANT, Field.FieldProperty.ACCESS_REVERT, Field.FieldProperty.STATIC, Field.FieldProperty.CONSTANT )
                .named( "server_name" ).getValue().toString(), new DeependBundle( "ServerStrings" ) );

        final ServerSettings settings;
        try
        {
            settings = CliFactory.parseArguments( ServerSettings.class, iargs );
        } catch ( final Exception e )
        {
            throw new ServerException( "Failed to parse server arguments", e );
        }

        if ( settings.getHelp() )
        {
            Logger.get().info( StringUtils.joinLines(
                    "\nDeependServer Help:",
                    "\t--help | --h : Display this help message",
                    "\t--port | -p <port> : Set the server port"
            ) );
            System.exit( JavaConstants.EXIT_STATUS_SUCCESS );
        }

        this.port = settings.getPort();

        Logger.get().info( "bootstrap.starting", this.port );

        // This isn't fully implemented, feel free to uncomment this
        // if you wish, though
        // this.storageBase = new SQLite("server_persistent_storage");
        // this.storageBase.setup();
        this.storageBase = null;

        // Register channels and
        // lock the manager
        application.registerChannels( ChannelManager.instance );
        ChannelManager.instance.lock();
        // Register byte factories
        // and lock the factory manager
        application.registerFactories();
        ValueFactory.lock();
        // This shouldn't be locked
        application.registerDataHolders( DataManager.instance );
        // Run stuff after initial managers and whatnot
        application.after( this );
    }

    final public void run()
    {
        final Thread thread = new Thread( "ServerThread" )
        {
            @Override
            public void run()
            {
                ServerSocket serverSocket = null;

                try
                {
                    serverSocket = new ServerSocket( port );
                } catch ( final Exception e )
                {
                    Logger.get().error( "Failed to bind to port " + port + ", is it busy?", e );
                }

                if ( serverSocket == null || !serverSocket.isBound() )
                {
                    Logger.get().error( "Failed to start the server socket, shutting down" );
                    System.exit( JavaConstants.EXIT_STATUS_FAILURE );
                }

                // Finalized copy
                final ServerSocket finalServerSocket = serverSocket;

                // Make sure that everything is shutdown correctly
                Runtime.getRuntime().addShutdownHook( new Thread( "ShutdownThread" )
                {

                    {
                        Logger.get().info( "Added shutdown hook" );
                        this.setDaemon( false );
                    }

                    @Override
                    public void run()
                    {
                        shutdown( finalServerSocket );
                    }
                } );

                Logger.get().info( "Server started on port " + port );

                while ( !serverSocket.isClosed() )
                {
                    tick( serverSocket );
                }

                // Call the shutdown method manually if this stage is reached
                shutdown( serverSocket );
            }
        };

        thread.setDaemon( false );
        thread.start();
    }

    @Synchronized
    private void shutdown(@NonNull final ServerSocket socket)
    {
        if ( internalShutdown )
        {
            Logger.get().error( "Cannot shutdown already shutdown server socket" );
            return;
        }

        // Make sure that we cannot do this twice
        internalShutdown = true;

        // Save the storage base if it's setup
        if ( storageBase != null )
        {
            storageBase.close();
        }

        // Close the socket
        try
        {
            if ( !socket.isClosed() )
            {
                socket.close();
            }
        } catch ( IOException e )
        {
            Logger.get().error( "Failed to close the server socket", e );
        }

        Logger.get().info( "Successfully shutdown the server" );
    }

    private void tick(@NonNull final ServerSocket serverSocket)
    {
        Socket temp = null;

        try
        {
            temp = serverSocket.accept();
        } catch ( final Exception e )
        {
            Logger.get().error( "Failed to accept incoming socket", e );
        }
        if ( temp == null || !temp.isConnected() )
        {
            Logger.get().error( "Failed to accept socket, throwing" );
            return;
        }

        Logger.get().info( "Accepted socket from: " + temp.getRemoteSocketAddress().toString() );

        final DeependConnection connection = getConnectionFactory().createConnection(
                (InetSocketAddress) temp.getRemoteSocketAddress()
        );

        final DeependContext context = new DeependContext(
                connection, temp, (InetSocketAddress) temp.getRemoteSocketAddress()
        );


        new Thread( connection.getRemoteAddress().toString() )
        {
            @Override
            public void run()
            {
                final InputStream iStream;
                try
                {
                    iStream = context.getSocket().getInputStream();
                } catch ( final Exception e )
                {
                    Logger.get().error( "Failed to fetch socket InputStream", e );
                    try
                    {
                        context.getSocket().close();
                    } catch ( final Exception ee )
                    {
                        Logger.get().error( "Failed to close socket", ee );
                    }
                    return;
                }

                final OutputStream oStream;
                try
                {
                    oStream = context.getSocket().getOutputStream();
                } catch ( final Exception e )
                {
                    Logger.get().error( "Failed to fetch socket outputStream" );
                    try
                    {
                        context.getSocket().close();
                    } catch ( final Exception ee )
                    {
                        Logger.get().error( "Failed to close socket", ee );
                    }
                    return;
                }

                ByteArrayOutputStream bufferStream = null;

                int nRead;
                byte[] data = new byte[ 1024 * 1024 ];

                while ( context.getSocket().isConnected() )
                {
                    try
                    {
                        bufferStream = new ByteArrayOutputStream();
                        try
                        {
                            while ( ( nRead = iStream.read( data, 0, data.length ) ) != -1 )
                            {
                                Logger.get().info( "read: " + nRead );

                                // This is a bit stupid, sure.
                                // But it does the job, and it does it real good
                                bufferStream.reset();
                                bufferStream.write( data, 0, nRead );
                                bufferStream.flush();

                                byte[] readBytes = bufferStream.toByteArray();

                                Logger.get().info( "Read bytes: " + readBytes.length );

                                if ( readBytes.length > 4 )
                                {
                                    NativeBuf inputBuf;

                                    try
                                    {
                                        inputBuf = decoder.decode( readBytes );
                                    } catch ( final Exception e )
                                    {
                                        Logger.get().error( "Failed to extract NativeBuf", e );
                                        continue;
                                    }

                                    NativeBuf outputBuf = new NativeBuf();
                                    try
                                    {
                                        handler.handle( inputBuf, outputBuf, context );
                                    } catch ( final Exception e )
                                    {
                                        Logger.get().error( "Failed to handle request", e );
                                        continue;
                                    }

                                    byte[] output = encoder.encode( outputBuf );
                                    oStream.write( output );
                                    oStream.flush();
                                }
                            }
                        } catch ( final Exception e )
                        {
                            Logger.get().error( "Failed to read stream input" );
                            if ( e instanceof SocketException )
                            {
                                Logger.get().error( "Cancelling the connection :(" );
                                return;
                            }
                        }
                    } finally
                    {
                        if ( bufferStream != null )
                        {
                            try
                            {
                                bufferStream.close();
                            } catch ( IOException e )
                            {
                                Logger.get().error( "Failed to close bufferStream", e );
                            }
                        }

                        Logger.get().debug( "Closed connection thread" );
                    }
                }
            }
        }.start();
    }

    /**
     * This is used to parse client arguments, using {@link CliFactory}
     */
    private interface ServerSettings
    {

        @Option(
                longName = "port",
                shortName = "p",
                pattern = "[0-9]+",
                defaultValue = "8000",
                description = "The port the server will run on"
        )
        int getPort();

        @Option(
                description = "Display the help menu",
                longName = "help",
                shortName = "h"
        )
        boolean getHelp();
    }

    /**
     * Simple server application interface
     */
    public interface DeependServerApplication extends DeependApplication
    {

        /**
         * Register data holders
         *
         * @param dataManager Data manager
         */
        void registerDataHolders(DataManager dataManager);

        default void after(Object context)
        {
            after( (DeependServer) context );
        }

        void after(DeependServer server);
    }
}
