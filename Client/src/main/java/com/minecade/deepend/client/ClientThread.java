package com.minecade.deepend.client;

import com.minecade.deepend.channels.Channel;
import com.minecade.deepend.data.DeependBuf;
import com.minecade.deepend.logging.Logger;
import com.minecade.deepend.pipeline.DeependContext;
import com.minecade.deepend.request.PendingRequest;
import com.minecade.deepend.request.Request;
import lombok.NonNull;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author Citymonstret
 */
public class ClientThread extends Thread
{

    public static volatile boolean authenticationAttempted = false;

    private static int threadID = 0;
    private static int threads = 0;
    private static int sThreads = 0;
    private final String host;
    private final int port;
    private final DeependContext context;
    private final int id;
    private int skippedSets = 0;
    private long lastExecutionTime = -1;
    ClientThread(@NonNull final String host, final int port)
    {
        // direct parameters
        this.host = host;
        this.port = port;
        //

        //
        // TODO: Look into optimizing the default socket
        //
        final Socket socket;
        try
        {
            socket = new Socket();
        } catch ( final Exception e )
        {
            Logger.get().error( "cthread.connect.failure", e );
            throw new RuntimeException( e );
        }

        //
        // TODO: Optimize this
        //
        this.context = new DeependContext(
                DeependClient.getCurrentConnection(),
                socket,
                (InetSocketAddress) socket.getLocalSocketAddress()
        );

        threads++;
        this.id = getThreadID();
        this.setName( "ClientThread-" + id );
        this.setDaemon( false );
        this.start();
    }

    private static int getThreadID()
    {
        return ++threadID;
    }

    @Override
    public void run()
    {
        boolean connectionProblems = true;

        while ( !DeependClient.getInstance().shutdown )
        {
            if ( !connectionProblems )
            {
                if ( DeependClient.getCurrentConnection().isAuthenticated() )
                {

                    // This is used to postpone all actions after authentication, as
                    // it previously would cause some strange issues.
                    // TODO: Investigate cause to issues
                    if ( skippedSets > 0 )
                    {
                        if ( System.currentTimeMillis() - lastExecutionTime > 1000 )
                        {
                            --skippedSets;
                            lastExecutionTime = System.currentTimeMillis();
                        }
                        continue;
                    }

                    // TODO: Re-evaluate
                    // This might be a very stupid thing to do
                    // And I'm open to suggestions
                    final Request request = DeependClient.getInstance().cloud.get();
                    if ( request == null )
                    {
                        continue;
                    }

                    // This skips requests that might be invalid. This can be used to
                    // set a time limit to requests, or something similar to that
                    if ( request.validate() )
                    {
                        if ( !request.handle( context, DeependClient.getInstance().getChannelHandler() ) )
                        {
                            connectionProblems = true;
                            DeependClient.getInstance().cloud.addPendingRequest( request );
                        }
                    }
                } else
                {
                    if ( !authenticationAttempted )
                    {
                        try
                        {
                            // This is actually a quite good showcase of the construction
                            // of a request!
                            new PendingRequest( Channel.AUTHENTICATE )
                            {
                                @Override
                                protected void makeRequest(final DeependBuf buf)
                                {
                                    Logger.get().info( "common.authenticating" );
                                    buf.writeString( DeependClient.getInstance().getProperty( "auth.user" ) );
                                    buf.writeString( DeependClient.getInstance().getProperty( "auth.pass" ) );
                                }
                            }.handle( context, DeependClient.getInstance().getChannelHandler() );
                            authenticationAttempted = true;
                        } catch ( final Exception e )
                        {
                            Logger.get().error( getName() + " has connection problems" );
                            connectionProblems = true;
                        }
                    }
                }
            } else
            {
                skippedSets = 5;
                try
                {
                    Logger.get().info( "connection.attempting" );
                    // Re-force authentication = magic
                    DeependClient.getCurrentConnection()
                            .setAuthenticated( false );
                    // Prone to throw exceptions. Evil code :(((
                    context.getSocket()
                            .connect( new InetSocketAddress( host, port ), 10000 );
                    connectionProblems = false;

                    // Yay! We passed!
                    Logger.get().info( "connection.success" );
                } catch ( final Exception e )
                {
                    Logger.get().error( "connection.fail" );
                    try
                    {
                        sleep( 2500 );
                    } catch ( final Exception ee )
                    {
                        ee.printStackTrace();
                    }
                }
            }
        }

        //
        // SHUTDOWN CODE
        //

        if ( context.getSocket() != null )
        {
            try
            {
                context.getSocket().close();
            } catch ( IOException e )
            {
                Logger.get().error( "cthread.socket.close.failure", e );
            }
        }

        Logger.get().info( this.getName() + " shut down!" );

        ++sThreads;

        if ( sThreads >= threads )
        {
            Logger.get().info( "cthread.shutdown" );
            Logger.get().info( "shutdown.started" );
            System.exit( 0 );
        }
    }

}
