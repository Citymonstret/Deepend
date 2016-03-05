package com.minecade.deepend.reflection;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Created 3/4/2016 for Deepend
 *
 * @author Citymonstret
 */
public final class Field {

    private final Class<?> clazz;
    private final List<FieldProperty> propertyList;
    private String name;
    private Object instance;

    public Field(Class<?> clazz) {
        this.clazz = clazz;
        this.propertyList = new ArrayList<>();
    }

    public Field withProperty(FieldProperty property) {
        this.propertyList.add(property);
        return this;
    }

    public Field withProperties(FieldProperty ... properties) {
        for (FieldProperty property : properties) {
            withProperty(property);
        }
        return this;
    }

    public Field fromInstance(Object instance) {
        this.instance = instance;
        return this;
    }

    public Field named(String name) {
        this.name = name;
        return this;
    }

    public java.lang.reflect.Field getField() throws Exception {
        if (instance == null && !propertyList.contains(FieldProperty.STATIC)) {
            throw new RuntimeException("Cannot fetch static field that isn't static!");
        }
        if (propertyList.contains(FieldProperty.CONSTANT)) {
            this.name = name.toUpperCase();
        }
        java.lang.reflect.Field field = clazz.getDeclaredField(name);
        if (propertyList.contains(FieldProperty.ACCESS_GRANT)) {
            field.setAccessible(true);
        }
        return field;
    }

    public Object getValue() throws Exception {
        return getField().get(instance);
    }

    public Field setValue(Object newValue) throws Exception {
        java.lang.reflect.Field field = getField();
        if (propertyList.contains(FieldProperty.CONSTANT)) {
            new Field(java.lang.reflect.Field.class).named("modifiers").withProperties(FieldProperty.ACCESS_GRANT).fromInstance(field).getField().setInt(field, field.getModifiers() & ~Modifier.FINAL);
        }
        field.set(instance, newValue);
        return this;
    }

    public enum FieldProperty {
        STATIC,
        CONSTANT,
        ACCESS_GRANT,
        ACCESS_REVERT
    }
}
