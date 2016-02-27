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

package com.minecade.deepend.request;

import com.minecade.deepend.lib.Stable;
import com.minecade.deepend.values.ValueProvider;

import java.util.UUID;

/**
 * This is a simple interface which
 * allows an object to return a UUID
 *
 * @author Citymonstret
 */
@Stable
public interface UUIDProvider extends ValueProvider<UUID> {

    /**
     * Get the UUID
     * @return UUID
     */
    UUID getUUID();

    default UUID getValue() {
        return getUUID();
    }
}
