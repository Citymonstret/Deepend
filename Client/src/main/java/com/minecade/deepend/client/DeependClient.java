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
import com.minecade.deepend.DeependChannelInitializer;
import com.minecade.deepend.DeependMeta;
import com.minecade.deepend.channels.Channel;
import com.minecade.deepend.channels.ChannelManager;
import com.minecade.deepend.client.channels.MainChannel;
import com.minecade.deepend.client.channels.impl.AddData;
import com.minecade.deepend.client.channels.impl.CheckData;
import com.minecade.deepend.client.channels.impl.DeleteData;
import com.minecade.deepend.client.channels.impl.GetData;
import com.minecade.deepend.connection.DeependConnection;
import com.minecade.deepend.connection.SimpleAddress;
import com.minecade.deepend.data.DeependBuf;
import com.minecade.deepend.logging.Logger;
import com.minecade.deepend.nativeprot.DeependProtocol;
import com.minecade.deepend.object.ObjectManager;
import com.minecade.deepend.request.PendingRequest;
import com.minecade.deepend.request.ShutdownRequest;
import com.minecade.deepend.resources.DeependBundle;
import com.minecade.deepend.values.ValueFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.Synchronized;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

import static com.minecade.deepend.DeependConstants.*;

/**
 * This is the client class. This is final, as it isn't meant
 * to be extended. Instead you're supposed to start it from
 * a remote class.
 *
 * Wiki: <a>https://github.com/DeependProject/Deepend/wiki/Client</a>
 *
 * For an example, see {@link com.minecade.deepend.client.test.TestGameClient}
 *
 * @author Citymonstret
 */
public final class DeependClient {

    private static DeependClient instance;

    public static DeependClient getInstance() {
        if (instance == null) {
            throw new RuntimeException("Cannot get instance before it's declared");
        }
        return instance;
    }

    @Getter
    private static DeependConnection currentConnection;

    @Getter
    public String echoTestString;

    private volatile boolean shutdown, isShutdown = false;
    private volatile boolean sentAuthenticationRequest;

    @Getter
    private final DeependBundle properties;

    private final Collection<PendingRequest> pendingRequests;

    @SneakyThrows
    public DeependClient(@NonNull DeependClientApplication application, boolean useProvided, String host, int port) {
        DeependClient.instance = this;
        DeependMeta.setMeta(CLIENT_META, "true");

        Logger.setup(CLIENT_NAME, new DeependBundle(CLIENT_STRINGS, true));

        // These are the default settings for the client
        this.properties = new DeependBundle(CLIENT_META, false, DeependBundle.DefaultBuilder.create()
                .add("echo.string", "Test")
                .add("auth.user", "admin")
                .add("auth.pass", "password")
                .add("conn.host", "localhost")
                .add("conn.port", "8000")
                .build()
        );

        try {
            DeependProtocol.setup();
        } catch (final Exception e) {
            Logger.get().error("Failed to setup native protocol library, using built in", e);
        }

        byte[] cat = "Cat".getBytes();
        for (byte b : cat) {
            System.out.print(b + ",");
        }
        System.out.println();
        System.out.println("len: " + cat.length);

        byte[] bytes = new byte[] {
                0,0,0,4, // Number of objects
                0,0,0,0, // Type of object 1
                0,0,0,1, // Size of object 1
                      1, // Value of object 1
                0,0,0,1, // Type of Object 2
                0,0,0,4, // Size of object 2
                0,0,0,6, // Value of object 2
                0,0,0,2, // Type of object 2
                0,0,0,3, // Length of object 3
                67, 97, 116,
                0,0,0,0, // Type of object 1
                0,0,0,1, // Size of object 1
                1, // Value of object 1
        };

        System.out.println("Start testing");
        try {
            System.out.println("ReadNativeBuf: " + DeependProtocol.readNativeBuf(bytes.length, bytes));
        } catch (final Exception e) {
            e.printStackTrace();
        }
        System.exit(0);

        // Let's load in some properties
        this.echoTestString = getProperty("echo.string");

        // Local variables
        String host1;
        int port1;

        if (!useProvided) {
            host1 = getProperty("conn.host");
            port1 = Integer.parseInt(getProperty("conn.port"));
        } else {
            Logger.get().info("Using provided values, this is not recommended.");
            host1 = host;
            port1 = port;
        }

        {   // SETUP CONNECTION LIMITATIONS
            DeependMeta.setMeta("serverAddr", host1);
            DeependMeta.setMeta("serverPort", port1 + "");
        }

        {   // DEFAULT VALUES
            this.shutdown = false;
            this.pendingRequests = Collections.synchronizedSet(new LinkedHashSet<>());
        }

        {   // CHANNEL SETUP
            ChannelManager.instance.addChannel(new GetData());
            ChannelManager.instance.addChannel(new DeleteData());
            ChannelManager.instance.addChannel(new AddData());
            ChannelManager.instance.addChannel(new CheckData());

            // Register custom channels
            application.registerChannels(ChannelManager.instance);

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
            application.registerObjectMappings(ObjectManager.instance);
        }

        final EventLoopGroup workerGroup = new NioEventLoopGroup();

        final Bootstrap b = new Bootstrap();
        b.group(workerGroup);
        b.channel(NioSocketChannel.class);
        b.option(ChannelOption.SO_REUSEADDR, true);
        b.option(ChannelOption.TCP_NODELAY, true);
        b.handler(new DeependChannelInitializer(MainChannel.class));

        b.remoteAddress(host1, port1);

        try {
            currentConnection = new DeependConnection(new SimpleAddress(InetAddress.getLocalHost().getHostName()));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        // Register requests that will
        // be sent as soon as we're
        // authenticated
        application.registerInitialRequests(this);

        Logger.get().info("thread.starting");

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    workerGroup.shutdownGracefully();
                } catch(final Exception e) {
                    e.printStackTrace();
                }
                Logger.get().info("shutdown.completed");
            }
        });

        final Thread thread = new Thread() {
            {
                setName("ClientThread");
            }

            @Override
            public void run() {
                ChannelFuture future;
                List<PendingRequest> toRemove;
                // We're not sure if things are working or
                // not, so we'll just assume that they aren't
                boolean connectionProblems = true;
                while (!shutdown) {
                    if (!connectionProblems) {
                        if (getCurrentConnection().isAuthenticated()) {
                            toRemove = new ArrayList<>();
                            for (PendingRequest r : pendingRequests)
                                requests:{
                                    if (r instanceof ShutdownRequest) {
                                        requestShutdown();
                                        break requests;
                                    }
                                    if (r.validate()) {
                                        try {
                                            future = b.connect().sync();
                                            r.send(future);
                                            future.channel().closeFuture().sync();
                                            // Make sure to only remove
                                            // if it actually succeeded
                                            // => will automatically
                                            // re-try this if it didn't
                                            // work the first time
                                            toRemove.add(r);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                            connectionProblems = true;
                                        }
                                    } else {
                                        toRemove.add(r);
                                    }
                                }
                            pendingRequests.removeAll(toRemove);
                        } else {
                            if (!sentAuthenticationRequest) {
                                try {
                                    future = b.connect().sync();

                                    new PendingRequest(Channel.AUTHENTICATE) {
                                        @Override
                                        protected void makeRequest(final DeependBuf buf) {
                                            Logger.get().info("common.authenticating");
                                            buf.writeString(getProperty("auth.user"));
                                            buf.writeString(getProperty("auth.pass"));
                                        }
                                    }.send(future);
                                    future.channel().closeFuture().sync();

                                    sentAuthenticationRequest = true;
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                    connectionProblems = true;
                                }
                            }
                        }
                    } else {
                        try {
                            Logger.get().info("connection.attempting");

                            // Disconnecting means that we
                            // cannot trust that we're
                            // authenticated, let's force authenticate!
                            currentConnection.setAuthenticated(false);

                            // This will just connect and instantly
                            // disconnect, just to make sure that it
                            // actually works
                            b.connect().sync().channel().close().sync();

                            // Yay, it worked! Now let's reset the error
                            // status ;D
                            connectionProblems = false;

                            Logger.get().info("connection.success");
                        } catch(final Exception e) {
                            Logger.get().error("connection.fail");
                            try {
                                sleep(2500);
                            } catch(final InterruptedException ee) {
                                ee.printStackTrace();
                            }
                        }
                    }
                }
                workerGroup.shutdownGracefully();
                Logger.get().info("shutdown.completed");
                isShutdown = true;
            }
        };

        thread.setDaemon(false);
        thread.start();
    }

    public DeependClient(DeependClientApplication application) {
        this(application, false, "", -1);
    }

    @Synchronized
    public void requestShutdown() {
        if (!shutdown) {
            Logger.get().info("shutdown.requested");
        }
        this.shutdown = true;
    }

    /**
     * Add a pending request that will be scheduled and
     * sent as soon as there is a possibility
     *
     * @param r Request to send
     */
    public void addPendingRequest(@NonNull final PendingRequest r) {
        this.pendingRequests.add(r);
    }

    public String getProperty(@NonNull String key) {
        return this.properties.get(key);
    }

    /**
     * Reset the authentication status for this client,
     * which forces it to re-authenticate
     */
    public void resetAuthenticationPendingStatus() {
        this.sentAuthenticationRequest = false;
    }

    public boolean isShutdown() {
        return isShutdown;
    }

    /**
     * <a>https://github.com/DeependProject/Deepend/wiki/Client</a>
     */
    public interface DeependClientApplication extends DeependApplication {

        default DeependConnection currentConnection() {
            return DeependClient.getCurrentConnection();
        }

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
        default void registerChannels(ChannelManager channelManager) {}
    }
}
