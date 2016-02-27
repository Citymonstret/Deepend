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
package com.minecade.deepend.resources;

import com.minecade.deepend.lib.Beta;
import com.minecade.deepend.object.ObjectGetter;
import com.minecade.deepend.util.Assert;
import lombok.AccessLevel;
import lombok.Getter;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple configuration/storage file implementation
 * based on individual lines
 *
 * The format is really simple:
 * <pre>
 * key1: value\n
 * key2: value\n
 * </pre>
 *
 * @author Citymonstret
 */
@Beta
public class DeependBundle implements ObjectGetter<String, String>, DataFile {

    @Getter(AccessLevel.PRIVATE)
    private final Map<String, String> properties;

    private final File file;

    public DeependBundle(String propertyFile) {
        this(propertyFile, false, DefaultBuilder.create());
    }

    public final static File folder;
    static {
        folder = new File("./.deepend");
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                throw new RuntimeException("Couldn't create ./configs, please do it manually!");
            }
        }
    }

    public DeependBundle(String propertyFile, boolean hasToExist, DefaultBuilder builder) {
        file = new File(folder, Assert.notEmpty(propertyFile) + ".deepend");

        this.properties = new HashMap<>();
        this.properties.putAll(builder.compiled);
        if (!file.exists()) {
            if (hasToExist) {
                throw new RuntimeException("Missing property file: " + file.getName());
            }
            try {
                if (file.createNewFile()) {
                    saveFile();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
           loadFile();
        }
    }

    public DeependBundle(String clientStrings, boolean b) {
        this(clientStrings, b, DefaultBuilder.create());
    }

    @Override
    public String get(String key) {
        key = Assert.notEmpty(key);

        if (properties.containsKey(key)) {
            return properties.get(key);
        }

        return "";
    }

    @Override
    public boolean containsKey(String s) {
        return properties.containsKey(s);
    }

    @Override
    public void reload() {}

    @Override
    public void saveFile() {
        try (FileWriter writer = new FileWriter(file)) {
            try (BufferedWriter bWriter = new BufferedWriter(writer)) {
                this.properties.forEach((k, v) -> {
                    try {
                        bWriter.write(k + ": " + v + "\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch(Exception ee) {
            ee.printStackTrace();
        }
    }

    @Override
    public void loadFile() {
        try (FileReader reader = new FileReader(file)) {
            try (BufferedReader bReader = new BufferedReader(reader)) {
                String line;
                while ((line = bReader.readLine()) != null) {
                    String[] parts = line.split(": ");
                    if (parts.length < 2) {
                        continue;
                    }
                    parts[0] = parts[0].replaceAll("\\s+", "");
                    properties.put(parts[0], parts[1]);
                }
            } catch(final Exception ee) {
                ee.printStackTrace();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public <T> void set(String key, T value) {
        properties.put(Assert.notEmpty(key), Assert.notNull(value).toString());
    }

    @Override
    public boolean contains(String key) {
        return properties.containsKey(Assert.notEmpty(key));
    }

    @Override
    public <T> T get(String key, T def) {
        return (T) properties.get(key);
    }

    @Override
    public <T> void setIfNotExists(String key, T value) {
        if (!containsKey(key)) {
            set(key, value);
        }
    }

    public static class DefaultBuilder {

        private final Map<String, String> compiled = new HashMap<>();
        private final Map<String, String> defaults = new HashMap<>();

        public DefaultBuilder add(String key, Object value) {
            this.defaults.put(key, value.toString());
            return this;
        }

        public DefaultBuilder build() {
            compiled.putAll(defaults);
            defaults.clear();
            return this;
        }

        public static DefaultBuilder create() {
            return new DefaultBuilder();
        }
    }
}
