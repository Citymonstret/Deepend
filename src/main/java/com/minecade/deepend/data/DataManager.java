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
import lombok.NonNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The manager which handles
 * all data holders
 *
 * @author Citymonstret
 */
public class DataManager {

    /**
     * The only instance of this
     */
    public static final DataManager instance = new DataManager();

    private final DataHolder mainDataHolder = new DataHolder("data");

    private final Map<ByteProvider, DataStatus> dataStatusMap;

    DataManager() {
        this.dataStatusMap = new ConcurrentHashMap<>();
    }

    public static void createDataHolder(ByteProvider provider) {
        instance.registerDataHolder(new DataHolder(provider.getIdentifier()), provider);
    }

    public static void createDataHolders(ByteProvider ... providers) {
        for (ByteProvider provider : providers) {
            createDataHolder(provider);
        }
    }

    /**
     * Register a data holder
     * @param dataHolder Holder to register
     */
    public void registerDataHolder(@NonNull final DataHolder dataHolder, @NonNull ByteProvider category) {
        this.mainDataHolder.put(dataHolder.getIdentifier(), dataHolder);
        this.registerDataStatus(category);
    }

    private void registerDataStatus(ByteProvider category) {
        this.dataStatusMap.put(category, new DataStatus(category));
    }

    public DataStatus getDataStatus(ByteProvider category) {
        return this.dataStatusMap.get(category);
    }

    /**
     * Check if the manager has a data holder
     *
     * @param key Holder key
     * @return True | False
     */
    public boolean hasDataHolder(final String key) {
        return this.mainDataHolder.containsKey(key);
    }

    /**
     * Get a data holder
     * @see #hasDataHolder(String) to check if it exists
     * @param key Data holder key
     * @return Holder | New data holder if non-existent
     */
    public DataHolder getDataHolder(@NonNull final Object key) {
        Object data = mainDataHolder.get(key.toString());
        if (data instanceof DataHolder) {
            return (DataHolder) data;
        }
        // Otherwise let's make a dataholder
        DataHolder holder = new DataHolder(key.toString());
        holder.put(key.toString(), data);
        return holder;
    }
}
