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

import com.minecade.deepend.object.ProviderGroup;
import com.minecade.deepend.values.ValueProvider;
import lombok.Getter;
import lombok.NonNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Wiki: <a>https://github.com/Minecade/Deepend/wiki/Bit-fields</a>
 *
 * @author Citymonstret
 */
public class BitField<DataType extends Number, Provider extends ValueProvider<DataType>> {

    @Getter
    private final ProviderGroup<DataType, Provider> providerGroup;

    public BitField(ProviderGroup<DataType, Provider> providerGroup) {
        this.providerGroup = providerGroup;
    }

    /**
     * Extract the values from a constructed field
     *
     * @see #construct(Collection) To construct a field from a collection
     *
     * @param field BitField to extract from
     *
     * @return Collection containing the extracted objects
     */
    public final Collection<Provider> extract(int field) {
        if (field == 0) {
            return Collections.emptySet();
        }
        return providerGroup.getInternalMap().keySet().stream().filter(b -> (field & b.intValue()) == b.intValue()).map(providerGroup.getInternalMap()::get).collect(Collectors.toCollection(HashSet::new));
    }


    /**
     * @see #construct(Collection) For super method
     */
    @SafeVarargs
    public final int construct(Provider ... objects) {
        return construct(Arrays.asList(objects));
    }

    /**
     * Construct a BitField from the specified objects
     *
     * @param objects Objects to construct from
     *
     * @return Constructed BitField
     */
    public final int construct(@NonNull Collection<Provider> objects) {
        Iterator<Provider> iterator = objects.iterator();
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
