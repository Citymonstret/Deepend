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
public final class StatusRequest extends PendingRequest {

    private static Map<String, StatusRequest> requestMap = new ConcurrentHashMap<>();

    /**
     * Get a request based on its ID
     * @param s Request ID
     * @return Request
     */
    public static StatusRequest getRequest(String s) {
        return requestMap.get(s);
    }

    private static volatile int currentIndex = 0;

    @Getter
    private final int index;

    @Getter
    private final int field;

    @Getter
    private final StatusRecipient recipient;

    public StatusRequest(int field, @NonNull StatusRecipient statusRecipient, UUIDProvider provider) {
        super(Channel.CHECK_DATA, provider);
        this.index = currentIndex++;
        this.field = field;
        this.recipient = statusRecipient;
        requestMap.put("request::" + index, this);
    }

    public void call(final int field) {
        recipient.act(field);
    }

    @Override
    final protected void makeRequest(DeependBuf buf) {
        buf.writeString("request::" + index);
        buf.writeInt(getField());
    }

    /**
     * This will delete the request from the request map
     */
    public void delete() {
        requestMap.remove("request::" + index);
    }

    /**
     * This will act on a list
     * of returned items
     */
    @FunctionalInterface
    public interface StatusRecipient {
        void act(int field);
    }
}
