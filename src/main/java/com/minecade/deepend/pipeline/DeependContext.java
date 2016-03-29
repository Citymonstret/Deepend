package com.minecade.deepend.pipeline;

import com.minecade.deepend.connection.DeependConnection;
import com.minecade.deepend.data.DeependBuf;
import lombok.Getter;
import lombok.Setter;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class DeependContext {

    @Getter
    @Setter
    private DeependConnection connection;

    @Getter
    private final InetSocketAddress address;

    @Getter
    @Setter
    private DeependBuf deependBuf;

    @Getter
    @Setter
    private byte[] bytes;

    @Getter
    private Socket socket;

    private Map<String, Object> meta;

    public DeependContext(final DeependConnection connection, final Socket socket, final InetSocketAddress address) {
        this.connection = connection;
        this.address = address;
        this.socket = socket;
        this.meta = new HashMap<>();
    }

    public <T> T getMeta(String key) {
        return (T) meta.get(key);
    }

    public boolean hasMeta(String key) {
        return meta.containsKey(key);
    }

    public void setMeta(String key, Object object) {
        this.meta.put(key, object);
    }
}
