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

import com.minecade.deepend.bytes.ByteProvider;
import com.minecade.deepend.lib.Stable;
import com.minecade.deepend.logging.Logger;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * Response class that can be re-used for
 * plenty different purposes
 *
 * @author Citymonstret
 */
@Stable
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public enum GenericResponse implements ByteProvider {

    /**
     * Something succeeded
     */
    SUCCESS(getByte(0x00)),

    /**
     * Something failed
     */
    FAILURE(getByte(0x01));

    private final byte b;

    @Override
    public Byte getValue() {
        return this.b;
    }

    private static byte getByte(final int i) {
        return new Integer(i).byteValue();
    }

    private static final Map<Byte, GenericResponse> cache = new HashMap<>();

    static {
        for (final GenericResponse genericResponse : values()) {
            cache.put(genericResponse.getValue(), genericResponse);
        }
    }

    /**
     * Get a GenericResponse from a byte value
     *
     * @param b Byte to parse
     * @return value
     */
    public static GenericResponse getGenericResponse(byte b) {
        if (!cache.containsKey(b)) {
            Logger.get().error("Reading invalid GenericResponse value, spoofing ;/");
            return FAILURE;
        }
        return cache.get(b);
    }
}
