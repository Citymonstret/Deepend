package com.minecade.deepend.prot;

import com.minecade.deepend.nativeprot.NativeBuf;
import com.minecade.deepend.nativeprot.NativeObj;

/**
 * Created 3/14/2016 for Deepend
 *
 * @author Citymonstret
 */
public class JavaProtocol implements Protocol {

    @Override
    public NativeBuf readNativeBuf(int i, byte[] b) {
        final Offset offset = new Offset();
        int objSize = bytesToInt(b, offset);

        NativeObj[] objs = new NativeObj[objSize];

        for (int o = 0; o < objSize; o++) {
            int required = offset.get() + 8;
            if (b.length < required) {
                break;
            }
            int type = bytesToInt(b, offset);
            int osize = bytesToInt(b, offset);
            if (type == NativeObj.TYPE_INT) {
                int in = bytesToInt(b, offset);
                objs[o] = new NativeObj(type, in, Byte.MIN_VALUE, "");
            } else if (type == NativeObj.TYPE_BYTE) {
                byte by = bytesToByte(b, offset);
                objs[o] = new NativeObj(type, 0, by, "");
            } else if(type == NativeObj.TYPE_STRING) {
                char[] chars = bytesToChars(b, osize, offset);
                objs[o] = new NativeObj(type, 0, Byte.MIN_VALUE, new String(chars));
            }
        }

        return new NativeBuf(objs);
    }

    @Override
    public NativeBuf writeNativeBuf(int i, NativeObj objs) {
        return null;
    }

    char[] bytesToChars(final byte[] bytes, final int size, final Offset offset) {
        char[] chars = new char[size];
        for (int i = 0; i < size; i++) {
            chars[i] = (char) bytes[offset.get() + i];
        }
        offset.add(size);
        return chars;
    }

    byte bytesToByte(final byte[] bytes, final Offset offset) {
        byte b = bytes[offset.get()];
        offset.add(1);
        return b;
    }

    int bytesToInt(final byte[] bytes, final Offset offset) {
        int result = 0;
        for (int i = 0; i < 4; i++) {
            result = (result << 8) + bytes[offset.get() + i];
        }
        offset.add(4);
        return result;
    }

    class Offset {
        int offset = 0;
        void set(int n) {
            offset = n;
        }
        void add(int n) {
            set(offset + n);
        }
        int get() {
            return offset;
        }
    }
}
