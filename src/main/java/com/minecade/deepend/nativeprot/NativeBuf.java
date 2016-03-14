package com.minecade.deepend.nativeprot;

/**
 * Created 3/13/2016 for Deepend
 *
 * @author Citymonstret
 */
public class NativeBuf {

    private final NativeObj[] objects;

    public NativeBuf(final NativeObj[] objects) {
        this.objects = objects;
    }

    public NativeObj[] getObjects() {
        return this.objects;
    }
}
