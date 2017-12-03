package com.minecade.deepend.prot;

import com.minecade.deepend.data.DeependBuf;
import com.minecade.deepend.nativeprot.NativeBuf;

import java.nio.ByteBuffer;

/**
 * Created 3/22/2016 for Deepend
 *
 * @author Citymonstret
 */
public class ProtocolEncoder
{

    private static ProtocolEncoder encoder;

    public static ProtocolEncoder getEncoder()
    {
        if ( encoder == null )
        {
            encoder = new ProtocolEncoder();
        }
        return encoder;
    }

    public byte[] encode(DeependBuf buf)
    {
        NativeBuf nativeBuf = (NativeBuf) buf;
        int size = nativeBuf.getSize();
        ByteBuffer buffer = ByteBuffer.allocate( size );
        nativeBuf.compile( buffer );
        return buffer.array();
    }

}
