package com.minecade.deepend.values;

import com.minecade.deepend.lib.Stable;

/**
 * This simply returns a value
 *
 * @author Citymonstret
 * @param <T> Value return type
 */
@Stable
public interface ValueProvider<T> {

    /**
     * Get the byte value of the object
     *
     * @return Byte value of the object
     */
    T getValue();

    /**
     * Get the identifier for this provider
     *
     * @return The identifier of this provider,
     *         if it hasn't been overridden then
     *         a combination of the class name
     *         and the byte value will be returned
     */
    default String getIdentifier() {
        return getClass().getName() + ":" + getValue();
    }
}