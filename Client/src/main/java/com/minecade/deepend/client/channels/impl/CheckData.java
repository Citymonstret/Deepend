package com.minecade.deepend.client.channels.impl;

import com.minecade.deepend.channels.Channel;
import com.minecade.deepend.channels.DeependChannel;
import com.minecade.deepend.connection.DeependConnection;
import com.minecade.deepend.data.DeependBuf;
import com.minecade.deepend.logging.Logger;
import com.minecade.deepend.request.StatusRequest;
import lombok.NonNull;

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
    public void act(@NonNull DeependConnection connection, DeependBuf buf) {
        DeependBuf in = connection.getBuf("in");
        String getID = in.getString();
        Logger.get().debug("Getting response for: " + getID);
        StatusRequest request = StatusRequest.getRequest(getID);
        if (request == null) {
            Logger.get().error("Got response for unregistered request, throwing!");
            return;
        }
        request.call(in.getInt());
    }
}
