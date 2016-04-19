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

package com.minecade.deepend.channels;

import com.minecade.deepend.values.NumberProvider;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * These are the channels that
 * are supported by the default protocol
 *
 * <a>https://github.com/DeependProject/Deepend/wiki/Channels</a>
 */
@RequiredArgsConstructor
public enum Channel implements NumberProvider<Integer> {

    /**
     * Authentication channel
     *
     * <a>https://github.com/DeependProject/Deepend/wiki/AUTHENTICATION</a>
     */
    AUTHENTICATE(1),

    /**
     * Used to check listen for
     * quality
     *
     * <a>https://github.com/DeependProject/Deepend/wiki/CHECK_DATA</a>
     */
    CHECK_DATA(1 << 1),

    /**
     * Add data, or update data on the server
     *
     * <a>https://github.com/DeependProject/Deepend/wiki/ADD_DATA</a>
     */
    ADD_DATA(1 << 2),

    /**
     * Get data from the server
     *
     * <a>https://github.com/DeependProject/Deepend/wiki/GET_DATA</a>
     */
    GET_DATA(1 << 3),

    /**
     * Remove data from the server
     *
     * <a>https://github.com/DeependProject/Deepend/wiki/REMOVE_DATA</a>
     */
    REMOVE_DATA(1 << 4),

    /**
     * Unknown
     */
    UNKNOWN(1 << 5);

    @Getter
    private final Integer value;

    @Override
    public String getIdentifier() {
        return this.name();
    }

    //
    // STATIC STUFF
    //

    private static final Map<Integer, Channel> cache = new HashMap<>();

    static {
        for (final Channel channel : values()) {
            cache.put(channel.getValue(), channel);
        }
    }

    /**
     * Get the channel based on its ID
     * @param id ID
     * @return Channel | UNKNOWN
     */
    public static Channel getChannel(final int id) {
        if (!cache.containsKey(id)) {
            return UNKNOWN;
        }
        return cache.get(id);
    }
}
