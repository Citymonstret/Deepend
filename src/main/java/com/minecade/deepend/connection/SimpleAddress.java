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

import com.minecade.deepend.lib.Stable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

/**
 * A simple address (connection address)
 *
 * @author Citymonstret
 */
@Stable
@RequiredArgsConstructor
public class SimpleAddress {

    @Getter
    private final String host;

    @Override
    public String toString() {
        return this.host;
    }

    public static SimpleAddress fromString(String i) {
        String[] parts = i.split(":");
        return new SimpleAddress(parts[0]);
    }
}
