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

import com.minecade.deepend.bits.BitField;
import com.minecade.deepend.lib.Beta;
import com.minecade.deepend.object.ProviderGroup;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.util.HashMap;
import java.util.Map;

/**
 * This is a link between ByteProvider enums
 * and their byte values.
 *
 * @author Citymonstret
 */
@Beta
public class ValueFactory<DataType extends Number, Group extends ProviderGroup<DataType, ValueProvider<DataType>>> {

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
     * @param <DataType> Number implementation
     */
    @SneakyThrows(RuntimeException.class)
    public static <DataType extends Number, B extends ProviderGroup<DataType, ValueProvider<DataType>>> void addValueFactory(@NonNull FactoryType type, @NonNull ValueFactory<DataType, B> factory) {
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
    private final Group group;

    @Getter
    private NumberProvider unknown;

    public ValueFactory(@NonNull Group group, @NonNull NumberProvider unknown) {
        this.cache = new HashMap<>();
        this.rCache = new HashMap<>();
        this.group = group;

        this.unknown = unknown;

        group.values().forEach(e -> {
            cache.put(e.getIdentifier(), e.getValue());
            rCache.put(e.getValue(), e.getIdentifier());
        });
    }

    /**
     * Get the name of the enum
     * value for the specified bye
     *
     * @param b Byte
     *
     * @return Name if registered, else
     *         the default value {@link #getUnknown()}
     */
    public String getName(Number b) {
        if (!rCache.containsKey(b)) {
            return unknown.getIdentifier();
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

    // This will prevent it from being generated when not used
    private BitField<? extends Number, ? extends ValueProvider<? extends Number>> bitField;

    public BitField<? extends Number, ? extends ValueProvider<? extends Number>> constructBitField() {
        if (bitField == null) { // Added caching to this
            bitField = new BitField<>(group);
        }
        return bitField;
    }
}
