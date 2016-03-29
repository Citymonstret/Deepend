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
import lombok.Getter;
import lombok.SneakyThrows;

/**
 * This is a request which will be kept in the
 * request buffer, until the client can send it
 *
 * @author Citymonstret
 */
@Stable
public abstract class PendingRequest extends Request {

    @Getter
    private Channel requestedChannel;

    /**
     * Constructor which allows for UUID specification,
     * should be used for all purposes other than authentication
     * @param requestedChannel Channel the
     *                         request will be sent to
     * @param provider UUID provider (used to authenticate the request)
     */
    @SneakyThrows(IllegalArgumentException.class)
    public PendingRequest(Channel requestedChannel) {
        this.requestedChannel = requestedChannel;
    }

    @Override
    public boolean handle(DeependContext context, ChannelHandler handler) {
        boolean status;
        try {
            this.send(context, handler);
            status = true;
        } catch (final Exception e) {
            Logger.get().error("Something went wrong when handing the pending request", e);
            status = false;
        }
        return status;
    }

    /**
     * This is used to populate the buf
     * with the data needed to make the request
     * @param buf Buf to populate
     */
    protected abstract void makeRequest(DeependBuf buf);

    /**
     * This will send the request to the
     * specified future
     * @see #makeRequest(DeependBuf) to populate the buf
     * @param future Future to send the request to
     */
     public void send(DeependContext context, ChannelHandler handler) {
        DeependBuf out = new NativeBuf();
        out.writeInt(requestedChannel.getValue());
        // Will send the UUID if it is specified
        if (requestedChannel != Channel.AUTHENTICATE) {
            out.writeString(context.getConnection().getUUID().toString());
        }
        makeRequest(out);
        out.writeAndFlush(context);
    }

}
