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
package com.minecade.deepend.storage;

import com.minecade.deepend.data.DataHolder;
import com.minecade.deepend.data.DataObject;

/**
 * Created 2/27/2016 for Deepend
 *
 * @author Citymonstret
 */
public interface StorageBase
{

    void setup();

    void close();

    void getDataObject(String path);

    void getDataHolder(String path);

    void saveDataHolder(String path, DataHolder holder);

    void saveDataObject(String path, DataObject object);

}
