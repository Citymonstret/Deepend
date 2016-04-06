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

import com.minecade.deepend.channels.ChannelHandler;
import com.minecade.deepend.logging.Logger;
import com.minecade.deepend.nativeprot.NativeBuf;
import com.minecade.deepend.pipeline.*;
import com.minecade.deepend.prot.ProtocolDecoder;
import com.minecade.deepend.prot.ProtocolEncoder;

import lombok.RequiredArgsConstructor;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;


/**
 * This is just a basic netty channel initializer
 * that will initialize the channels using a universal
 * set of options
 *
 * @author Citymonstret
 */
@RequiredArgsConstructor
public final class DeependChannelInitializer {

    private static final ProtocolEncoder encoder = new ProtocolEncoder();
    private static final ProtocolDecoder decoder = new ProtocolDecoder();

    private final ChannelHandler handler;

    /* public void handle(DeependContext context) throws IOException {
        boolean allowed = true;
        InetSocketAddress remoteAddress = context.getAddress();

        if (DeependMeta.hasMeta("client")) {
            allowed = remoteAddress.getHostName().equals(DeependMeta.getMeta("serverAddr"))
                    && ("" + remoteAddress.getPort()).equals(DeependMeta.getMeta("serverPort"));
        }
        if (!allowed) {
            Logger.get().debug("Dropping channel attempt from: " + remoteAddress.getHostName());
            context.getSocket().close();
        } else {
            ByteArrayOutputStream bufferStream = new ByteArrayOutputStream();
            InputStream stream = context.getSocket().getInputStream();
            int nRead;
            byte[] data = new byte[1024 * 64];
            while ((nRead = stream.read(data, 0, data.length)) != -1) {
                bufferStream.write(data, 0, nRead);
            }
            bufferStream.flush();
            byte[] bytes = bufferStream.toByteArray();
            bufferStream.close();

            ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
            buffer.put(bytes);

            NativeBuf in;
            try {
                in = decoder.decode(buffer);
            } catch (final Exception e) {
                Logger.get().error("Failed to extract the DeependBuf", e);
                context.getSocket().close();
                return;
            }

            // Remove the buffer
            // as it isn't needed anymore
            buffer.clear();

            NativeBuf out = new NativeBuf();
            try {
                handler.handle(in, out, context);
            } catch (final Exception e) {
                Logger.get().error("Failed to handle channel", e);
                context.getSocket().close();
                return;
            }

            byte[] output = encoder.encode(out);
            OutputStream outputStream = context.getSocket().getOutputStream();
            outputStream.write(output);
            outputStream.flush();

            context.getSocket().close();
        }
    } */

}
