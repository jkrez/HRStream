package com.streamteam.pushpoint;

import java.nio.charset.Charset;

/**
 * Created by john on 6/7/16.
 */

public class Helpers {

    public static boolean isNullOrWhitespace(String s) {
        if (s == null || isWhitespace(s)) {
            return true;
        }

        return false;
    }

    public static boolean isWhitespace(String s) {
        int length = s.length();
        if (length > 0) {
            for (int i = 0; i < length; i++) {
                if (!Character.isWhitespace(s.charAt(i))) {
                    return false;
                }
            }
            return true;
        }
        return true;
    }

    public static byte[] StringToBytes(String s) {
        return Charset.forName("UTF-8").encode(s).array();
    }

    public static <T extends Enum<?>> T searchEnum(Class<T> enumeration,
                                                   String search) {
        for (T each : enumeration.getEnumConstants()) {
            if (each.name().compareToIgnoreCase(search) == 0) {
                return each;
            }
        }
        return null;
    }
}
