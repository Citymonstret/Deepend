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

import com.minecade.deepend.values.ValueFactory;
import com.minecade.deepend.channels.Channel;
import com.minecade.deepend.channels.DeependChannel;
import com.minecade.deepend.connection.DeependConnection;
import com.minecade.deepend.data.DataObject;
import com.minecade.deepend.data.DeependBuf;
import com.minecade.deepend.logging.Logger;
import com.minecade.deepend.object.*;
import com.minecade.deepend.request.DataRequest;

import java.util.ArrayList;
import java.util.List;

public class GetData extends DeependChannel {

    public GetData() {
        super(Channel.GET_DATA);
    }

    @Override
    public void act(DeependConnection connection, DeependBuf buf) {
        // ByteBuf in = connection.getObject("in", ByteBuf.class);
        DeependBuf in = connection.getBuf("in");

        String getID = in.getString();
        Logger.get().debug("Getting response for: " + getID);
        DataRequest request = DataRequest.getRequest(getID);

        if (request == null) {
            Logger.get().error("Got response for unregistered request, throwing!");
            return;
        }

        GenericResponse response = in.getResponse();
        if (response == GenericResponse.SUCCESS) {
            byte categoryByte = in.getByte();

            String category = ValueFactory.getFactory(ValueFactory.FactoryType.DATA_TYPE)
                    .getName(categoryByte);

            Logger.get().debug("Type: " + category);

            int num = in.getInt();
            Logger.get().debug("Found " + num + " objects");

            List<Object> objects = new ArrayList<>();

            if (!ObjectManager.instance.hasRegisteredType(categoryByte)) {
                for (int i = 0; i < num; i++) {
                    String identifier = in.getString();
                    String data = in.getString();
                    Logger.get().info("Data received: \"" + identifier + "\":\"" + data + "\"");
                    objects.add(new DataObject(identifier, data));
                }
            } else {
                while (in.readable()) {
                    DeependObject object = ObjectManager.instance
                            .construct(() -> categoryByte, in);
                    objects.add(object);
                }
            }

            request.call(objects);
            request.delete();
        } else {
            Logger.get().error("Failed to fetch data: \""+ in.getString() + "\"");
        }
    }
}
