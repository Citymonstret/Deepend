package com.minecade.deepend.data;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;

/**
 * Created 3/14/2016 for Deepend
 *
 * @author Citymonstret
 */
public class NettyBuf extends DeependBuf {

    private ByteBuf buf;

    public NettyBuf(final ByteBuf buf) {
        this.buf = buf;
        this.writtenIndices = 0;
    }

    /**
     * Create a buf from another buf, and
     * pre-load the specified values
     * @param deependBuf Buf to re-create
     * @param types Values to pre-load
     */
    public NettyBuf(final NettyBuf deependBuf, DataType[] types) {
        this(deependBuf.buf, types);
    }

    /**
     * Wrap a ByteBuf and pre-load
     * the specified values
     * @param buf Buf to wrap
     * @param types Values to pre-load
     */
    public NettyBuf(final ByteBuf buf, DataType[] types) {
        this.dataType = types;
        this.buf = buf;
        this.object = new Object[types.length];

        for (DataType ignored : types) {
            read();
        }

        this.writtenIndices = types.length;
    }

    protected void _writeByte(byte b) {
        buf.writeByte(b);
    }

    protected void _writeString(String str) {
        byte[] bytes = str.getBytes();
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);
    }

    protected String readString() {
        int length = buf.readInt();
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++) {
            bytes[i] = buf.readByte();
        }
        return new String(bytes);
    }

    protected byte readByte() {
        return buf.readByte();
    }

    protected int readInt() {
        return buf.readInt();
    }

    protected void _writeInt(int n) {
        buf.writeInt(n);
    }

    public boolean readable() {
        return buf.isReadable();
    }

    public void _writeAll(NettyBuf in) {
        checkLock();
        buf.writeBytes(in.buf);
    }

    public void reset() {
        buf.resetWriterIndex();
        buf.resetReaderIndex();
    }

    public void copyTo(NettyBuf mirrorBuf) {
        copyTo(mirrorBuf.buf);
    }

    /**
     * Write to the future's channel, and flush it
     * @param future Future
     */
    public void writeAndFlush(ChannelFuture future) {
        checkLock();

        future.channel().writeAndFlush(buf);
    }

    /**
     * Copy the bytes from this
     * buf to a ByteBuf
     * @param buf Buf to copy to
     */
    public void copyTo(ByteBuf buf) {
        buf.writeBytes(this.buf);
    }
}
