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

package com.minecade.deepend.client.channels;

import com.minecade.deepend.ServerResponse;
import com.minecade.deepend.channels.Channel;
import com.minecade.deepend.channels.ChannelManager;
import com.minecade.deepend.client.DeependClient;
import com.minecade.deepend.data.DataType;
import com.minecade.deepend.data.DeependBuf;
import com.minecade.deepend.logging.Logger;
import com.minecade.deepend.object.GenericResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class MainChannel extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(final ChannelHandlerContext context, Object message) {
        DeependBuf in = new DeependBuf((ByteBuf) message, new DataType[] {
                DataType.BYTE
        });

        // Make sure no one tries to write to this
        in.lock();

        try {
            ServerResponse serverResponse = ServerResponse.getServerResponse(in.getByte());
            Channel channel = Channel.getChannel(in.getInt());

            Logger.get().info("Received message. Response: " + serverResponse.name() + " | Channel: " + channel.name());

            if (serverResponse == ServerResponse.AUTHENTICATION_ATTEMPTED) {
                GenericResponse authenticationResponse = GenericResponse.getGenericResponse(in.getByte());

                if (authenticationResponse == GenericResponse.SUCCESS) {
                    int uuidLenght = in.getInt();
                    byte[] bytes = new byte[uuidLenght];
                    for (int i = 0; i < uuidLenght; i++) {
                        bytes[i] = in.getByte();
                    }

                    DeependClient.getCurrentConnection().getRemoteAddress().setUUID(new String(bytes));
                    DeependClient.getCurrentConnection().setAuthenticated(true);
                    DeependClient.getInstance().resetAuthenticationPendingStatus();

                    Logger.get().info("Authentication succeeded | Authentication UUID: " + DeependClient.getCurrentConnection().getRemoteAddress().getUUID());
                } else {
                    Logger.get().error("Authentication failed | Was the login details correct?");
                }
            } else if (serverResponse == ServerResponse.ALREADY_AUTHENTICATED) {
                DeependClient.getCurrentConnection().setAuthenticated(true);
            }

            if (serverResponse == ServerResponse.REQUIRES_AUTHENTICATION) {
                Logger.get().error("Not authenticated before trying to run queries!!!");
            } else if (serverResponse == ServerResponse.INVALID_UUID) {
                Logger.get().error("Invalid uuid???");
            } else if (serverResponse == ServerResponse.CHANNEL_EXCEPTION) {
                Logger.get().error("Server channel failed to return data");
            } else {
                if (channel != Channel.UNKNOWN && channel != Channel.AUTHENTICATE) {
                    DeependClient.getCurrentConnection().addMeta("in", in);
                    ChannelManager.instance.getChannel(channel).act(DeependClient.getCurrentConnection(), null);
                }
            }
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            ReferenceCountUtil.release(message);
        }
    }
}
