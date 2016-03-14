package com.minecade.deepend.nativeprot;

/**
 * Created 3/13/2016 for Deepend
 *
 * @author Citymonstret
 */
public class DeependProtocol {

    public static void setup() throws Exception {
        System.loadLibrary("NativeProtocol");
        initialize();
    }

    public native static void initialize();
    public native static void destroy();

    public native static NativeObj[] loadNativeBuf(int i, byte b[]);
}
