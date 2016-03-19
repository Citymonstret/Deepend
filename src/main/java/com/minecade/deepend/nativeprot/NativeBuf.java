package com.minecade.deepend.nativeprot;

import com.minecade.deepend.data.DeependBuf;
import com.minecade.deepend.prot.JavaProtocol;
import com.minecade.deepend.prot.Protocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;

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
    private ByteBuf buf;

    private int readIndex = 0;
    private boolean updated = false;

    public NativeBuf(final NativeObj[] objects) {
        this(objects, null, null);
    }

    public NativeBuf(final ByteBuf in) {
        if (in.readableBytes() < 4 /* size of an integer */) {
            this.buf = in;
            this.i_objects = new ArrayList<>();
        } else {
            int size = in.readInt();
            this.bytes = in.readBytes(size).array();
            NativeBuf tmp = protocol.readNativeBuf(size, bytes);
            this.objects = tmp.getObjects();
            this.i_objects = tmp.i_objects;
            this.buf = in;
        }
    }

    public NativeBuf(final NativeObj[] objects, final byte[] bytes, ByteBuf buf) {
        this.buf = buf;
        this.objects = objects;
        this.bytes = bytes;
        this.i_objects = new ArrayList<>(Arrays.asList(objects));
    }

    public void setByteBuf(ByteBuf buf) {
        this.buf = buf;
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
        return objects[readIndex++].getS();
    }

    @Override
    protected byte readByte() {
        return objects[readIndex++].getB();
    }

    @Override
    protected int readInt() {
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
    public void writeAndFlush(ChannelFuture future) {
        checkCompile();
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);
        future.channel().writeAndFlush(buf);
    }

    public ChannelFuture writeAndFlush(ChannelHandlerContext context) {
        checkCompile();
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);
        return context.writeAndFlush(buf);
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
}
