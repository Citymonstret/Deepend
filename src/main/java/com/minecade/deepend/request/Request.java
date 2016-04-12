package com.minecade.deepend.request;

import com.minecade.deepend.channels.ChannelHandler;
import com.minecade.deepend.pipeline.DeependContext;
import lombok.NonNull;

public abstract class Request implements Comparable<Request> {

    public long internalID = -1;

    public abstract boolean handle(DeependContext context, ChannelHandler handler);

    /**
     * Override this to get more
     * control over the lifespan
     * of your requests
     * @return True, unless overridden
     */
    public boolean validate() {
        return true;
    }

    public int compareTo(@NonNull final Request r2) {
        if (internalID < r2.internalID) {
            return -1;
        }
        if (internalID > r2.internalID) {
            return 1;
        }
        return 0;
    }
}
