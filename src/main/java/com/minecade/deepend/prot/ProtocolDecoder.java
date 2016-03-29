package com.minecade.deepend.prot;

import com.minecade.deepend.nativeprot.NativeBuf;

import java.nio.ByteBuffer;

/**
 * Created 3/22/2016 for Deepend
 *
 * @author Citymonstret
 */
public class ProtocolDecoder {

    public NativeBuf decode(ByteBuffer buffer) throws Exception {
        if (buffer.remaining() < 4) {
            return null;
        } else {
            int size = buffer.getInt();
            if (buffer.remaining() < size) {
                throw new Exception("Decoder needed " + size + " bytes, found " + buffer.remaining());
            } else {
                return new NativeBuf(buffer, size);
            }
        }
    }

}
