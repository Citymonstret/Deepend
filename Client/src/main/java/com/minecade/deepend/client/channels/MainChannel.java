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
import com.minecade.deepend.channels.ChannelHandler;
import com.minecade.deepend.channels.ChannelManager;

import com.minecade.deepend.client.ClientThread;
import com.minecade.deepend.client.DeependClient;
import com.minecade.deepend.logging.Logger;
import com.minecade.deepend.nativeprot.NativeBuf;
import com.minecade.deepend.object.GenericResponse;
import com.minecade.deepend.pipeline.DeependContext;

public class MainChannel extends ChannelHandler {

    @Override
    public void handle(NativeBuf in, NativeBuf response, DeependContext __context) {
        ServerResponse serverResponse = ServerResponse.getServerResponse(in.getByte());
        Channel channel = Channel.getChannel(in.getInt());
        Logger.get().info("Received message. Response: " + serverResponse.name() + " | Channel: " + channel.name());
        if (serverResponse == ServerResponse.AUTHENTICATION_ATTEMPTED) {
            GenericResponse authenticationResponse = GenericResponse.getGenericResponse(in.getByte());

            if (authenticationResponse == GenericResponse.SUCCESS) {
                DeependClient.getCurrentConnection().setAuthenticated(true);
                ClientThread.authenticationAttempted = false;

                Logger.get().info("Authentication succeeded");
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
    }

}
