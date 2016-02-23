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

import com.minecade.deepend.channels.Channel;

/**
 * This will request the deletion of the
 * specified objects
 *
 * @see com.minecade.deepend.request.DataRequest
 *
 * @author Citymonstret
 */
public abstract class DeleteRequest extends DataRequest {

    /**
     * Constructor
     * @param dataRecipient Recipient that will handle the data
     * @param provider Provider used for authentication
     */
    public DeleteRequest(DataRecipient dataRecipient, UUIDProvider provider) {
        super(Channel.REMOVE_DATA, dataRecipient, provider);
    }
}
