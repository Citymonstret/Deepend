package com.minecade.deepend.object;

import com.minecade.deepend.connection.SimpleAddress;
import lombok.Getter;

import java.util.*;

/**
 * Created 2/25/2016 for Deepend
 *
 * @author Citymonstret
 */
public abstract class Status<T> {

    private List<String> receivedUpdate;

    {
        receivedUpdate = Collections.synchronizedList(new ArrayList<>());
    }

    @Getter
    private final T t;

    protected Status(T t) {
        this.t = t;
    }

    final public boolean needsUpdate(SimpleAddress address) {
        return !receivedUpdate.contains(address.toString());
    }

    final public void resetStatus() {
        receivedUpdate.clear();
    }

    final public void setUpdated(SimpleAddress address) {
        if (needsUpdate(address)) {
            receivedUpdate.add(address.toString());
        }
    }

    final public Collection<SimpleAddress> getUpdated() {
        Collection<SimpleAddress> collection = new HashSet<>();
        receivedUpdate.forEach(s -> collection.add(SimpleAddress.fromString(s)));
        return collection;
    }
}
