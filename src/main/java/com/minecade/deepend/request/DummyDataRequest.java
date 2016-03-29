package com.minecade.deepend.request;

import com.minecade.deepend.channels.Channel;
import com.minecade.deepend.channels.ChannelHandler;
import com.minecade.deepend.data.DeependBuf;

import com.minecade.deepend.pipeline.DeependContext;
import lombok.NonNull;

public class DummyDataRequest extends DataRequest {

    public DummyDataRequest(@NonNull final Runnable recipient) {
        super(Channel.UNKNOWN, list -> recipient.run());
    }

    @Override
    public void send(DeependContext context, ChannelHandler handler) {
        super.send(context, handler);
    }

    @Override
    public boolean handle(DeependContext context, ChannelHandler handler) {
        return true;
    }

    @Override
    protected void buildRequest(DeependBuf buf) {}


}
