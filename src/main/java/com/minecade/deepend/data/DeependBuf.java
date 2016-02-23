/*
 * Copyright 2016 Minecade
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.minecade.deepend.data;

import com.minecade.deepend.object.ByteProvider;
import com.minecade.deepend.object.GenericResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;

/**
 * A wrapper for the netty ByteBuf
 *
 * @see ByteBuf
 *
 * This allows for some magnificent
 * things, and this is what the whole
 * project is built to be using
 */
public class DeependBuf {

    private ByteBuf buf;

    private DataType[] dataType;
    private Object[] object;

    private final int writtenIndices;

    private int readPointer = 0;
    private int writePointer = 0;

    private boolean writeLocked = false;

    /**
     * Create a wrapper for the specified buf
     * @param buf Buf to wrap
     */
    public DeependBuf(final ByteBuf buf) {
        this.buf = buf;
        this.writtenIndices = 0;
    }

    /**
     * Disallow writing to this buf
     */
    public void lock() {
        writeLocked = true;
    }

    /**
     * Create a buf from another buf, and
     * pre-load the specified values
     * @param deependBuf Buf to re-create
     * @param types Values to pre-load
     */
    public DeependBuf(final DeependBuf deependBuf, DataType[] types) {
        this(deependBuf.buf, types);
    }

    /**
     * Wrap a ByteBuf and pre-load
     * the specified values
     * @param buf Buf to wrap
     * @param types Values to pre-load
     */
    public DeependBuf(final ByteBuf buf, DataType[] types) {
        this.dataType = types;
        this.buf = buf;
        this.object = new Object[types.length];

        for (DataType ignored : types) {
            read();
        }

        this.writtenIndices = types.length;
    }

    private void read() {
        int p = writePointer++;

        Object o = null;
        DataType type = dataType[p];

        switch (type) {
            case BYTE:
                o = readByte();
                break;
            case STRING: {
                o = readString();
                break;
            }
            case INT:
                o = readByte();
                break;
            default:
                break;
        }

        object[p] = o;
    }

    public GenericResponse getResponse() {
        return GenericResponse.getGenericResponse(buf.readByte());
    }

    public void writeByte(ByteProvider response) {
        checkLock();

        writeByte(response.getByte());
    }

    public void writeByte(byte b) {
        checkLock();

        buf.writeByte(b);
    }

    public void writeString(String str) {
        checkLock();

        byte[] bytes = str.getBytes();
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);
    }

    private String readString() {
        int lenght = buf.readInt();
        byte[] bytes = new byte[lenght];
        for (int i = 0; i < lenght; i++) {
            bytes[i] = buf.readByte();
        }
        return new String(bytes);
    }

    public String getString() {
        Entry entry = get();
        if (entry == null) {
            return readString();
        }
        return (String) entry.getObject();
    }

    private byte readByte() {
        return buf.readByte();
    }

    public byte getByte() {
        Entry entry = get();
        if (entry == null) {
            return readByte();
        }
        return (byte) entry.object;
    }

    private int readInt() {
        return buf.readInt();
    }

    public int getInt() {
        Entry entry = get();
        if (entry == null) {
            return readInt();
        }
        return (int) entry.getObject();
    }

    public Entry get() {
        int p = readPointer++;

        if (p >= writtenIndices) {
            return null;
        }

        return new Entry(dataType[p], object[p]);
    }

    public void writeInt(int i) {
        checkLock();
        buf.writeInt(i);
    }

    private void checkLock() {
        if (writeLocked) {
            throw new IllegalAccessError("Cannot write to locked buf");
        }
    }

    public boolean readable() {
        return buf.isReadable();
    }

    public void writeAll(DeependBuf in) {
        checkLock();

        buf.writeBytes(in.buf);
    }

    public void reset() {
        buf.resetReaderIndex();
        buf.resetWriterIndex();
    }

    public void copyTo(DeependBuf mirrorBuf) {
        copyTo(mirrorBuf.buf);
    }

    private class Entry {
        DataType type;
        Object object;

        Entry(DataType type, Object object) {
            this.type = type;
            this.object = object;
        }

        public DataType getType() {
            return this.type;
        }

        public Object getObject() {
            return this.object;
        }
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
