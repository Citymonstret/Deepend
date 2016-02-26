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
import com.minecade.deepend.data.DeependBuf;
import io.netty.channel.ChannelFuture;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.SneakyThrows;

/**
 * This is a request which will be kept in the
 * request buffer, until the client can send it
 *
 * @author Citymonstret
 */
@EqualsAndHashCode
public abstract class PendingRequest implements Comparable {

    @Getter
    private Channel requestedChannel;

    @Getter
    private UUIDProvider provider;

    /**
     * Constructor that will default the UUID to null,
     * should only be used for authentication
     * @param requestedChannel Channel the
     *                         request will be sent to
     */
    public PendingRequest(Channel requestedChannel) {
        this(requestedChannel, () -> null);
    }

    /**
     * Constructor which allows for UUID specification,
     * should be used for all purposes other than authentication
     * @param requestedChannel Channel the
     *                         request will be sent to
     * @param provider UUID provider (used to authenticate the request)
     */
    @SneakyThrows(IllegalArgumentException.class)
    public PendingRequest(Channel requestedChannel, UUIDProvider provider) {
        this.requestedChannel = requestedChannel;
        this.provider = provider;

        if (provider == null && requestedChannel != Channel.AUTHENTICATE) {
            throw new IllegalArgumentException("Cannot specify null UUID for channels other than for authentication");
        }
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
    final public void send(ChannelFuture future) {
        DeependBuf out = new DeependBuf(future.channel().alloc().buffer());
        out.writeInt(requestedChannel.getValue());
        // Will send the UUID if it is specified
        if (provider.getUUID() != null) {
            out.writeString(provider.getUUID().toString());
        }
        makeRequest(out);
        out.writeAndFlush(future);
    }

    @Override
    public int compareTo(Object o) {
        return ((o instanceof PendingRequest) && o.equals(this)) ? 0 : -1;
    }

    /**
     * Override this to get more
     * control over the lifespan
     * of your requests
     * @return True, unless overridden
     */
    public boolean validate() {
        return true;
    }
}
