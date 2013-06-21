package com.chuger.bithdayapp.model.utils;

/**
 * User: Acer5740
 * Date: 13.02.12
 * Time: 11:00
 */
public final class StringUtils {

    private StringUtils() {
    }

    public static boolean isNotEmpty(final String... str) {
        return !isEmpty(str);
    }

    public static boolean isEmpty(final String... str) {
        boolean result = false;
        for (String string : str) {
            result = string == null || string.trim().length() == 0;
            if (result) {
                break;
            }
        }
        return result;
    }
}
