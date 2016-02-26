package com.minecade.deepend.server.channels.impl;

import com.minecade.deepend.bytes.ByteProvider;
import com.minecade.deepend.channels.Channel;
import com.minecade.deepend.channels.DeependChannel;
import com.minecade.deepend.connection.DeependConnection;
import com.minecade.deepend.data.DataManager;
import com.minecade.deepend.data.DeependBuf;
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
        Collection requested = ValueFactory.getFactory(ValueFactory.FactoryType.DATA_TYPE).constructBitField().extract(in.getInt());
        Collection updated = new ArrayList<>();
        requested.stream().filter(r -> DataManager.instance.getDataStatus((ByteProvider) r).fetchUpdate(connection.getRemoteAddress())).forEach(updated::add);
        buf.writeInt(ValueFactory.getFactory(ValueFactory.FactoryType.DATA_TYPE).constructBitField().construct(updated));
    }

}
