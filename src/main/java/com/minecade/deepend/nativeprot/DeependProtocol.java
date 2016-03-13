package com.minecade.deepend.nativeprot;

/**
 * Created 3/13/2016 for Deepend
 *
 * @author Citymonstret
 */
public class DeependProtocol {

    public static void setup() throws Exception {
        System.loadLibrary("DeependProtocol");
    }

    public native static NativeBuf readNativeBuf(int i, byte b[]);

    public native static NativeBuf writeNativeBuf(int i, NativeObj objs);

}
