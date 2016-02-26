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

package com.minecade.deepend.server.channels.impl;

import com.minecade.deepend.ConnectionFactory;
import com.minecade.deepend.channels.Channel;
import com.minecade.deepend.channels.DeependChannel;
import com.minecade.deepend.connection.DeependConnection;
import com.minecade.deepend.data.DeependBuf;
import com.minecade.deepend.logging.Logger;
import com.minecade.deepend.object.GenericResponse;
import com.minecade.deepend.resources.DeependBundle;

public class Authentication extends DeependChannel {

    public Authentication() {
        super(Channel.AUTHENTICATE);
    }

    private static DeependBundle accountBundle;
    static {
        accountBundle = new DeependBundle("accounts", false, DeependBundle.DefaultBuilder.create().add("admin.password", "password").build());
    }

    @Override
    public void act(DeependConnection connection, DeependBuf buf) {
        DeependBuf in = connection.getObject("in", DeependBuf.class);

        String username = in.getString();
        String password = in.getString();

        GenericResponse response = GenericResponse.FAILURE;

        if (accountBundle.containsKey(username + ".password")) {
            if (accountBundle.get(username + ".password").equals(password)) {
                Logger.get().info("Authenticated: " + connection.getRemoteAddress().toString());
                connection.setAuthenticated(true);
                response = GenericResponse.SUCCESS;
                ConnectionFactory.instance.addConnection(connection);
            }
        }

        buf.writeByte(response.getValue());

        if (response == GenericResponse.SUCCESS) {
            buf.writeString(connection.getRemoteAddress().getUUID());
        }
    }
}
