package com.minecade.deepend.server.channels.impl;

import com.minecade.deepend.channels.Channel;
import com.minecade.deepend.channels.DeependChannel;
import com.minecade.deepend.connection.DeependConnection;
import com.minecade.deepend.data.DeependBuf;
import lombok.NonNull;

/**
 * Created 2/26/2016 for Deepend
 *
 * @author Citymonstret
 */
public class UpdateData extends DeependChannel {

    public UpdateData() {
        super(Channel.UPDATE_DATA);
    }

    @Override
    public void act(@NonNull DeependConnection connection, @NonNull DeependBuf buf) {}
}
