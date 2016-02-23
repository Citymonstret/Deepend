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

import java.util.UUID;

/**
 * A simple address (connection address)
 *
 * @author Citymonstret
 */
public class SimpleAddress {

    private final String host;
    private UUID uuid;

    /**
     * Constructor
     * @param host Host Address
     */
    public SimpleAddress(final String host) {
        this.host = host;
        this.uuid = UUID.randomUUID();
    }

    /**
     * Get the host address
     * @return Host Address
     */
    public String getHost() {
        return this.host;
    }

    @Override
    public String toString() {
        return this.host + ":" + this.uuid;
    }

    /**
     * Get the UUID
     * @return UUID
     */
    public String getUUID() {
        return uuid.toString();
    }

    /**
     * Set the UUID
     * @param uuid UUID
     */
    public void setUUID(String uuid) {
        this.uuid = UUID.fromString(uuid);
    }
}
