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

import com.minecade.deepend.logging.Logger;

import java.util.HashMap;
import java.util.Map;

public enum GenericResponse implements ByteProvider {
    SUCCESS(getByte(0x00)),
    FAILURE(getByte(0x01));

    private final byte b;

    GenericResponse(byte b) {
        this.b = b;
    }

    public byte getByte() {
        return this.b;
    }

    protected static byte getByte(int i) {
        return new Integer(i).byteValue();
    }

    private static Map<Byte, GenericResponse> cache = new HashMap<>();

    static {
        for (GenericResponse genericResponse : values()) {
            cache.put(genericResponse.getByte(), genericResponse);
        }
    }

    public static GenericResponse getGenericResponse(byte b) {
        if (!cache.containsKey(b)) {
            Logger.get().error("Reading invalid GenericResponse value, spoofing ;/");
            return FAILURE;
        }
        return cache.get(b);
    }
}
