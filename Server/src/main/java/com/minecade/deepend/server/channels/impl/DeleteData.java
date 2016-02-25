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
import com.minecade.deepend.object.ByteFactory;
import com.minecade.deepend.object.GenericResponse;
import com.minecade.deepend.util.DataUtil;

import java.util.ArrayList;
import java.util.List;

public class DeleteData extends DeependChannel {

    public DeleteData() {
        super(Channel.REMOVE_DATA);
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
            List<DataObject> object = DataUtil.getDataObject(holder, null, in, new ArrayList<>(), true);

            if (object == null) {
                buf.writeByte(GenericResponse.FAILURE);
                buf.writeString("Object not found :(");
                break scope;
            }

            // Write the response
            buf.writeByte(GenericResponse.FAILURE);

            // Write some response info
            buf.writeByte(categoryByte);
            buf.writeInt(object.size());

            // Write the actual response(s)
            for (DataObject o : object) {
                buf.writeString(o.getName());
                buf.writeString(o.getValue());

                // Will cause the object to delete itself
                // how sad
                o.delete();
            }

            resetStatus();
        }
    }
}
