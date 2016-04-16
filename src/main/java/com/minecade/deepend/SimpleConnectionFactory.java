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

public class SimpleConnectionFactory implements ConnectionFactory {

    private final Map<String, DeependConnection> internalMap = new ConcurrentHashMap<>();

    /**
     * Get the connection for a simple address
     * @param socketAddress Simple Address
     * @return Connection | Null
     */
    @Stable
    @Override
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
    @Override
    final public DeependConnection addConnection(final @NonNull DeependConnection connection) {
        this.internalMap.put(connection.getRemoteAddress().toString(), connection);
        return connection;
    }

    /**
     * Create and add a connection
     * @param remoteAddress Socket Address
     * @return Registered connection
     */
    @Stable
    @Override
    final public DeependConnection createConnection(final @NonNull InetSocketAddress remoteAddress) {
        return addConnection(new DeependConnection(new SimpleAddress(remoteAddress.getHostName())));
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
    @Override
    final public DeependConnection getOrCreate(final @NonNull SocketAddress socketAddress, final @NonNull UUID uuid) {
        InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;
        SimpleAddress simpleAddress = new SimpleAddress(inetSocketAddress.getHostName());
        if (!internalMap.containsKey(simpleAddress.toString())) {
            return createConnection(inetSocketAddress);
        }
        return getConnection(simpleAddress);
    }

}
