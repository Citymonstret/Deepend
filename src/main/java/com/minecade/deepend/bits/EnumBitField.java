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
