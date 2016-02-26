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
package com.minecade.deepend.bits;

import com.minecade.deepend.values.NumberProvider;
import lombok.NonNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Wiki: <a>https://github.com/Minecade/Deepend/wiki/Bit-fields</a>
 *
 * @author Citymonstret
 */
public class BitField<E extends Enum<E> & NumberProvider> {

    /**
     * Just for caching of bytes, to make this
     * a little faster. Memory usage is too
     * minimal to even care about.
     */
    private final Map<Number, E> internalMap;

    /**
     * @param values Values to be used in this BitField
     */
    public BitField(@NonNull E[] values) {
        internalMap = new HashMap<>();

        for (E value : values) {
            internalMap.put(value.getValue(), value);
        }
    }

    /**
     * Extract the values from a constructed field
     *
     * @see #construct(Collection) To construct a field from a collection
     * @see #construct(Enum[]) To construct a field from an array (vararg)
     *
     * @param field BitField to extract from
     *
     * @return Collection containing the extracted objects
     */
    public final Collection<E> extract(int field) {
        if (field == 0) {
            return Collections.emptySet();
        }
        return internalMap.keySet().stream().filter(b -> (field & b.intValue()) == b.intValue()).map(internalMap::get).collect(Collectors.toCollection(HashSet::new));
    }


    /**
     * @see #construct(Collection) For super method
     */
    @SafeVarargs
    public final int construct(E... objects) {
        return construct(Arrays.asList(objects));
    }

    /**
     * Construct a BitField from the specified objects
     *
     * @param objects Objects to construct from
     *
     * @return Constructed BitField
     */
    public final int construct(@NonNull Collection<E> objects) {
        Iterator<E> iterator = objects.iterator();
        if (!iterator.hasNext()) {
            return 0;
        }
        int i = iterator.next().getValue().intValue();
        while (iterator.hasNext()) {
            i |= iterator.next().getValue().intValue();
        }
        return i;
    }
}
