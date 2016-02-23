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

package com.minecade.deepend.object;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class ByteFactory<E extends Enum<E>> {

    static boolean locked = false;

    public static void lock() {
        locked = true;
    }

    public enum FactoryType {
        DATA_TYPE
    }

    private static Map<FactoryType, ByteFactory> map = new HashMap<>();

    public static void addByteFactory(FactoryType type, ByteFactory factory) {
        if (locked) {
            throw new RuntimeException("Cannot add factory to locked manager");
        }
        map.put(type, factory);
    }

    public static ByteFactory getFactory(FactoryType type) {
        return map.get(type);
    }

    private Map<String, Byte> cache;
    private Map<Byte, String> rCache;

    private E unknown;

    public ByteFactory(Class<E> enumClass, E unknown) {
        this.cache = new HashMap<>();
        this.rCache = new HashMap<>();

        this.unknown = unknown;

        EnumSet.allOf(enumClass).forEach(
                e -> {
                    ByteProvider temp = (ByteProvider) e;
                    cache.put(e.name(), temp.getByte());
                    rCache.put(temp.getByte(), e.name());
                }
        );
    }

    public String getName(byte b) {
        if (!rCache.containsKey(b)) {
            return unknown.name();
        }
        return rCache.get(b);
    }

    public byte getByte(String key) {
        if (!cache.containsKey(key)) {
            return ((ByteProvider) unknown).getByte();
        }
        return cache.get(key);
    }
}
