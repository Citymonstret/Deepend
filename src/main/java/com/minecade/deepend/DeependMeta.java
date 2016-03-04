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
package com.minecade.deepend;

import com.minecade.deepend.lib.Stable;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A very simple globalized meta system
 *
 * @author Citymonstret
 */
@Stable
@UtilityClass
public final class DeependMeta {

    /**
     * The storage for the meta system
     *
     * It's using ConcurrentHashMap as it will be used by loads of
     * different threads
     */
    private static final Map<String, String> map = new ConcurrentHashMap<>();

    /**
     * Check if a key value exists
     *
     * @param key Key to check for
     * @return True if exists | False if it doesn't
     */
    public static boolean hasMeta(@NonNull String key) {
        return map.containsKey(key);
    }

    /**
     * Get a meta value
     *
     * @see #hasMeta(String) To see if exists
     *
     * @param key Value key
     * @return Value
     */
    public static String getMeta(@NonNull String key) {
        return map.get(key);
    }

    /**
     * Set a meta value (add | update)
     *
     * @param key Key to (add | update)
     * @param val Meta value
     */
    public static void setMeta(@NonNull String key, @NonNull String val) {
        map.put(key, val);
    }

    /**
     * Remove a data value
     *
     * @param key Value to remove
     */
    public static void removeMeta(@NonNull String key) {
        map.remove(key);
    }
}
