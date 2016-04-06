package com.minecade.deepend.client;

import com.minecade.deepend.logging.Logger;
import com.minecade.deepend.request.Request;
import com.minecade.deepend.request.ShutdownRequest;
import com.minecade.deepend.util.Assert;
import lombok.Synchronized;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created 3/25/2016 for Deepend
 *
 * @author Citymonstret
 */
class RequestCloud {

    private final Map<Long, Request> requestMap;
    private long requestID = 0L;
    private boolean shutdown = false;

    RequestCloud() {
        this.requestMap = new ConcurrentHashMap<>();
    }

    @Synchronized
    void addPendingRequest(final Request request) {
        Assert.notNull(request);

        //
        // This is to add a global hook for shutdowns
        //
        if (request instanceof ShutdownRequest) {
            shutdown = true;
        }

        //
        // Refuse requests when pending shutdown
        //
        if (shutdown) {
            Logger.get().error("Trying to add request after shutdown has been requested");
            return;
        }

        //
        // This re-forces priority for failed requests
        //
        if (request.internalID == -1) {
            add(requestID++, request);
        } else {
            add(request.internalID, request);
        }
    }

    private void add(final long id, final Request request) {
        this.requestMap.put(id, request);
    }

    @Synchronized
    private long loop() {
        for (long i = 0; i < requestID; i++) {
            if (requestMap.containsKey(i)) {
                return i;
            }
        }
        return -1;
    }

    @Synchronized
    public Request get() {
        if (requestMap.isEmpty() && shutdown) {
            DeependClient.getInstance().shutdown = true;
            return null;
        }

        long index = loop();

        if (index == -1) {
            return null;
        }

        Request request = requestMap.remove(index);
        request.internalID = index;
        return request;
    }
}
