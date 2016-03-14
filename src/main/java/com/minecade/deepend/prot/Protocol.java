package com.minecade.deepend.prot;

import com.minecade.deepend.nativeprot.NativeBuf;
import com.minecade.deepend.nativeprot.NativeObj;

/**
 * Created 3/14/2016 for Deepend
 *
 * @author Citymonstret
 */
public interface Protocol {

    NativeBuf readNativeBuf(int i, byte b[]);

    NativeBuf writeNativeBuf(int i, NativeObj objs);

}
