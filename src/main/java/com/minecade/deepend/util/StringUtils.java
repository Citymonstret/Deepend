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

import com.minecade.deepend.object.StringList;
import lombok.experimental.UtilityClass;

import java.util.Collection;

/**
 * Created 2/27/2016 for Deepend
 *
 * @author Citymonstret
 */
@UtilityClass
public class StringUtils
{

    public static final String LINE_SEPARATOR = System.getProperty( "line.separator" );

    public static String joinLines(final String... lines)
    {
        return joinLines( new StringList( lines ) );
    }

    public static String joinLines(Collection<String> lines)
    {
        final StringBuilder result = new StringBuilder();
        lines.forEach( s -> result.append( s ).append( LINE_SEPARATOR ) );
        return result.toString();
    }

}
