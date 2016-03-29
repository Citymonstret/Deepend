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

import com.minecade.deepend.bytes.ByteProvider;
import com.minecade.deepend.channels.Channel;
import com.minecade.deepend.channels.DeependChannel;
import com.minecade.deepend.connection.DeependConnection;
import com.minecade.deepend.data.DataManager;
import com.minecade.deepend.data.DataStatus;
import com.minecade.deepend.data.DeependBuf;
import com.minecade.deepend.logging.Logger;
import com.minecade.deepend.values.ValueFactory;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * Created 2/26/2016 for Deepend
 *
 * @author Citymonstret
 */
public class CheckData extends DeependChannel {

    private static Collection<ByteProvider> convert(Collection in) {
        Collection<ByteProvider> collection = new HashSet<>();
        in.stream().filter(o -> o instanceof ByteProvider).forEach(o -> {
            collection.add((ByteProvider) o);
        });
        return collection;
    }

    public CheckData() {
        super(Channel.CHECK_DATA);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void act(@NonNull DeependConnection connection, @NonNull DeependBuf buf) {
        DeependBuf in = connection.getBuf("in");
        String request = in.getString();
        Logger.get().debug("Received request: " + request);
        buf.writeString(request); // Just echo the request ID
        Collection<ByteProvider> requested = convert(ValueFactory.getFactory(ValueFactory.FactoryType.DATA_TYPE).constructBitField().extract(in.getInt()));
        Collection<ByteProvider> updated = new ArrayList<>();
        requested.stream()
                .filter(r -> {
                    DataStatus status = DataManager.instance.getDataStatus(r);
                    return status != null && status.fetchUpdate(connection.getRemoteAddress());
                })
                .forEach(updated::add);
        int field = ValueFactory.getFactory(ValueFactory.FactoryType.DATA_TYPE).constructBitField().construct(updated);
        Logger.get().debug("Field: " + field);
        buf.writeInt(field);
    }

}
