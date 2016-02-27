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
public class StringUtils {

    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    public static String joinLines(final String ... lines) {
        return joinLines(new StringList(lines));
    }

    public static String joinLines(Collection<String> lines) {
        final StringBuilder result = new StringBuilder();
        lines.forEach(s -> result.append(s).append(LINE_SEPARATOR));
        return result.toString();
    }

}
