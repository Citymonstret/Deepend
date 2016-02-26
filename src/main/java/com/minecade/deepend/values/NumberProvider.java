package com.minecade.deepend.values;

/**
 * @author Citymonstret
 */
public interface NumberProvider<T extends Number> extends ValueProvider<T> {

    @Override
    T getValue();
}
