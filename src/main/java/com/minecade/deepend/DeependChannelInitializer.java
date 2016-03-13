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

package com.minecade.deepend;

import com.minecade.deepend.lib.Beta;
import com.minecade.deepend.logging.Logger;
import com.minecade.deepend.netty.compability.AbstractRemoteAddressFilter;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import lombok.Getter;
import lombok.NonNull;

import java.net.InetSocketAddress;

/**
 * This is just a basic netty channel initializer
 * that will initialize the channels using a universal
 * set of options
 *
 * @author Citymonstret
 */
public final class DeependChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final Logger logger;

    @Getter
    private final Class<? extends ChannelHandlerAdapter> channelHandlerAdapter;

    public DeependChannelInitializer(Class<? extends ChannelHandlerAdapter> channelHandlerAdapter) {
        Logger.setup("DeependChannelInitializer", null);
        this.logger = Logger.get("DeependChannelInitializer");
        this.channelHandlerAdapter = channelHandlerAdapter;
        this.logger.info("Created new channel initializer for: " + channelHandlerAdapter);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }

    @Beta
    @Override
    protected void initChannel(@NonNull final SocketChannel socketChannel) {
        final ChannelPipeline pipeline = socketChannel.pipeline();

        // Add GZIP Encryption
        // pipeline.addLast(ZlibCodecFactory.newZlibEncoder(ZlibWrapper.GZIP));
        // pipeline.addLast(ZlibCodecFactory.newZlibDecoder(ZlibWrapper.GZIP));

        // If this is a client, limit IP connections
        // to the server, only
        if (DeependMeta.hasMeta("client")) {
            pipeline.addLast(new AbstractRemoteAddressFilter<InetSocketAddress>() {
                @Override
                protected boolean accept(ChannelHandlerContext ctx, InetSocketAddress remoteAddress) throws Exception {
                    boolean allowed = remoteAddress.getHostName().equals(DeependMeta.getMeta("serverAddr"))
                                && ("" + remoteAddress.getPort()).equals(DeependMeta.getMeta("serverPort"));
                    if (!allowed) {
                        DeependChannelInitializer.this.logger.debug("Dropping channel attempt from: " + remoteAddress.getHostName());
                    } else {
                        DeependChannelInitializer.this.logger.debug("Allowed from: " + remoteAddress.getHostName());
                    }
                    return allowed;
                }
            });
        }

        try {
            // This is the main channel; we're using our
            // own wrappers, because netty is too complex
            // for what we need
            pipeline.addLast(getChannelHandlerAdapter().newInstance());
        } catch (final Exception e) {
            this.logger.error("Failed to add channel handler adapter", e);
        }
    }
}
