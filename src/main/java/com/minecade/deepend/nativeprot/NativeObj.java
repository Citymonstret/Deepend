package com.minecade.deepend.nativeprot;

import lombok.Getter;

/**
 * Created 3/13/2016 for Deepend
 *
 * @author Citymonstret
 */
public class NativeObj
{

    public static final int TYPE_INT = 1;
    public static final int TYPE_BYTE = 0;
    public static final int TYPE_STRING = 2;

    private final int type;
    @Getter
    byte b;
    @Getter
    String s;
    @Getter
    private int i;

    public NativeObj()
    {
        this.type = TYPE_BYTE;
    }

    public NativeObj(int type, int i, byte b, String s)
    {
        this.type = type;
        this.i = i;
        this.b = b;
        this.s = s;
    }

    public NativeObj(byte b)
    {
        this.type = TYPE_BYTE;
        this.b = b;
    }

    public NativeObj(int i)
    {
        this.type = TYPE_INT;
        this.i = i;
    }

    public NativeObj(String s)
    {
        this.type = TYPE_STRING;
        this.s = s;
    }

    public int getType()
    {
        return this.type;
    }

}
