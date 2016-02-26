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
import lombok.NonNull;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The factory responsible for
 * keeping track of connections,
 * and matching connections to
 * addresses and UUIDs
 *
 * @author Citymonstret
 */
public class ConnectionFactory {

    public static final ConnectionFactory instance = new ConnectionFactory();

    private Map<String, DeependConnection> internalMap;

    ConnectionFactory() {
        this.internalMap = new ConcurrentHashMap<>();
    }

    /**
     * Get the connection for a simple address
     * @param socketAddress Simple Address
     * @return Connection | Null
     */
    @Stable
    final public DeependConnection getConnection(final @NonNull SimpleAddress socketAddress) {
        if (!internalMap.containsKey(socketAddress.toString())) {
            return null;
        }
        return internalMap.get(socketAddress.toString());
    }

    /**
     * Add a connection
     * @param connection Connection
     */
    @Stable
    final public void addConnection(final @NonNull DeependConnection connection) {
        this.internalMap.put(connection.getRemoteAddress().toString(), connection);
    }

    /**
     * Create and add a connection
     * @param remoteAddress Socket Address
     * @return Registered connection
     */
    @Stable
    final public DeependConnection createConnection(final @NonNull InetSocketAddress remoteAddress) {
        DeependConnection connection = new DeependConnection(new SimpleAddress(remoteAddress.getHostName()));
        addConnection(connection);
        return connection;
    }

    /**
     * Get the connection if it exists,
     * otherwise it will be created
     *
     * @param socketAddress Socket Address
     * @param uuid Authentication UUID
     *
     * @return Created, or re-used connection
     */
    @Beta
    final public DeependConnection getOrCreate(final @NonNull SocketAddress socketAddress, UUID uuid) {
        InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;
        SimpleAddress simpleAddress = new SimpleAddress(inetSocketAddress.getHostName());
        simpleAddress.setUUID(uuid.toString());
        if (!internalMap.containsKey(simpleAddress.toString())) {
            return createConnection(inetSocketAddress);
        }
        return getConnection(simpleAddress);
    }
}
