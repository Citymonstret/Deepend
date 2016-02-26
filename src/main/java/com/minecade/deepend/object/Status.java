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

    final public boolean fetchUpdate(SimpleAddress address) {
        if (needsUpdate(address)) {
            setUpdated(address);
            return true;
        }
        return false;
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
