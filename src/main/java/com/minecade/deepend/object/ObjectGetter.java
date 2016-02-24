package com.minecade.deepend.object;

/**
 * Created 2/24/2016 for Deepend
 *
 * @author Citymonstret
 */
public interface ObjectGetter<K, V> {
    V get(K k);
    boolean containsKey(K k);
}
