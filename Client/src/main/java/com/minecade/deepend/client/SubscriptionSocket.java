package com.minecade.deepend.client;

import com.minecade.deepend.channels.ChannelHandler;
import com.minecade.deepend.logging.Logger;
import com.minecade.deepend.nativeprot.NativeBuf;
import com.minecade.deepend.prot.ProtocolDecoder;
import com.minecade.deepend.util.Constants;
import lombok.Synchronized;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

class SubscriptionSocket extends Thread {

    private static final int SERVER_SOCKET_PORT = 4345; /* TODO: Make configurable */

    private final ServerSocket socket;
    private final ChannelHandler handler;
    private final String host;

    SubscriptionSocket(final ChannelHandler handler, String host) {
        try {
            this.socket = new ServerSocket(SERVER_SOCKET_PORT);
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize the client server socket", e);
        }
        this.handler = handler;
        this.host = host;
        this.setName("SubscriptionManager");
        this.start();
    }

    @Override
    public void run() {
        Logger.get().info("Waiting for @subscription requests");

        Socket in;
        try {
            in = socket.accept();
        } catch (final Exception e) {
            Logger.get().error("Failed to accept incoming socket | Shutting down", e);
            this.close();
            return;
        }

        Logger.get().info("Got a subscription message from the server!");

        InetSocketAddress remoteAddress = (InetSocketAddress) in.getRemoteSocketAddress();
        if (!remoteAddress.getHostString().equals(this.host)) {
            Logger.get().error("Illegal host name @subscription: " + remoteAddress.getHostString());
        } else {
            scope:
            {
                final InputStream stream;
                try {
                    stream = in.getInputStream();
                } catch (final Exception e) {
                    Logger.get().error("Failed to fetch InputStream", e);
                    break scope;
                }

                byte[] data = new byte[Constants.MEGABYTE];

                int nRead;
                try {
                    nRead = stream.read(data, 0, data.length);
                } catch (final Exception e) {
                    Logger.get().error("Failed to read data from socket", e);
                    break scope;
                }

                if (nRead == -1) {
                    Logger.get().error("Stream returned no bytes");
                    break scope;
                }

                final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byteArrayOutputStream.write(data, 0, nRead);

                try {
                    byteArrayOutputStream.flush();
                } catch (final Exception e) {
                    Logger.get().error("Failed to flush to buffer", e);
                    return;
                }

                byte[] readBytes = byteArrayOutputStream.toByteArray();
                if (readBytes.length > 4) {
                    NativeBuf inputBuf;
                    try {
                        inputBuf = ProtocolDecoder.decoder.decode(readBytes);
                    } catch (final Exception e) {
                        Logger.get().error("Failed to extract NativeBuf", e);
                        break scope;
                    }
                    handler.handle(inputBuf, null, null);
                }
            }
        }

        try {
            in.close();
        } catch (final Exception e) {
            Logger.get().error("Failed to close socket", e);
        }

        if (!shutdown) {
            if (!socket.isClosed()) {
                this.run();
            }
        }
    }

    private volatile boolean shutdown = false;

    @Synchronized
    private void close() {
        this.shutdown = true;
        try {
            this.socket.close();
            Logger.get().info("Shutdown subscription socket");
        } catch (IOException e) {
            Logger.get().error("Failed to close the subscription socket", e);
        }
    }
}
