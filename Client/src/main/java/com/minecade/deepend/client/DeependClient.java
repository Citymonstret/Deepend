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

package com.minecade.deepend.client;

import com.minecade.deepend.DeependApplication;
import com.minecade.deepend.DeependMeta;
import com.minecade.deepend.channels.ChannelHandler;
import com.minecade.deepend.channels.ChannelManager;
import com.minecade.deepend.client.channels.MainChannel;
import com.minecade.deepend.client.channels.impl.AddData;
import com.minecade.deepend.client.channels.impl.CheckData;
import com.minecade.deepend.client.channels.impl.DeleteData;
import com.minecade.deepend.client.channels.impl.GetData;
import com.minecade.deepend.connection.DeependConnection;
import com.minecade.deepend.connection.SimpleAddress;
import com.minecade.deepend.logging.Logger;
import com.minecade.deepend.object.ObjectManager;
import com.minecade.deepend.request.Request;
import com.minecade.deepend.resources.DeependBundle;
import com.minecade.deepend.values.ValueFactory;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static com.minecade.deepend.DeependConstants.CLIENT_META;
import static com.minecade.deepend.DeependConstants.CLIENT_NAME;
import static com.minecade.deepend.DeependConstants.CLIENT_STRINGS;

/**
 * This is the client class. This is final, as it isn't meant
 * to be extended. Instead you're supposed to start it from
 * a remote class.
 * <p>
 * Wiki: <a>https://github.com/DeependProject/Deepend/wiki/Client</a>
 * <p>
 * For an example, see {@link com.minecade.deepend.client.test.TestGameClient}
 *
 * @author Citymonstret
 */
public final class DeependClient
{

    private static DeependClient instance;
    @Getter
    private static DeependConnection currentConnection;
    final RequestCloud cloud = new RequestCloud();
    @Getter
    private final ChannelHandler channelHandler = new MainChannel();
    @Getter
    private final DeependBundle properties;
    @Getter
    public String echoTestString;
    protected volatile boolean shutdown;
    @Getter
    private int connectionPools = 3;
    private volatile boolean isShutdown = false;

    @SneakyThrows
    public DeependClient(@NonNull final DeependClientApplication application, final boolean useProvided, final String host, final int port)
    {
        DeependClient.instance = this;

        DeependMeta.setMeta( CLIENT_META, "true" );

        Logger.setup( CLIENT_NAME, new DeependBundle( CLIENT_STRINGS, true ) );

        // These are the default settings for the client
        this.properties = new DeependBundle( CLIENT_META, false, DeependBundle.DefaultBuilder.create()
                .add( "echo.string", "Test" )
                .add( "auth.user", "admin" )
                .add( "auth.pass", "password" )
                .add( "conn.host", "localhost" )
                .add( "conn.port", "8000" )
                .add( "pools", "3" )
                .build()
        );

        this.connectionPools = Integer.parseInt( getProperty( "pools" ) );

        // Let's load in some properties
        this.echoTestString = getProperty( "echo.string" );

        // Local variables
        String host1;
        int port1;

        if ( !useProvided )
        {
            host1 = getProperty( "conn.host" );
            port1 = Integer.parseInt( getProperty( "conn.port" ) );
        } else
        {
            Logger.get().info( "Using provided values, this is not recommended." );
            host1 = host;
            port1 = port;
        }

        {   // SETUP CONNECTION LIMITATIONS
            DeependMeta.setMeta( "serverAddr", host1 );
            DeependMeta.setMeta( "serverPort", port1 + "" );
        }

        {   // DEFAULT VALUES
            this.shutdown = false;
        }

        {   // CHANNEL SETUP
            ChannelManager.instance.addChannel( new GetData() );
            ChannelManager.instance.addChannel( new DeleteData() );
            ChannelManager.instance.addChannel( new AddData() );
            ChannelManager.instance.addChannel( new CheckData() );

            // Register custom channels
            application.registerChannels( ChannelManager.instance );

            // Lock channel registration
            ChannelManager.instance.lock();
        }

        {   // FACTORY SETUP
            // Register byte factories, before everything
            // is loaded
            application.registerFactories();

            // Lock byte factory registration
            ValueFactory.lock();

            // Will register all object
            // mappings
            application.registerObjectMappings( ObjectManager.instance );
        }

        try
        {
            currentConnection = new DeependConnection( new SimpleAddress( InetAddress.getLocalHost().getHostName() ) );
        } catch ( UnknownHostException e )
        {
            e.printStackTrace();
        }

        // Register requests that will
        // be sent as soon as we're
        // authenticated
        application.registerInitialRequests( this );

        Logger.get().info( "thread.starting", connectionPools );

        for ( int i = 0; i < connectionPools; i++ )
        {
            new ClientThread( host1, port1 );
        }

        new SubscriptionSocket( channelHandler, host1 );
    }

    public DeependClient(final DeependClientApplication application)
    {
        this( application, false, "", -1 );
    }

    public static DeependClient getInstance()
    {
        if ( instance == null )
        {
            throw new RuntimeException( "Cannot get instance before it's declared" );
        }
        return instance;
    }

    /**
     * Add a pending request that will be scheduled and
     * sent as soon as there is a possibility
     *
     * @param r Request to send
     */
    public void addPendingRequest(@NonNull final Request r)
    {
        this.cloud.addPendingRequest( r );
    }

    public String getProperty(@NonNull final String key)
    {
        return this.properties.get( key );
    }

    public boolean isShutdown()
    {
        return isShutdown;
    }

    /**
     * <a>https://github.com/DeependProject/Deepend/wiki/Client</a>
     */
    public interface DeependClientApplication extends DeependApplication
    {

        /**
         * Register requests that will be sent as soon
         * as the client has established a connection
         * to the server
         *
         * @param client Client (global instance)
         */
        void registerInitialRequests(DeependClient client);

        /**
         * Use the ObjectManager to bind any DeependObject implementations to
         * their specified object type
         *
         * @param objectManager Manager (global instance)
         */
        void registerObjectMappings(ObjectManager objectManager);

        @Override
        default void registerChannels(ChannelManager channelManager)
        {
        }
    }
}
