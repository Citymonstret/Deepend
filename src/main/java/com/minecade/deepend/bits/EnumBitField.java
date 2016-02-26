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
import lombok.NonNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;

/**
 * An enum wrapper for the @link{BitField}
 *
 * @author Citymonstret
 */
@SuppressWarnings("ALL")
public class EnumBitField<DataType extends Number, E extends Enum<E> & ValueProvider<DataType>> {

    private BitField bitField;

    public EnumBitField(Class<E> clazz) {
        this(clazz, new BitField<>(new ProviderGroup<>(EnumSet.allOf(clazz))));
    }

    public EnumBitField(Class<E> clazz, BitField bitField) {
        this.bitField = bitField;
    }

    public final int construct(E ... objects) {
        return construct(Arrays.asList(objects));
    }

    public final int construct(@NonNull Collection<E> objects) {
        return bitField.construct(objects);
    }

    public BitField getBitField() {
        return this.bitField;
    }

    public final Collection<E> extract(int field) {
        return bitField.extract(field);
    }
}
