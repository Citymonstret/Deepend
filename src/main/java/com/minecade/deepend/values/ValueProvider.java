package com.minecade.deepend.values;

/**
 * This is used to return value, as
 * easy as that
 *
 * @author Citymonstret
 */
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