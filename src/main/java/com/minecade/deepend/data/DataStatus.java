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
package com.minecade.deepend.data;

import com.minecade.deepend.object.ByteProvider;
import com.minecade.deepend.object.Status;

/**
 * Created 2/25/2016 for Deepend
 *
 * @author Citymonstret
 */
public class DataStatus extends Status<ByteProvider> {

    public DataStatus(ByteProvider category) {
        super(category);
    }

}
