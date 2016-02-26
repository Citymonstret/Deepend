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

package com.minecade.deepend.util;

import com.minecade.deepend.data.DataHolder;
import com.minecade.deepend.data.DataObject;
import com.minecade.deepend.data.DeependBuf;
import com.minecade.deepend.lib.Beta;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Utilities involving data objects
 *
 * @author Citymonstret
 */
@UtilityClass
public class DataUtil {

    /**
     * Will loop through a holder and find
     * all appropriate data objects
     *
     * @param holder Holder to search in
     * @param s Name | Null (will then read from buf)
     * @param buf Input buffer
     * @param initialList Pre-created list
     * @param wrapHolder If this is true, then
     *                   holders will be wrapped
     *                   to objects, rather than
     *                   being read from
     *
     * @return List containing all appropriate data objects
     */
    @Beta
    public static List<DataObject> getDataObject(@NonNull final DataHolder holder, final String s, @NonNull final DeependBuf buf, final List<DataObject> initialList, final boolean wrapHolder) {
        String name;

        try {
            // This will use the pre-defined name, if it
            // exists, otherwise it will attempt to read
            // it
            name = s == null ? buf.getString() : s;
        } catch(final Exception e) {
            // Oh no! Something went wrong. Don't worry, though;
            // this will spit out everything in the initial
            // holder, and return that instead
            holder.forEach((key, value) -> {
                if (value instanceof DataObject) {
                    initialList.add((DataObject) value);
                } else {
                    if (!wrapHolder) {
                        initialList.add((DataObject)((DataHolder) value).getFallback());
                    } else {
                        initialList.add(new HolderWrapper((DataHolder) value));
                    }
                }
            });
            return initialList;
        }

        // The star means that all objects within the holder should be
        // returned, so let's convert it to a list instead
        if (name.equals("*")) {
            StringBuilder newName = new StringBuilder();
            Iterator<String> strings = holder.keySet().iterator();
            while (strings.hasNext()) {
                newName.append(strings.next());
                if (strings.hasNext()) {
                    newName.append(",");
                }
            }
            name = newName.toString();
        }

        // This means that a list has been sent
        // and that multiple items should be returned
        if (name.contains(",")) {
            for (String p : name.split(",")) {
                List<DataObject> r = getDataObject(holder, p, buf, new ArrayList<>(), false);
                if (r == null) {
                    return null;
                }
                initialList.addAll(r);
            }
        } else {
            Object o = holder.get(name);
            if (o == null) {
                return null;
            }
            if (o instanceof DataObject) {
                initialList.add((DataObject) o);
            } else if (o instanceof DataHolder) {
                if (!wrapHolder) {
                    List<DataObject> newList;
                    try {
                        newList = getDataObject((DataHolder) o, null, buf, new ArrayList<>(), false);
                        if (newList == null) {
                            return Collections.singletonList(new HolderWrapper((DataHolder) o));
                        }
                    } catch (final Exception e) {
                        e.printStackTrace();
                        return Collections.singletonList(new HolderWrapper((DataHolder) o));
                    }
                    initialList.addAll(newList);
                } else {
                    initialList.add(new HolderWrapper((DataHolder) o));
                }
            }
        }
        return initialList;
    }

    /**
     * This is a simple wrapper for
     * holders, which also allows for
     * deletion, quite useful, indeed
     *
     * @author Citymonstret
     */
    public static class HolderWrapper extends DataObject {

        @Getter
        private final DataHolder holder;

        public HolderWrapper(final DataHolder holder) {
            super(holder.getIdentifier(), "");
            this.holder = holder;
        }

        @Override
        public void delete() {
            this.holder.delete();
        }
    }
}