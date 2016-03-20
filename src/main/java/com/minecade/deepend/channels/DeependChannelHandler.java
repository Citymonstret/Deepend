package com.minecade.deepend.channels;

import com.minecade.deepend.connection.DeependConnection;
import com.minecade.deepend.data.DeependBuf;
import com.minecade.deepend.nativeprot.NativeBuf;

import java.util.UUID;

/**
 * Created 3/20/2016 for Deepend
 *
 * @author Citymonstret
 */
public interface DeependChannelHandler {

    void handle(NativeBuf in, NativeBuf out, Object context) throws Exception;

    DeependBuf generateBuf(Object context);

    DeependConnection generateConnection(Object context);

    DeependConnection generateConnection(Object context, UUID uuid);
}
