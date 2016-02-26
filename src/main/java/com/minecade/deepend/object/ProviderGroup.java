package com.minecade.deepend.object;

import com.minecade.deepend.values.NumberProvider;
import com.minecade.deepend.values.ValueProvider;
import lombok.Getter;
import lombok.NonNull;

import java.util.*;
import java.util.function.BiConsumer;

/**
 * Replaces the final structure of enumerators
 *
 * @author Citymonstret
 *
 * @param <DataType> Type that this ProviderGroup will handle
 * @param <T> Value provider returning the specified DataType
 */
public class ProviderGroup<DataType, T extends ValueProvider<? extends DataType>>  {

    @Getter
    private final Map<DataType, T> internalMap;

    @SafeVarargs
    public ProviderGroup(T ... values) {
        this(Arrays.asList(values));
    }

    /**
     * @param values Values to use in this ProviderGroup
     */
    public ProviderGroup(Collection<T> values) {
        internalMap = new HashMap<>();

        for (T value : values) {
            internalMap.put(value.getValue(), value);
        }
    }

    /**
     * Values Wrapper
     * @see Map#values()
     */
    final public Collection<T> values() {
        return internalMap.values();
    }

    /**
     * ForEach Wrapper
     * @see Map#forEach(BiConsumer)
     */
    final public void forEach(BiConsumer<? super DataType, ? super T> consumer) {
        internalMap.forEach(consumer);
    }

    @SafeVarargs
    public static <E extends Enum<E> & NumberProvider<Number>> ProviderGroup<Number, NumberProvider<Number>> fromEnum(E ... values) {
        return new ProviderGroup<>(values);
    }

    /**
     * Generates a ProviderGroup instance from an enum class, the
     * enum must implement {@link ValueProvider}
     *
     * @see #fromEnum(EnumSet) For super method
     *
     * @param clazz Enumerator class
     * @param <DataType> Type extending {@link Number}
     * @param <E> Enum type
     *
     * @return Generated ProviderGroup
     */
    public static <DataType extends Number, E extends Enum<E> & ValueProvider<DataType>> ProviderGroup<DataType, ValueProvider<DataType>> fromEnumClass(@NonNull final Class<E> clazz) {
        return fromEnum(EnumSet.allOf(clazz));
    }

    /**
     * Generates a ProviderGroup instance from en EnumSet, the
     * enum type must implement {@link ValueProvider}
     *
     * @param es EnumSet containing the enumerator values
     * @param <DataType> Type extending {@link Number}
     * @param <E> Enum type
     *
     * @return Generated ProviderGroup
     */
    public static <DataType extends Number, E extends Enum<E> & ValueProvider<DataType>> ProviderGroup<DataType, ValueProvider<DataType>> fromEnum(@NonNull final EnumSet<E> es) {
        List<ValueProvider<DataType>> numberProvider = new ArrayList<>();
        for (E e : es) {
            numberProvider.add(e);
        }
        return new ProviderGroup<>(numberProvider);
    }
}
