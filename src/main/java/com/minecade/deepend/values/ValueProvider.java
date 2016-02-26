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
package com.minecade.deepend.values;

import com.minecade.deepend.lib.Stable;

/**
 * This simply returns a value
 *
 * @author Citymonstret
 * @param <T> Value return type
 */
@Stable
public interface ValueProvider<T> {

    /**
     * Get the byte value of the object
     *
     * @return Byte value of the object
     */
    T getValue();

    /**
     * Get the identifier for this provider
     *
     * @return The identifier of this provider,
     *         if it hasn't been overridden then
     *         a combination of the class name
     *         and the byte value will be returned
     */
    default String getIdentifier() {
        return getClass().getName() + ":" + getValue();
    }
}