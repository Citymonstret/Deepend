package com.minecade.deepend.request;

import com.minecade.deepend.channels.Channel;
import com.minecade.deepend.data.DeependBuf;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import lombok.Getter;
import lombok.NonNull;

/**
 * Created by Citymonstret on 3/27/2016.
 */
public class DummyDataRequest extends DataRequest {

    public DummyDataRequest(@NonNull final Runnable recipient) {
        super(Channel.UNKNOWN, list -> recipient.run());
    }

    @Override
    protected void buildRequest(DeependBuf buf) {}

    @Override
    public void send(UUIDProvider provider, ChannelFuture future) {}

    @Override
    public boolean handle(UUIDProvider provider, Bootstrap bootstrap) {
        return true;
    }

}
