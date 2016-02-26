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

package com.minecade.deepend.server.channels;

import com.minecade.deepend.ConnectionFactory;
import com.minecade.deepend.ServerResponse;
import com.minecade.deepend.channels.Channel;
import com.minecade.deepend.channels.ChannelManager;
import com.minecade.deepend.connection.DeependConnection;
import com.minecade.deepend.connection.SimpleAddress;
import com.minecade.deepend.data.DeependBuf;
import com.minecade.deepend.logging.Logger;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

import java.net.InetSocketAddress;
import java.util.UUID;

/**
 * The main server channel implementation
 */
public class MainChannel extends ChannelHandlerAdapter {

    @Override
    final public void channelRead(final ChannelHandlerContext context, Object message) {
        DeependBuf in = new DeependBuf((ByteBuf) message);

        // Prevent writing to the input buf
        in.lock();

        try {
            int channelID = in.getInt();
            final ByteBuf response = context.alloc().buffer();

            ServerResponse serverResponse = ServerResponse.UNKNOWN;
            Channel channel;
            boolean everythingFine = false;
            DeependConnection connection = null;
            DeependBuf written = new DeependBuf(context.alloc().buffer());

            scope: {
                // Get the requested channel
                channel = Channel.getChannel(channelID);
                if (channel == null) {
                    serverResponse = ServerResponse.INVALID_CHANNEL;
                }
                if (channel == Channel.AUTHENTICATE) {
                    // Let's create a temporary connection
                    connection = new DeependConnection(new SimpleAddress(((InetSocketAddress) context.channel().remoteAddress()).getHostName()));
                } else {
                    int uuidLenght = in.getInt();
                    byte[] uuidBytes = new byte[uuidLenght];
                    for (int i = 0; i < uuidLenght; i++) {
                        uuidBytes[i] = in.getByte();
                    }

                    UUID uuid;

                    try {
                        uuid = UUID.fromString(new String(uuidBytes));
                    } catch(final Exception e) {
                        serverResponse = ServerResponse.INVALID_UUID;
                        break scope;
                    }

                    Logger.get().info("Given UUID: " + uuid.toString());
                    connection = ConnectionFactory.instance.getOrCreate(context.channel().remoteAddress(), uuid);
                }

                if (!connection.isAuthenticated()) {
                    if (channel != Channel.AUTHENTICATE) {
                        serverResponse = ServerResponse.REQUIRES_AUTHENTICATION;
                    } else {
                        serverResponse = ServerResponse.AUTHENTICATION_ATTEMPTED;
                    }
                }

                // Add connection meta
                connection.addMeta("context", context);
                connection.addMeta("in", in);
                connection.addMeta("address", context.channel().remoteAddress());

                Logger.get().info("Server Response Code: " + serverResponse.name());

                if (channel == null) {
                    channel = Channel.UNKNOWN;
                }

                everythingFine = channel != Channel.UNKNOWN && serverResponse != ServerResponse.REQUIRES_AUTHENTICATION;

                if (everythingFine && serverResponse != ServerResponse.AUTHENTICATION_ATTEMPTED) {
                    serverResponse = ServerResponse.SUCCESS;
                }
            }

            if (everythingFine) {
                Logger.get().info("Calling channel of type: " + channel);
                try {
                    ChannelManager.instance.getChannel(channel).act(connection, written);
                } catch(final Exception e) {
                    Logger.get().error("Channel failed :/", e);
                    serverResponse = ServerResponse.CHANNEL_EXCEPTION;
                }
            }
            // Write the response code
            response.writeByte(serverResponse.getValue());
            // Write the channel ID
            assert channel != null; // Ugh, java sux :(
            response.writeInt(channel.getValue());
            // Copy channel response
            //response.writeBytes(written);
            written.copyTo(response);

            // Write and listen for receiving of data
            final ChannelFuture f = context.writeAndFlush(response);
            f.addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) throws Exception {
                    assert f == future;
                    context.close();

                    Logger.get().debug("closed");
                }
            });
        } finally {
            ReferenceCountUtil.release(message);
        }
    }

    @Override
    final public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
        cause.printStackTrace();
        context.close();
    }
}
