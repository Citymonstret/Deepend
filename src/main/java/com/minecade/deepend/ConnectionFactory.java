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

import com.minecade.deepend.connection.DeependConnection;
import com.minecade.deepend.connection.SimpleAddress;
import com.minecade.deepend.lib.Beta;
import com.minecade.deepend.lib.Stable;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.UUID;

/**
 * The factory responsible for
 * keeping track of connections,
 * and matching connections to
 * addresses and UUIDs
 *
 * @author Citymonstret
 */
public interface ConnectionFactory
{

    /**
     * Get the connection for a simple address
     *
     * @param simpleAddress Simple Address
     * @return Connection | Null
     */
    @Stable
    DeependConnection getConnection(final SimpleAddress simpleAddress);

    /**
     * Add a connection
     *
     * @param connection Connection
     */
    @Stable
    DeependConnection addConnection(final DeependConnection connection);


    /**
     * Create and add a connection
     *
     * @param remoteAddress Socket Address
     * @return Registered connection
     */
    @Stable
    DeependConnection createConnection(final InetSocketAddress remoteAddress);

    /**
     * Get the connection if it exists,
     * otherwise it will be created
     *
     * @param socketAddress Socket Address
     * @param uuid          Authentication UUID
     * @return Created, or re-used connection
     */
    @Beta
    DeependConnection getOrCreate(final SocketAddress socketAddress, final UUID uuid);
}
