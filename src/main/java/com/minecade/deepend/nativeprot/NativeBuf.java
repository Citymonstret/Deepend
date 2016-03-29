package com.minecade.deepend.nativeprot;

import com.minecade.deepend.data.DeependBuf;
import com.minecade.deepend.pipeline.DeependContext;
import com.minecade.deepend.prot.JavaProtocol;
import com.minecade.deepend.prot.Protocol;
import com.minecade.deepend.prot.ProtocolEncoder;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created 3/13/2016 for Deepend
 *
 * @author Citymonstret
 */
public class NativeBuf extends DeependBuf {

    public static final Protocol protocol = new JavaProtocol(); // Make this customizable

    private NativeObj[] objects;
    private final List<NativeObj> i_objects;
    private byte[] bytes;

    private int readIndex = 0;
    private boolean updated = false;

    public NativeBuf(final NativeObj[] objects) {
        this(objects, null, null);
    }

    public NativeBuf(final ByteBuffer in, int size) {
        this.bytes = in.array();
        NativeBuf tmp = protocol.readNativeBuf(size, bytes);
        this.objects = tmp.getObjects();
        this.i_objects = tmp.i_objects;
    }

    public NativeBuf(final NativeObj[] objects, final byte[] bytes, ByteBuffer buf) {
        this.objects = objects;
        this.bytes = bytes;
        if (objects == null) {
            this.i_objects = new ArrayList<>();
        } else {
            this.i_objects = new ArrayList<>(Arrays.asList(objects));
        }
    }

    public NativeBuf() {
        this(null, null, null);
    }

    public NativeObj[] getObjects() {
        if (updated) {
            this.objects = this.i_objects.toArray(new NativeObj[this.i_objects.size()]);
            this.updated = false;
        }
        return this.objects;
    }

    public byte[] getBytes() {
        return this.bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    @Override
    protected void _writeByte(byte b) {
        this.i_objects.add(new NativeObj(b));
        setUpdated();
    }

    protected void setUpdated() {
        this.updated = true;
    }

    @Override
    protected void _writeString(String str) {
        this.i_objects.add(new NativeObj(str));
        setUpdated();
    }

    @Override
    protected String readString() {
        if (objects == null) {
            checkCompile();
        }
        return objects[readIndex++].getS();
    }

    @Override
    protected byte readByte() {
        if (objects == null) {
            checkCompile();
        }
        return objects[readIndex++].getB();
    }

    @Override
    protected int readInt() {
        if (objects == null) {
            checkCompile();
        }
        return objects[readIndex++].getI();
    }

    @Override
    protected void _writeInt(int n) {
        this.i_objects.add(new NativeObj(n));
        setUpdated();
    }

    @Override
    public boolean readable() {
        return readIndex < objects.length;
    }

    @Override
    public void reset() {
        this.readIndex = 0;
        this.i_objects.clear();
        this.updated = false;
    }

    @Override
    public void writeAndFlush(DeependContext context) {
        try {
            OutputStream outputStream = context.getSocket().getOutputStream();
            outputStream.write(ProtocolEncoder.getEncoder().encode(this));
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void compile(ByteBuffer out) {
        checkCompile();
        out.putInt(bytes.length);
        out.put(bytes);
    }

    private void checkCompile() {
        if (updated || this.bytes == null) {
            if (this.objects == null) {
                this.objects = new NativeObj[0];
            }
            protocol.writeNativeBuf(0, this);
        }
    }

    public void copyTo(NativeBuf buf) {
        checkCompile();
        for (NativeObj nativeObj : objects) {
            buf.addObj(nativeObj);
        }
    }

    protected void addObj(NativeObj obj) {
        this.i_objects.add(obj);
        setUpdated();
    }

    public int getSize() {
        checkCompile();
        return 4 + bytes.length;
    }
}
