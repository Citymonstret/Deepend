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

/**
 * A mirror of another data holder
 */
public class MirrorDataHolder extends DataHolder {

    private final DataHolder toMirror;
    private final MirrorFilter mirrorFilter;

    /**
     * Constructor
     * @param identifier Holder identifier
     * @param toMirror Holder to mirror values from
     * @param mirrorFilter Value filter
     */
    public MirrorDataHolder(String identifier, DataHolder toMirror, MirrorFilter mirrorFilter) {
        super(identifier);

        this.toMirror = toMirror;
        this.mirrorFilter = mirrorFilter;

        // Aren't lambdas beautiful?
        this.toMirror.addListener(MirrorDataHolder.this::sync);
    }

    void sync() {
        this.clear();
        this.toMirror.forEach((key, value) -> {
            if ((value = mirrorFilter.filter(value)) != null) {
                if (value instanceof DataHolder) {
                    DataHolder holder = (DataHolder) value;
                    this.put(holder.getIdentifier(), holder);
                } else {
                    this.put(((DataObject) value).getName(), value);
                }
            }
        });
    }

    /**
     * Filter which decides
     * what values are mirrored
     */
    public interface MirrorFilter {

        /**
         * Return null to
         * filter the object away
         *
         * @param o Input object
         * @return Object | Null (to remove)
         */
        Object filter(Object o);
    }
}
