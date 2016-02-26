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

import com.minecade.deepend.DeependApplication;
import com.minecade.deepend.DeependChannelInitializer;
import com.minecade.deepend.channels.ChannelManager;
import com.minecade.deepend.data.DataManager;
import com.minecade.deepend.logging.Logger;
import com.minecade.deepend.values.ValueFactory;
import com.minecade.deepend.resources.DeependBundle;
import com.minecade.deepend.server.channels.MainChannel;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.ResourceLeakDetector;
import lombok.NonNull;
import lombok.SneakyThrows;

/**
 * The main server implementation
 */
public class DeependServer implements Runnable {

    private final int port;
    private final DeependServerApplication application;

    @SneakyThrows
    public DeependServer(final int port, @NonNull DeependServerApplication application) {
        Logger.setup("DeependServer", /* ResourceBundle.getBundle("ServerStrings")*/ new DeependBundle("ServerStrings"));
        Logger.get().info("bootstrap.starting");

        this.port = port;
        this.application = application;

        // Register channels and
        // lock the manager
        application.registerChannels(ChannelManager.instance);
        ChannelManager.instance.lock();
        // Register byte factories
        // and lock the factory manager
        application.registerByteFactories();
        ValueFactory.lock();
        // This shouldn't be locked
        application.registerDataHolders(DataManager.instance);
        // Run stuff after initial managers and whatnot
        application.after();
    }

    final public void run() {
        if (System.getProperty("io.netty.leakDetectionLevel") == null) {
            ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.DISABLED);
        }

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
        } catch(final Exception e) { // Why do you have to hurt me?
            e.printStackTrace();
        }
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

    }
}
