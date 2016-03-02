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
import com.minecade.deepend.connection.DeependConnection;
import com.minecade.deepend.data.DataObject;
import com.minecade.deepend.data.DeependBuf;
import com.minecade.deepend.logging.Logger;
import com.minecade.deepend.object.GenericResponse;
import com.minecade.deepend.request.DataRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Citymonstret
 */
public class AddData extends DeependChannel {

    public AddData() {
        super(Channel.ADD_DATA);
    }

    @Override
    public void act(DeependConnection connection, DeependBuf buf) {
        DeependBuf in = connection.getBuf("in");
        // ByteBuf in = connection.getObject("in", ByteBuf.class);

        String getID = in.getString();
        DataRequest request = DataRequest.getRequest(getID);

        if (request == null) {
            Logger.get().error("Got response for unregistered request, throwing!");
            return;
        }

        GenericResponse response = in.getResponse();

        if (response == GenericResponse.SUCCESS) {
            byte categoryByte = in.getByte();

            int num = in.getInt();

            List<Object> objects = new ArrayList<>();

            for (int i = 0; i < num; i++) {
                String identifier = in.getString();
                String data = in.getString();
                objects.add(new DataObject(identifier, data));
            }

            request.call(objects);
            request.delete();
        } else {
            Logger.get().error("Failed to add data: \""+ in.getString() + "\"");
        }
    }
}
