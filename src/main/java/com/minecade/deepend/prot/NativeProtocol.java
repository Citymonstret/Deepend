package com.minecade.deepend.prot;

import com.minecade.deepend.nativeprot.DeependProtocol;
import com.minecade.deepend.nativeprot.NativeBuf;

/**
 * Created 3/14/2016 for Deepend
 *
 * @author Citymonstret
 */
public class NativeProtocol implements Protocol {

    @Override
    public NativeBuf readNativeBuf(int i, byte[] b) {
        return new NativeBuf(
                DeependProtocol.loadNativeBuf(i, b)
        );
    }

    @Override
    public NativeBuf writeNativeBuf(int i, NativeBuf buf) {
        return null;
        // return DeependProtocol.writeNativeBuf(i, objs);
    }

}
