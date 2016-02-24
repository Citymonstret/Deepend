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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Citymonstret
 */
public class DeependMeta {

    private static final Map<String, String> map = new ConcurrentHashMap<>();

    public static boolean hasMeta(String key) {
        return map.containsKey(key);
    }

    public static String getMeta(String key) {
        return map.get(key);
    }

    public static void setMeta(String key, String val) {
        map.put(key, val);
    }

    public static void removeMeta(String key) {
        map.remove(key);
    }

}
