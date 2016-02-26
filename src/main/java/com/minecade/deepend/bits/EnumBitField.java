package com.minecade.deepend.bits;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.minecade.deepend.object.ProviderGroup;
import com.minecade.deepend.values.NumberProvider;
import com.minecade.deepend.values.ValueProvider;
import lombok.NonNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;

/**
 * Created 2/26/2016 for Deepend
 *
 * @author Citymonstret
 */
public class EnumBitField<E extends Enum<E> & NumberProvider> {

    private BitField bitField;

    private BiMap<Number, E> internalMap;

    private final Class<E> clazz;

    public EnumBitField(Class<E> clazz) {
        bitField = new BitField<>(new ProviderGroup<>(convert(EnumSet.allOf(clazz))));
        this.clazz = clazz;
    }

    public EnumBitField(Class<E> clazz, BitField bitField) {
        internalMap = HashBiMap.create();
        EnumSet.allOf(clazz).forEach(e -> internalMap.put(e.getValue(), e));
        this.bitField = bitField;
        this.clazz = clazz;
    }

    public final int construct(E ... objects) {
        return construct(Arrays.asList(objects));
    }

    public final int construct(@NonNull Collection<E> objects) {
        return bitField.construct(convert(objects));
    }

    public Collection<ValueProvider<Number>> convert(Collection<E> es) {
        Collection<ValueProvider<Number>> collection = new HashSet<>();
        es.forEach(collection::add);
        return collection;
    }

    public BitField getBitField() {
        return this.bitField;
    }

    public final Collection<E> extract(int field) {
        Collection input = bitField.extract(field);
        Collection<E> output = new HashSet<>();
        input.forEach(o -> {
            if (clazz.isInstance(o)) {
                output.add(clazz.cast(o));
            }
        });
        return output;
    }
}
