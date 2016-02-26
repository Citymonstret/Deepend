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

/**
 * Created 2/26/2016 for Deepend
 *
 * @author Citymonstret
 */
public class CheckData extends DeependChannel {

    public CheckData() {
        super(Channel.CHECK_DATA);
    }

    @Override
    public void act(@NonNull DeependConnection connection, @NonNull DeependBuf buf) {
        DeependBuf in = connection.getBuf("in");
        String request = in.getString();
        Logger.get().debug("Received request: " + request);
        buf.writeString(request); // Just echo the request ID
        Collection requested = ValueFactory.getFactory(ValueFactory.FactoryType.DATA_TYPE).constructBitField().extract(in.getInt());
        Collection updated = new ArrayList<>();

        requested.stream()
                .filter(r -> {
                    DataStatus status = DataManager.instance.getDataStatus((ByteProvider) r);
                    return status != null && status.fetchUpdate(connection.getRemoteAddress());
                })
                .forEach(updated::add);

        int field = ValueFactory.getFactory(ValueFactory.FactoryType.DATA_TYPE).constructBitField().construct(updated);
        Logger.get().debug("Field: " + field);
        buf.writeInt(field);
    }

}
