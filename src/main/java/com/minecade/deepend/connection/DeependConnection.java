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

package com.minecade.deepend.connection;

import com.minecade.deepend.data.DeependBuf;
import com.minecade.deepend.lib.Stable;
import com.minecade.deepend.request.UUIDProvider;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Stable
public class DeependConnection implements UUIDProvider {

    private final Map<String, Object> metaMapping = new ConcurrentHashMap<>();

    @NonNull
    @Getter
    private final SimpleAddress remoteAddress;

    @Getter
    @Setter
    private boolean authenticated;

    public DeependConnection(SimpleAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
        this.authenticated = false;
    }

    public <T> T getObject(String key, Class<T> clazz) {
        return clazz.cast(metaMapping.get(key));
    }

    public DeependBuf getBuf(String key) {
        Object o = metaMapping.get(key);
        if (o instanceof ByteBuf) {
            return new DeependBuf((ByteBuf) o);
        } else if (o instanceof DeependBuf) {
            return (DeependBuf) o;
        }
        return null;
    }

    public void addMeta(String key, Object meta) {
        this.metaMapping.put(key, meta);
    }

    private UUID cachedUUID;

    @Override
    public UUID getUUID() {
        return cachedUUID == null ? (cachedUUID = UUID.fromString(remoteAddress.getUUID())) : cachedUUID;
    }
}
