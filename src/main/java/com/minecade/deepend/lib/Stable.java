package com.minecade.deepend.lib;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates a stable method
 * or class
 *
 * This means that the class members
 * won't change names or signatures
 * in a foreseeable future
 *
 * @author Citymonstret
 */
@Target({ElementType.PACKAGE, ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.SOURCE)
public @interface Stable {
}
