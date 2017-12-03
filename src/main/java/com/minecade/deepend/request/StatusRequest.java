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
import lombok.Getter;
import lombok.NonNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created 2/26/2016 for Deepend
 *
 * @author Citymonstret
 */
public final class StatusRequest extends PendingRequest
{

    private static final byte TYPE = 0;

    private static Map<String, StatusRequest> requestMap = new ConcurrentHashMap<>();
    private static volatile int currentIndex = 0;
    @Getter
    private final int index;
    @Getter
    private final int field;
    @Getter
    private final StatusRecipient recipient;

    public StatusRequest(int field, @NonNull StatusRecipient statusRecipient)
    {
        super( Channel.CHECK_DATA );
        this.index = currentIndex++;
        this.field = field;
        this.recipient = statusRecipient;
        requestMap.put( "request::" + index, this );
    }

    /**
     * Get a request based on its ID
     *
     * @param s Request ID
     * @return Request
     */
    public static StatusRequest getRequest(String s)
    {
        return requestMap.get( s );
    }

    public void call(final int field)
    {
        this.recipient.act( field );
        this.delete();
    }

    @Override
    final protected void makeRequest(DeependBuf buf)
    {
        buf.writeByte( TYPE );
        buf.writeString( "request::" + index );
        buf.writeInt( getField() );
    }

    /**
     * This will delete the request from the request map
     */
    public void delete()
    {
        requestMap.remove( "request::" + index );
    }

    /**
     * This will act on a list
     * of returned items
     */
    @FunctionalInterface
    public interface StatusRecipient
    {

        void act(int field);
    }
}
