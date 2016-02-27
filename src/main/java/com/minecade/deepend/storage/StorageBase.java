package com.minecade.deepend.storage;

import com.minecade.deepend.data.DataHolder;
import com.minecade.deepend.data.DataObject;

/**
 * Created 2/27/2016 for Deepend
 *
 * @author Citymonstret
 */
public interface StorageBase {

    void setup();

    void close();

    void getDataObject(String path);

    void getDataHolder(String path);

    void saveDataHolder(String path, DataHolder holder);

    void saveDataObject(String path, DataObject object);

}
