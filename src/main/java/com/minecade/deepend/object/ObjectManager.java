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

import com.minecade.deepend.data.DeependBuf;
import com.minecade.deepend.logging.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ObjectManager {

    public static final ObjectManager instance = new ObjectManager();

    private final Map<Byte, Class<? extends DeependObject>> objectMapping;

    public ObjectManager() {
        this.objectMapping = new ConcurrentHashMap<>();
    }

    public void registerMapping(Class<? extends DeependObject> clazz) {
        DeependObject instance = getInstance(clazz);
        this.objectMapping.put(instance.getObjectType().getByte(), clazz);
    }

    public DeependObject getInstance(Class<? extends DeependObject> clazz) {
        DeependObject instance = null;
        try {
            instance = clazz.newInstance();
        } catch(final Exception e) {
            try {
                clazz.getConstructor().setAccessible(true);
                instance = clazz.newInstance();
            } catch(final Exception ee) {
                Logger.get().error("Failed to initiate object", ee);
            }
        }
        return instance;
    }

    public DeependObject getInstance(ByteProvider provider) {
        Class<? extends DeependObject> clazz = this.objectMapping.get(provider.getByte());
        return getInstance(clazz);
    }

    public boolean hasRegisteredType(byte provider) {
        return this.objectMapping.containsKey(provider);
    }

    public DeependObject construct(ByteProvider provider, DeependBuf buf) {
        DeependObject instance = getInstance(provider);
        instance.read(buf);
        return instance;
    }
}
