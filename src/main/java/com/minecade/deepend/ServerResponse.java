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

package com.minecade.deepend;

import com.minecade.deepend.bytes.ByteProvider;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * Response codes that the
 * server may return
 *
 * @author Citymonstret
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public enum ServerResponse implements ByteProvider {

    /**
     * The server was unable
     * to return a proper code
     * OR
     * version mismatch
     */
    UNKNOWN(getNextByte()),

    /**
     * The client must authenticate
     * before doing anything
     */
    REQUIRES_AUTHENTICATION(getNextByte()),

    /**
     * The server tried to authenticate
     * the client
     */
    AUTHENTICATION_ATTEMPTED(getNextByte()),

    /**
     * The client has already been
     * authenticated
     */
    ALREADY_AUTHENTICATED(getNextByte()),

    /**
     * Everything went well
     */
    SUCCESS(getNextByte()),

    /**
     * The client provided an invalid
     * UUID
     */
    INVALID_UUID(getNextByte()),

    /**
     * The channel couldn't generate
     * the data
     */
    CHANNEL_EXCEPTION(getNextByte()),

    /**
     * The client requested a channel
     * which isn't present on the
     * server
     */
    INVALID_CHANNEL(getNextByte());

    private final byte responseCode;

    @Override
    public byte getByte() {
        return this.responseCode;
    }

    private static byte b = 0;

    private static byte getNextByte() {
        return b++;
    }

    private static Map<Byte, ServerResponse> cache;

    static {
        cache = new HashMap<>();
        for (ServerResponse response : values()) {
            cache.put(response.getByte(), response);
        }
    }

    /**
     * Get the response for a byte
     * @param b Byte
     * @return Response | UNKNOWN if not found
     */
    public static ServerResponse getServerResponse(byte b) {
        if (!cache.containsKey(b)) {
            return UNKNOWN;
        }
        return cache.get(b);
    }
}
