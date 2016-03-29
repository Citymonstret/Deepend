package com.minecade.deepend.request;

import com.minecade.deepend.channels.ChannelHandler;
import com.minecade.deepend.logging.Logger;
import com.minecade.deepend.pipeline.DeependContext;

import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class RequestChain extends Request {

    private final PriorityQueue<DataRequest> requests;
    private final AtomicInteger requestCount;
    private Request lastRequest;

    private long idPool = 1L;

    {
        requests = new PriorityQueue<>((o1, o2) -> {
            if (o1.internalID < o2.internalID) {
                return -1;
            } else if (o1.internalID > o2.internalID) {
                return 1;
            }
            return 0;
        });
        requestCount = new AtomicInteger(0);
    }

    public RequestChain add(DataRequest request) {
        this.requests.add(request);
        this.requestCount.incrementAndGet();
        return this;
    }

    @Override
    public boolean handle(DeependContext context, ChannelHandler handler) {
        int lastNum = requestCount.get();
        if (lastNum == 0) {
            return true;
        }
        DataRequest request;
        while ((request = requests.poll()) != null) {
            request.addRecipient(data -> {
                requestCount.decrementAndGet();
                Logger.get().debug("Finished request");
            });
            if (!request.handle(context, handler)) {
                Logger.get().error("Something went very wrong :///");
            }
            while (lastNum == requestCount.get()) {
                // Wait
            }
        }
        Logger.get().info("Done!");
        if (lastRequest != null) {
            lastRequest.handle(context, handler);
        }
        return true;
    }

    public void addLast(ShutdownRequest shutdownRequest) {
        this.lastRequest = shutdownRequest;
    }
}
