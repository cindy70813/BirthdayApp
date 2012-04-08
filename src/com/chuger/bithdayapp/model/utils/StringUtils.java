package com.chuger.bithdayapp.model.utils;

/**
 * User: Acer5740
 * Date: 13.02.12
 * Time: 11:00
 */
public final class StringUtils {
    private static final String EMPTY_STRING = "";

    public static boolean isNotEmpty(final String value) {
        return !isEmpty(value);
    }

    public static boolean isEmpty(final String value) {
        return value == null || EMPTY_STRING.equals(value.trim());
    }
}
