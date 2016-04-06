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

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This is a request involving data
 *
 * @see com.minecade.deepend.request.PendingRequest
 *
 * @author Citymonstret
 */
public abstract class DataRequest extends PendingRequest {

    private static Map<String, DataRequest> requestMap = new ConcurrentHashMap<>();

    /**
     * Get a request based on its ID
     * @param s Request ID
     * @return Request
     */
    public static DataRequest getRequest(String s) {
        return requestMap.get(s);
    }

    private static volatile int currentIndex = 0;

    @Getter
    private final int index;

    private String buildID(DataRequest request) {
        return "request::" + request.index;
    }

    private List<DataRecipient> recipient = new ArrayList<>();

    public DataRecipient getRecipient() {
        return recipient.get(0);
    }

    /**
     * Constructor
     * @param channel Channel to send the request to
     * @param dataRecipient Recipient that will handle the data
     */
    public DataRequest(@NonNull Channel channel, @NonNull DataRecipient dataRecipient) {
        super(channel);
        this.index = currentIndex++;
        this.recipient.add(dataRecipient);
        requestMap.put(buildID(this), this);
    }

    public void addRecipient(@NonNull final DataRecipient recipient) {
        this.recipient.add(recipient);
    }

    /**
     * This will send the objects to the
     * recipient, which will then act as
     * per definition
     * @param o List of objects
     */
    public void call(final List<Object> o) {
        recipient.forEach(recipient -> recipient.act(o));
    }

    @Override
    final protected void makeRequest(DeependBuf buf) {
        buf.writeString(buildID(this));
        buildRequest(buf);
    }

    /**
     * Build the request
     * @param buf DeependBuf to populate
     */
    protected void buildRequest(DeependBuf buf){}

    /**
     * This will delete the request from the request map
     */
    public void delete() {
        requestMap.remove(buildID(this));
    }

    /**
     * This will act on a list
     * of returned items
     */
    public interface DataRecipient {

        /**
         * Perform an action on the
         * list of items
         * @param data List of items
         */
        void act(List<Object> data);

    }
}
