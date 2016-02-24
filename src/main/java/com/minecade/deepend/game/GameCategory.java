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

package com.minecade.deepend.game;

import com.minecade.deepend.object.ByteProvider;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public enum GameCategory implements ByteProvider {

    UNKNOWN((byte) 0x00),               // Unknown category type
    PLAYERS((byte) 0x01),               // Game players
    SERVERS((byte) 0x02),               // Game servers
    PLAYER_SERVERS((byte) 0x04),        // Player -> Server relations
    SERVER_PLAYERS((byte) 0x05),        // Players on a server
    PROXIES((byte) 0x06);               // Game proxies (such as bungee)

    private final byte categoryID;

    @Override
    public byte getByte() {
        return this.categoryID;
    }
}
