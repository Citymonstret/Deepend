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

package com.minecade.deepend.client.channels.impl;

import com.minecade.deepend.channels.Channel;
import com.minecade.deepend.channels.DeependChannel;
import com.minecade.deepend.client.DeependClient;
import com.minecade.deepend.connection.DeependConnection;
import com.minecade.deepend.data.DeependBuf;
import com.minecade.deepend.logging.Logger;

public class EchoChannel extends DeependChannel {

    public EchoChannel() {
        super(Channel.ECHO);
    }

    @Override
    public void act(DeependConnection connection, DeependBuf buf) {
        DeependBuf in = connection.getObject("in", DeependBuf.class);

        String returned;
        Logger.get().debug("Echo: " + (returned = in.getString()));

        if (!returned.equals(DeependClient.getInstance().echoTestString)) {
            Logger.get().error("Echo test string didn't return correctly, shutting down!");
            DeependClient.getInstance().requestShutdown();
        }
    }
}
