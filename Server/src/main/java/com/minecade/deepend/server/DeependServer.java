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
import com.minecade.deepend.DeependApplication;
import com.minecade.deepend.DeependChannelInitializer;
import com.minecade.deepend.DeependConstants;
import com.minecade.deepend.channels.ChannelManager;
import com.minecade.deepend.data.DataManager;
import com.minecade.deepend.logging.Logger;
import com.minecade.deepend.reflection.Field;
import com.minecade.deepend.resources.DeependBundle;
import com.minecade.deepend.server.channels.MainChannel;
import com.minecade.deepend.server.exceptions.ServerException;
import com.minecade.deepend.storage.StorageBase;
import com.minecade.deepend.util.JavaConstants;
import com.minecade.deepend.util.StringUtils;
import com.minecade.deepend.values.ValueFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.ResourceLeakDetector;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;

/**
 * The main server implementation
 */
public class DeependServer implements Runnable {

    private interface ServerSettings {

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

    private final int port;

    @Getter
    protected StorageBase storageBase;

    @SneakyThrows
    public DeependServer(String[] iargs, @NonNull DeependServerApplication application) {
        Logger.setup(new Field(DeependConstants.class)
                .withProperties(Field.FieldProperty.ACCESS_GRANT, Field.FieldProperty.ACCESS_REVERT, Field.FieldProperty.STATIC, Field.FieldProperty.CONSTANT)
                .named("server_name").getValue().toString(), new DeependBundle("ServerStrings"));

        ServerSettings settings;
        try {
            settings = CliFactory.parseArguments(ServerSettings.class, iargs);
        } catch(final Exception e) {
            throw new ServerException("Failed to parse server arguments", e);
        }

        if (settings.getHelp()) {
            Logger.get().info(StringUtils.joinLines(
                    "\nDeependServer Help:",
                    "\t--help | --h : Display this help message",
                    "\t--port | -p <port> : Set the server port"
            ));
            System.exit(JavaConstants.EXIT_STATUS_SUCCESS);
        }

        this.port = settings.getPort();

        Logger.get().info("bootstrap.starting", this.port);

        // This isn't fully implemented, feel free to uncomment this
        // if you wish, though
        // this.storageBase = new SQLite("server_persistent_storage");
        // this.storageBase.setup();
        this.storageBase = null;

        // Register channels and
        // lock the manager
        application.registerChannels(ChannelManager.instance);
        ChannelManager.instance.lock();
        // Register byte factories
        // and lock the factory manager
        application.registerFactories();
        ValueFactory.lock();
        // This shouldn't be locked
        application.registerDataHolders(DataManager.instance);
        // Run stuff after initial managers and whatnot
        application.after(this);
    }

    final public void run() {
        if (System.getProperty("io.netty.leakDetectionLevel") == null) {
            ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.DISABLED);
        }

        Thread thread = new Thread("ServerThread") {
            @Override
            public void run() {
                // Let's use the simplest group
                // setup possible
                EventLoopGroup bossGroup = new NioEventLoopGroup();
                EventLoopGroup workerGroup = new NioEventLoopGroup();

                // Exceptions aren't fun kids. Stay away from them!
                try {
                    // This should be clear enough
                    ServerBootstrap bootstrap = new ServerBootstrap();

                    // Setup the bootstrap
                    bootstrap.group(bossGroup, workerGroup)
                            .channel(NioServerSocketChannel.class)
                            .childHandler(new DeependChannelInitializer(MainChannel.class))
                            .option(ChannelOption.SO_BACKLOG, 128)
                            .childOption(ChannelOption.SO_KEEPALIVE, true);

                    // Yay for logging
                    Logger.get().info("bootstrap.started");

                    // At least they've got a future :o
                    ChannelFuture future;

                    // Yay for nested try statements
                    try {
                        // start the server
                        future = bootstrap.bind(port).sync();
                    } catch(final Exception e) { // Nuh! This shouldn't happen :(
                        Logger.get().error("bootstrap.failed.bind");
                        return;
                    }
                    // Wait until the socket is closed
                    future.channel().closeFuture().sync();

                    if (storageBase != null) {
                        storageBase.close();
                    }
                } catch(final Exception e) { // Why do you have to hurt me?
                    e.printStackTrace();
                }
            }
        };

        thread.setDaemon(false);
        thread.start();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                Logger.get().info("Shutdown requested");

                if (storageBase != null) {
                    storageBase.close();
                }
            }
        });
    }

    /**
     * Simple server application interface
     */
    public interface DeependServerApplication extends DeependApplication {

        /**
         * Register data holders
         * @param dataManager Data manager
         */
        void registerDataHolders(DataManager dataManager);

        default void after(Object context) {
            after((DeependServer) context);
        }

        void after(DeependServer server);
    }
}
