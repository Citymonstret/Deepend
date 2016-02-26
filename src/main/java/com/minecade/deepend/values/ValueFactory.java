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

package com.minecade.deepend.values;

import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * This is a link between ByteProvider enums
 * and their byte values.
 *
 * @author Citymonstret
 *
 * @param <E> Enum implementing ByteProvider
 */
public class ValueFactory<E extends Enum<E> & NumberProvider> {

    static boolean locked = false;

    /**
     * Lock the byte factory factorization process
     */
    public static void lock() {
        locked = true;
    }

    /**
     * Factory Types
     */
    public enum FactoryType {

        /**
         * Categories used for
         * data management
         */
        DATA_TYPE
    }

    private static Map<FactoryType, ValueFactory> map = new HashMap<>();

    /**
     * This will add a byte factory for the
     * given factory type
     *
     * @param type What should this factory be used for
     * @param factory Factory for the given type
     *
     * @param <B> Enum implementing ByteProvider
     */
    @SneakyThrows(RuntimeException.class)
    public static <B extends Enum<B> & NumberProvider> void addValueFactory(@NonNull FactoryType type, @NonNull ValueFactory<B> factory) {
        if (locked) {
            throw new RuntimeException("Cannot add factory to locked manager");
        }
        map.put(type, factory);
    }

    /**
     * Get the factory for the given type
     *
     * @param type Factory Type
     * @return Factory for the specified type
     */
    @SneakyThrows(RuntimeException.class)
    public static ValueFactory getFactory(@NonNull FactoryType type) {
        if (!map.containsKey(type)) {
            throw new RuntimeException("No byte factory registered for: " + type.name());
        }
        return map.get(type);
    }

    private final Map<String, Number> cache;
    private final Map<Number, String> rCache;

    @Getter
    private E unknown;

    /**
     * @param enumClass The class of the enumerator
     *                  used to construct this factory
     * @param unknown The enum value that should be
     *                used for unknown values
     */
    public ValueFactory(@NonNull Class<E> enumClass, @NonNull E unknown) {
        this.cache = new HashMap<>();
        this.rCache = new HashMap<>();

        this.unknown = unknown;

        EnumSet.allOf(enumClass).forEach(
                e -> {
                    cache.put(e.name(), e.getValue());
                    rCache.put(e.getValue(), e.name());
                }
        );
    }

    /**
     * Get the name of the enum
     * value for the specified bye
     *
     * @param b Byte
     *
     * @return Name if registered, else
     *         the default value {@see #getUnknown()}
     */
    public String getName(Number b) {
        if (!rCache.containsKey(b)) {
            return unknown.name();
        }
        return rCache.get(b);
    }

    /**
     * Get the byte for the enum name
     *
     * @param key Enum name
     *
     * @return Byte if registered, else
     *         the default value (@see #getUnknown()}
     */
    public Number getNumberValue(String key) {
        if (!cache.containsKey(key)) {
            return unknown.getValue();
        }
        return cache.get(key);
    }
}
