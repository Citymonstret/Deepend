package com.minecade.deepend.prot;

import com.minecade.deepend.nativeprot.NativeBuf;

/**
 * Created 3/22/2016 for Deepend
 *
 * @author Citymonstret
 */
public class ProtocolDecoder {

    public static final ProtocolDecoder decoder = new ProtocolDecoder();

    public NativeBuf decode(byte[] bytes) throws Exception {
        if (bytes.length< 4) {
            return null;
        } else {
            int size = JavaProtocol.bytesToInt(bytes);

            byte[] remaining = new byte[bytes.length - 4];
            System.arraycopy(bytes, 4, remaining, 0, bytes.length - 4);

            if (remaining.length < size) {
                throw new Exception("Decoder needed " + size + " bytes, found " + remaining.length);
            } else {
                return new NativeBuf(remaining, size);
            }
        }
    }

}
