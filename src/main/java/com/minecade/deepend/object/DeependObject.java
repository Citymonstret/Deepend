/*
 * Copyright 2016 Minecade
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.minecade.deepend.object;

import com.minecade.deepend.data.DataType;
import com.minecade.deepend.data.DeependBuf;
import com.minecade.deepend.logging.Logger;
import lombok.*;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 **********************************************************
 * Direct involvement with this
 * class is not recommended, unless
 * you know what you're doing!
 * ********************************************************
 *
 * @author Citymonstret
 */
@SuppressWarnings("unused")
public abstract class DeependObject {

    @Getter
    private ByteProvider objectType;

    protected Map<PropertyHolder, PropertyGetter> properties;
    protected Map<String, Object> cache = null;
    protected Map<String, PropertyHolder> holderMap = null;

    protected volatile boolean isBuilt;

    public DeependObject(@NonNull ByteProvider objectType, boolean scan, @NonNull Class<?> clazz) {
        this.objectType = objectType;
        this.properties = new ConcurrentHashMap<>();
        this.isBuilt = false;

        if (scan) {
            scan(clazz);
        }
    }

    @Synchronized
    protected void scan(@NonNull Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            for (ObjectProperty property : field.getDeclaredAnnotationsByType(ObjectProperty.class)) {
                addValue(property.name(), property.type(), new FieldGetter(field, this));
            }
        }
        buildValues();
    }

    @SneakyThrows
    protected void updateFieldValue(@NonNull String name) {
        FieldGetter getter = (FieldGetter) properties.get(holderMap.get(name));
        this.cache.put(name, getter.getValue());
    }

    protected void addValue(@NonNull String name, @NonNull DataType type, @NonNull PropertyGetter getter) {
        PropertyHolder holder = new PropertyHolder(name, type);
        properties.put(holder, getter);
    }

    protected void convertAndRead(@NonNull DeependBuf buf) {
        Map<String, String> _v = convert(buf, this.properties.size());
        for (Map.Entry<String, String> _k : _v.entrySet()) {
            try {
                updateField(_k.getKey(), _k.getValue());
            } catch(final Exception e) {
                Logger.get().error("Failed to read \"" + _k.getKey() + "\":\"" + _k.getValue() + "\"", e);
            }
        }
    }

    protected void sendKeys(@NonNull DeependBuf buf) {
        StringBuilder builder = new StringBuilder();
        Iterator<String> keys = cache.keySet().iterator();
        while (keys.hasNext()) {
            builder.append(keys.next());
            if (keys.hasNext()) {
                builder.append(",");
            }
        }
        Logger.get().debug("Sending keys: " + builder.toString());
        buf.writeString(builder.toString());
    }

    @Override
    public abstract String toString();

    protected void addValue(@NonNull String name, @NonNull DataType type, @NonNull Object value) {
        PropertyGetter getter = new PropertyGetter() {
            @Override
            Object getValue() {
                return value;
            }
        };
        addValue(name, type, getter);
    }

    @Synchronized
    protected void buildValues() {
        this.isBuilt = false;
        this.cache = new HashMap<>();
        this.holderMap = new HashMap<>();
        for (Map.Entry<PropertyHolder, PropertyGetter> entry : properties.entrySet()) {
            this.cache.put(entry.getKey().name, entry.getValue().getValue());
            this.holderMap.put(entry.getKey().name, entry.getKey());
        }
        this.isBuilt = true;
    }

    protected void updateField(@NonNull String key, @NonNull Object value) {
        FieldGetter getter = (FieldGetter) properties.get(holderMap.get(key));
        try {
            getter.field.set(getter.object, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    protected void writeValues(@NonNull DeependBuf buf) {
        buf.writeInt(cache.size());
        for (Map.Entry<String, Object> cacheEntry : cache.entrySet()) {
            String entryName = cacheEntry.getKey();
            Object entryValue = cacheEntry.getValue();
            buf.writeString(entryName + ":" + entryValue.toString());
        }
    }

    public Object getValue(@NonNull String key) {
        if (this.hasCache()) {
            this.buildValues();
        }
        return cache.get(key);
    }

    public boolean hasCache() {
        return this.isBuilt;
    }

    public DeependObject(ByteProvider objectType) {
        this(objectType, false, null);
    }

    public abstract void write(DeependBuf buf);

    public abstract void read(DeependBuf buf);

    public abstract void request(String requestedKey, DeependBuf buf);

    protected Map<String, String> convert(DeependBuf buf, int num) {
        DataType[] req = new DataType[num * 2];
        for (int i = 0; i < num * 2; i++) {
            req[i] = DataType.STRING;
        }
        buf = new DeependBuf(buf, req);
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < num; i++) {
            map.put(buf.getString(), buf.getString());
        }
        return map;
    }

    private class FieldGetter extends PropertyGetter {

        @NonNull private final Field field;
        @NonNull private final Object object;

        public FieldGetter(Field field, Object object) {
            try {
                field.setAccessible(true);
            } catch (final Exception e) {
                e.printStackTrace();
            } finally {
                this.field = field;
                this.object = object;
            }
        }

        @Override
        Object getValue() {
            try {
                return field.get(object);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private abstract class PropertyGetter {
        abstract Object getValue();
    }

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    private class PropertyHolder {
        @NonNull final String name;
        @NonNull final DataType type;
    }
}
