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
import com.minecade.deepend.data.DataManager;
import com.minecade.deepend.data.DataObject;
import com.minecade.deepend.data.DeependBuf;
import com.minecade.deepend.logging.Logger;
import com.minecade.deepend.object.DeependObject;
import com.minecade.deepend.object.GenericResponse;
import com.minecade.deepend.object.ObjectManager;
import com.minecade.deepend.request.DataRequest;
import com.minecade.deepend.request.SubscriptionRequest;

import java.util.ArrayList;
import java.util.List;

public class GetData extends DeependChannel {

    public GetData() {
        super(Channel.GET_DATA);
    }

    @Override
    public void act(DeependConnection connection, DeependBuf buf) {
        DeependBuf in = connection.getBuf("in");

        String getID = in.getString();

        DataRequest request;

        boolean deleted = false;

        if (getID.equals("subscription")) {
            request = SubscriptionRequest.getDummyRequest();
            deleted = in.getByte() != 0;
        } else {
            request = DataRequest.getRequest(getID);
        }

        GenericResponse response = in.getResponse();
        if (response == GenericResponse.SUCCESS) {
            byte categoryByte = in.getByte();

            int num = in.getInt();

            List<Object> objects = new ArrayList<>();

            if (!ObjectManager.instance.hasRegisteredType(categoryByte)) {
                for (int i = 0; i < num; i++) {
                    String identifier = in.getString();
                    String data = in.getString();
                    DataObject object = new DataObject(identifier, data);
                    if (deleted) {
                        object.setDeleted(true);
                    }
                    objects.add(deleted);
                }
            } else {
                while (in.readable()) {
                    DeependObject object = ObjectManager.instance
                            .construct(() -> categoryByte, in);
                    if (deleted) {
                        object.setDeleted(true);
                    }
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
