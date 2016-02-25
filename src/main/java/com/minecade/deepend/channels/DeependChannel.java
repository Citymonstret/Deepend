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

package com.minecade.deepend.channels;

import com.minecade.deepend.connection.DeependConnection;
import com.minecade.deepend.data.DeependBuf;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * A simple channel shell
 *
 * @author Citymonstret
 */
@RequiredArgsConstructor
public abstract class DeependChannel {

    @NonNull
    @Getter
    private final Channel channelType;

    /**
     * This is where the channel does what it's supposed
     * to do
     *
     * @param connection Connection listing
     * @param buf Output buffer. This is
     *            NOT the input buf
     */
    public abstract void act(@NonNull DeependConnection connection, @NonNull DeependBuf buf);

    protected void resetStatus() {
        ChannelManager.instance.getChannelStatus(getChannelType()).resetStatus();
    }
}
