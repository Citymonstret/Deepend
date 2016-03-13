package com.minecade.deepend.nativeprot;

/**
 * Created 3/13/2016 for Deepend
 *
 * @author Citymonstret
 */
public class NativeObj {

    public static final int TYPE_INT = 1;
    public static final int TYPE_BYTE = 0;
    public static final int TYPE_STRING = 3;

    private final int type;
    private int i;
    byte b;
    String s;

    public NativeObj(final int type, int i, byte b, String s) {
        this.type = type;
        this.i = i;
        this.b = b;
        this.s = s;
    }

    public int getType() {
        return this.type;
    }

}
