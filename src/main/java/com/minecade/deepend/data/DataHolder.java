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

package com.minecade.deepend.data;

import com.minecade.deepend.bytes.ByteProvider;
import lombok.*;

import java.util.*;
import java.util.function.BiConsumer;

/**
 * This is basically a map, but smarter
 *
 * @author Citymonstret
 */
@RequiredArgsConstructor
public class DataHolder implements Map<String, Object> {

    @Setter
    private DataHolder parent;

    /**
     * Delete this holder from the
     * parent holder
     */
    public void delete() {
        // This will basically
        // remove this from
        // everywhere, slightly
        // wasteful, but oh well
        if (parent != null) {
            parent.remove(this.getIdentifier());
        }
    }

    public void register(ByteProvider provider) {
        DataManager.instance.getDataHolder(provider)
                .put(this.getIdentifier(), this);
    }

    /**
     * A simple listener interface,
     * which will listen to updates
     * of the parent holder
     *
     * @author Citymonstret
     */
    public interface DataListener {

        /**
         * This is called whenever
         * the parent is updates,
         * and means that the
         * child implementation
         * should update its values
         */
        void requestSync();
    }

    @NonNull private final Map<String, Object> data = new HashMap<>();

    @Getter
    @NonNull
    private final String identifier;
    @NonNull private final List<DataListener> listeners = new ArrayList<>();

    @Setter
    private String fallback;

    /**
     * Get the fallback value, if
     * declared. Otherwise it will
     * return a new data object
     * where everything is set to
     * return the identifier of the holder
     *
     * @return Fallback value
     */
    public Object getFallback() {
        if (this.fallback == null) {
            return new DataObject(this.identifier, this.identifier);
        }
        return new DataObject(this.identifier, ((DataObject) this.get(fallback)).getValue());
    }

    /**
     * Add a data listener, which will receive
     * update notifications for this holder
     * @param listener Listener
     */
    public void addListener(final DataListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Called whenever the values of
     * this holder are updated
     */
    protected void pushSync() {
        this.listeners.forEach(DataListener::requestSync);
    }

    @Override
    final public String toString() {
        return this.getIdentifier();
    }

    @Override
    public int size() {
        return this.data.size();
    }

    @Override
    public boolean isEmpty() {
        return this.data.isEmpty();
    }

    @Override
    public Object get(Object key) {
        if (!this.data.containsKey(key.toString())) {
            return null;
        }
        return this.data.get(key);
    }

    @Override
    public boolean containsKey(Object key) {
        return get(key) != null;
    }

    @Override
    public Object put(String key, Object value) {
        if (value == null) {
            value = getIdentifier();
        }

        // Automatically wrap strings to
        // data objects, for convenience
        if (value instanceof String) {
            value = new DataObject(key, value.toString());
        }

        // Auto-register holders for deletion
        // purposes
        if (value instanceof DataObject) {
            ((DataObject) value).setHolder(this);
        }

        // Will just add the values to any
        // pre-existent data holder, if the
        // data holder is already registered
        if (value instanceof DataHolder) {
            DataHolder holder = (DataHolder) value;

            // This is a bit hackish
            if (containsKey(holder.getIdentifier())) {
                DataHolder oldHolder = (DataHolder) get(holder.getIdentifier());
                oldHolder.putAll(holder);
                this.pushSync();
                return holder;
            }

            holder.setParent(this);
        }

        Object o = this.data.put(key, value);

        // Push changes to listeners
        this.pushSync();

        return o;
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        // Make sure that it goes
        // through all filters
        m.forEach(this::put);
        // this.data.putAll(m);
        this.pushSync();
    }

    @Override
    public Object remove(Object key) {
        Object o = this.data.remove(key);
        pushSync();
        return o;
    }

    @Override
    public boolean containsValue(Object value) {
        return this.data.containsValue(value);
    }

    @Override
    public void clear() {
        this.data.clear();
        this.pushSync();
    }

    @Override
    public Set<String> keySet() {
        Set<String> keySet = new HashSet<>();
        this.forEach((k,v) -> keySet.add(k));
        return keySet;
    }

    @Override
    public Collection<Object> values() {
        Set<Object> values = new HashSet<>();
        this.forEach((k,v) -> values.add(v));
        return values;
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        Set<Entry<String,Object>> entrySet = new HashSet<>();
        Set<Entry<String,Object>> superSet = this.data.entrySet();
        superSet.forEach((e) -> {
            if (containsKey(e.getKey())) {
                entrySet.add(e);
            }
        });
        return entrySet;
    }

    @Override
    public Object getOrDefault(Object key, Object defaultValue) {
        if (containsKey(key)) {
            return get(key);
        }
        return defaultValue;
    }

    @Override
    public Object putIfAbsent(String key, Object value) {
        if (containsKey(key)) {
            return null;
        }
        return put(key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        boolean b = this.data.remove(key, value);
        pushSync();
        return b;
    }

    @Override
    public boolean replace(String key, Object oldValue, Object newValue) {
        return this.data.replace(key, oldValue, newValue);
    }

    @Override
    public Object replace(String key, Object value) {
        return this.data.replace(key, value);
    }

    @Override
    public void forEach(BiConsumer<? super String, ? super Object> action) {
        // This will make sure that
        // data gets deleted, when
        // it's supposed to, etc
        this.data.keySet().forEach(
                key -> {
                    if (key == null) {
                        return;
                    }
                    Object value = get(key);
                    if (value == null) {
                        return;
                    }
                    action.accept(key, value);
                }
        );
    }

    @Builder
    public static class DataHolderInitalizer {

        final private String name;
        private String fallback;
        private DataHolder parent;
        @Singular("object") private Map<String, Object> objects;

        public DataHolder getDataHolder() {
            DataHolder holder = new DataHolder(name);
            if (fallback != null) {
                holder.setFallback(fallback);
            }
            if (parent != null) {
                holder.setParent(parent);
            }
            holder.putAll(objects);
            return holder;
        }

        public void register(ByteProvider provider) {
            getDataHolder().register(provider);
        }
    }
}
