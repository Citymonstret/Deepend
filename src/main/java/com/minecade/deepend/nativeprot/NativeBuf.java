package com.minecade.deepend.nativeprot;

/**
 * Created 3/13/2016 for Deepend
 *
 * @author Citymonstret
 */
public class NativeBuf {

    private final int size;
    private final byte[] bytes;
    private final NativeObj[] objects;

    public NativeBuf(final int size, final byte[] bytes, final NativeObj[] objects) {
        this.size = size;
        this.bytes = bytes;
        this.objects = objects;
    }

    public NativeObj[] getObjects() {
        return this.objects;
    }

    public int getSize() {
        return this.size;
    }

    public byte[] getBytes() {
        return this.bytes;
    }
}
