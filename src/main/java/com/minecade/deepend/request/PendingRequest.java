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

package com.minecade.deepend.request;

import com.minecade.deepend.channels.Channel;
import com.minecade.deepend.channels.ChannelHandler;
import com.minecade.deepend.data.DeependBuf;
import com.minecade.deepend.lib.Stable;
import com.minecade.deepend.logging.Logger;
import com.minecade.deepend.nativeprot.NativeBuf;
import com.minecade.deepend.pipeline.DeependContext;
import com.minecade.deepend.prot.ProtocolDecoder;
import com.minecade.deepend.util.Constants;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * This is a request which will be kept in the
 * request buffer, until the client can send it
 *
 * @author Citymonstret
 */
@Stable
public abstract class PendingRequest extends Request
{

    @Getter
    private Channel requestedChannel;

    /**
     * Constructor which allows for UUID specification,
     * should be used for all purposes other than authentication
     *
     * @param requestedChannel Channel the
     *                         request will be sent to
     */
    @SneakyThrows(IllegalArgumentException.class)
    public PendingRequest(Channel requestedChannel)
    {
        this.requestedChannel = requestedChannel;
    }

    @Override
    public boolean handle(@NonNull final DeependContext context, @NonNull final ChannelHandler handler)
    {
        boolean status;
        try
        {
            this.send( context, handler );
            status = true;
        } catch ( final Exception e )
        {
            Logger.get().error( "Something went wrong when handing the pending request", e );
            status = false;
        }
        return status;
    }

    /**
     * This is used to populate the buf
     * with the data needed to make the request
     *
     * @param buf Buf to populate
     */
    protected abstract void makeRequest(DeependBuf buf);

    @Stable
    public void send(@NonNull final DeependContext context,
                     @NonNull final ChannelHandler handler)
    {
        // This is our buf for the output
        final DeependBuf out = new NativeBuf();
        // This is the channel ID
        out.writeInt( requestedChannel.getValue() );
        // Now we'll write the request specific data to the buf
        makeRequest( out );
        // This writes the data from the buf to the stream
        out.writeAndFlush( context );
        // This removes all data from the buf
        out.nullify();

        // This makes sure that we have a working
        // ByteArrayOutputStream to pass our data through
        final ByteArrayOutputStream byteArrayOutputStream;
        if ( !context.hasMeta( "bufferStream" ) )
        {
            byteArrayOutputStream = new ByteArrayOutputStream();
            context.setMeta( "bufferStream", byteArrayOutputStream );
        } else
        {
            byteArrayOutputStream = context.getMeta( "bufferStream" );
        }
        byteArrayOutputStream.reset();

        // This is the stream from which we'll read the data
        final InputStream stream;
        try
        {
            stream = context.getSocket().getInputStream();
        } catch ( final IOException e )
        {
            Logger.get()
                    .error( "Failed to read from socket InputStream", e );
            return;
        }

        // A simple 1MB buffer
        final byte[] data = new byte[ Constants.MEGABYTE ];

        // Here we read all available data (up to a megabyte)
        final int nRead;
        try
        {
            nRead = stream.read( data, 0, data.length );
        } catch ( final IOException e )
        {
            Logger.get().error( "Failed to read from stream", e );
            return;
        }

        if ( nRead == -1 )
        {
            Logger.get().error( "Stream returned no bytes" );
            return;
        }

        // Now we write the read data to the buffer
        byteArrayOutputStream.write( data, 0, nRead );
        try
        {
            byteArrayOutputStream.flush();
        } catch ( final IOException e )
        {
            Logger.get().error( "Failed to flush stream", e );
            return;
        }

        // And extract the available bytes
        final byte[] readBytes = byteArrayOutputStream
                .toByteArray();

        // 4 bytes = 1 32bit integer,
        // which is the size indicator for the protocol
        // Any less, and we just get rid of it
        if ( readBytes.length > 4 )
        {
            final NativeBuf inputBuf;
            try
            {
                // Here we turn the raw data into objects
                inputBuf = ProtocolDecoder.decoder
                        .decode( readBytes );
            } catch ( final Exception e )
            {
                Logger.get().error( "Failed to extract NativeBuf", e );
                return;
            }
            // And now we pass the data to the channel handler
            handler.handle( inputBuf, null, context );
        }
    }

}
