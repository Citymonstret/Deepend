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

import com.minecade.deepend.ServerResponse;
import com.minecade.deepend.channels.Channel;
import com.minecade.deepend.channels.ChannelHandler;
import com.minecade.deepend.channels.ChannelManager;
import com.minecade.deepend.connection.DeependConnection;
import com.minecade.deepend.data.DeependBuf;
import com.minecade.deepend.logging.Logger;
import com.minecade.deepend.nativeprot.NativeBuf;
import com.minecade.deepend.pipeline.DeependContext;

/**
 * The main server channel implementation
 */
public class MainChannel extends ChannelHandler {

    @Override
    public void handle(NativeBuf in, NativeBuf response, DeependContext context) {
        // The ID of the requested channel
        int channelID = in.getInt();
        // The response status from the server
        ServerResponse serverResponse = ServerResponse.UNKNOWN;
        Channel channel;
        boolean everythingFine;
        DeependConnection connection = context.getConnection();
        DeependBuf written = new NativeBuf();
        {
            channel = Channel.getChannel(channelID);
            if (channel == null) {
                serverResponse = ServerResponse.INVALID_CHANNEL;
            }

            if (!connection.isAuthenticated()) {
                if (channel != Channel.AUTHENTICATE) {
                    serverResponse = ServerResponse.REQUIRES_AUTHENTICATION;
                } else {
                    serverResponse = ServerResponse.AUTHENTICATION_ATTEMPTED;
                }
            }

            connection.addMeta("in", in);
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
        response.writeInt(channel.getValue());
        // Copy channel response
        //response.writeBytes(written);
        // ((NettyBuf) written).copyTo(response);
        ((NativeBuf) written).copyTo(response);
    }
}
