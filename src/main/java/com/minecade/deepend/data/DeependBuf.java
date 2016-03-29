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

import com.minecade.deepend.bytes.ByteProvider;
import com.minecade.deepend.object.GenericResponse;
import com.minecade.deepend.pipeline.DeependContext;

/**
 * A wrapper for the netty ByteBuf
 *
 * This allows for some magnificent
 * things, and this is what the whole
 * project is built to be using
 */
public abstract class DeependBuf {

    protected DataType[] dataType;
    protected Object[] object;

    private int readPointer = 0;
    private int writePointer = 0;

    protected int writtenIndices = 0;

    private boolean writeLocked = false;

    protected DeependBuf() {
    }

    /**
     * Disallow writing to this buf
     */
    public void lock() {
        writeLocked = true;
    }

    protected void read() {
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
        return GenericResponse.getGenericResponse(readByte());
    }

    public void writeByte(ByteProvider response) {
        checkLock();

        writeByte(response.getValue());
    }

    final public void writeByte(byte b) {
        checkLock();
        _writeByte(b);
    }
    protected abstract void _writeByte(byte b);

    final public void writeString(String str) {
        checkLock();
        _writeString(str);
    }
    protected abstract void _writeString(String str);

    protected abstract String readString();

    public String getString() {
        Entry entry = get();
        if (entry == null) {
            return readString();
        }
        return (String) entry.getObject();
    }

    protected abstract byte readByte();

    public byte getByte() {
        Entry entry = get();
        if (entry == null) {
            return readByte();
        }
        return (byte) entry.object;
    }

    protected abstract int readInt();

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
        _writeInt(i);
    }

    protected abstract void _writeInt(int n);

    protected void checkLock() {
        if (writeLocked) {
            throw new IllegalAccessError("Cannot write to locked buf");
        }
    }

    public abstract boolean readable();

    public void writeAll(DeependBuf in) {
        checkLock();
    }

    public abstract void reset();

    public abstract void writeAndFlush(DeependContext context);

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

}
