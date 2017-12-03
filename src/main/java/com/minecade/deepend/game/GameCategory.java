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

import com.minecade.deepend.bytes.ByteProvider;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public enum GameCategory implements ByteProvider
{

    UNKNOWN( (byte) 1 ),                      // Unknown category type
    PLAYERS( (byte) ( 1 << 1 ) ),               // Game players
    SERVERS( (byte) ( 1 << 2 ) ),               // Game servers
    PLAYER_SERVERS( (byte) ( 1 << 3 ) ),        // Player -> Server relations
    SERVER_PLAYERS( (byte) ( 1 << 4 ) ),        // Players on a server
    PLAYER_NAMES( (byte) ( 1 << 6 ) ),
    SERVER_CATEGORIES( (byte) ( 1 << 5 ) ),               // Game proxies (such as bungee)
    CATEGORY_SERVERS( (byte) ( 1 << 7 ) );

    private final byte categoryID;

    @Override
    public Byte getValue()
    {
        return this.categoryID;
    }

    @Override
    public String getIdentifier()
    {
        return this.name();
    }
}
