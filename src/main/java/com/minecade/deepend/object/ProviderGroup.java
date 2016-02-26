package com.minecade.deepend.object;

import com.minecade.deepend.values.NumberProvider;
import com.minecade.deepend.values.ValueProvider;
import lombok.Getter;

import java.util.*;
import java.util.function.BiConsumer;

/**
 * Created 2/26/2016 for Deepend
 *
 * @author Citymonstret
 */
public class ProviderGroup<DataType, T extends ValueProvider<? extends DataType>>  {

    @Getter
    private final Map<DataType, T> internalMap;

    @SafeVarargs
    public ProviderGroup(T ... values) {
        this(Arrays.asList(values));
    }

    public ProviderGroup(Collection<T> values) {
        internalMap = new HashMap<>();

        for (T value : values) {
            internalMap.put(value.getValue(), value);
        }
    }

    public Collection<T> values() {
        return internalMap.values();
    }

    public void forEach(BiConsumer<? super DataType, ? super T> consumer) {
        internalMap.forEach(consumer);
    }

    @SafeVarargs
    public static <E extends Enum<E> & NumberProvider<Number>> ProviderGroup<Number, NumberProvider<Number>> fromEnum(E ... values) {
        return new ProviderGroup<>(values);
    }

    public static <DataType extends Number, E extends Enum<E> & ValueProvider<DataType>> ProviderGroup<DataType, ValueProvider<DataType>> fromEnumClass(Class<E> clazz) {
        return fromEnum(EnumSet.allOf(clazz));
    }

    public static <DataType extends Number, E extends Enum<E> & ValueProvider<DataType>> ProviderGroup<DataType, ValueProvider<DataType>> fromEnum(EnumSet<E> es) {
        List<ValueProvider<DataType>> numberProvider = new ArrayList<>();
        for (E e : es) {
            numberProvider.add(e);
        }
        return new ProviderGroup<>(numberProvider);
    }
}
