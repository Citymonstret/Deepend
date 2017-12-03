package com.minecade.deepend.channels;

import com.minecade.deepend.nativeprot.NativeBuf;
import com.minecade.deepend.pipeline.DeependContext;

public abstract class ChannelHandler
{

    public abstract void handle(NativeBuf in, NativeBuf out, DeependContext context);

}
