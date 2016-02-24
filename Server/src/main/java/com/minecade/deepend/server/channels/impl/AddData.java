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

import com.minecade.deepend.channels.Channel;
import com.minecade.deepend.channels.DeependChannel;
import com.minecade.deepend.connection.DeependConnection;
import com.minecade.deepend.data.DataHolder;
import com.minecade.deepend.data.DataManager;
import com.minecade.deepend.data.DataObject;
import com.minecade.deepend.data.DeependBuf;
import com.minecade.deepend.logging.Logger;
import com.minecade.deepend.object.ByteFactory;
import com.minecade.deepend.object.GenericResponse;

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
        DeependBuf in = connection.getObject("in", DeependBuf.class);

        // Just echo the request ID
        buf.writeString(in.getString());

        scope: {
            // String category = readString(in);
            byte categoryByte = in.getByte();
            String category = ByteFactory.getFactory(ByteFactory.FactoryType.DATA_TYPE).getName(categoryByte);

            if (!DataManager.instance.hasDataHolder(category)) {
                buf.writeByte(GenericResponse.FAILURE);
                buf.writeString("Category not found :(");
                break scope;
            }

            DataHolder holder = DataManager.instance.getDataHolder(category);

            String[] pathParts = in.getString().split(".");
            for (String part : pathParts) {
                if (holder.containsKey(part)) {
                    holder = (DataHolder) holder.get(part);
                } else {
                    DataHolder newHolder = new DataHolder(part);
                    holder.put(part, newHolder);
                    holder = newHolder;
                }
            }

            List<DataObject> object = new ArrayList<>();

            int numObjects = in.getInt();

            for (int i = 0; i < numObjects; i++) {
                String[] pieces = in.getString().split(":");
                if (pieces.length < 2) {
                    pieces = new String[] {pieces[0], ""};
                }
                DataObject o = new DataObject(pieces[0], pieces[1]);
                holder.put(pieces[0], o);
                object.add(o);
            }

            buf.writeByte(GenericResponse.SUCCESS);

            // Write some response info
            buf.writeByte(categoryByte);
            buf.writeInt(object.size());

            object.forEach(Logger.get()::dump);
        }
    }
}
