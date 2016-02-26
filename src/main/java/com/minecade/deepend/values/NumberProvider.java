package com.minecade.deepend.values;

import com.minecade.deepend.lib.Stable;

/**
 * @author Citymonstret
 */
@Stable
public interface NumberProvider<T extends Number> extends ValueProvider<T> {

    @Override
    T getValue();

}
