package com.chuger.bithdayapp.model.utils;

import android.util.Log;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

import static com.chuger.bithdayapp.model.utils.StringUtils.isEmpty;
import static org.joda.time.format.DateTimeFormat.forPattern;

/**
 * User: Acer5740
 * Date: 13.02.12
 * Time: 10:31
 */
public final class DateTimeUtils {

    private static final String TAG = DateTimeUtils.class.getSimpleName();
    public static final DateTimeFormatter LONG_DATE_FORMAT = forPattern("dd.MM.yyyy");
    public static final DateTimeFormatter SHORT_DATE_FORMAT = forPattern("dd.MM");
    public static final DateTimeFormatter FB_DATE_FORMAT = forPattern("MM/dd/yyyy");
    public static final DateTimeFormatter FB_DATE_FORMAT_SHORT = forPattern("MM/dd");

    public static final DateTimeFormatter VK_DATE_FORMAT = forPattern("d.M.yyyy");
    public static final DateTimeFormatter VK_DATE_FORMAT_SHORT = forPattern("d.M");

    public static String format(final Date date) {
        if (date != null) {
            return LONG_DATE_FORMAT.print(new DateTime(date));
        } else {
            return null;
        }
    }

    public static DateTime parseFBDate(final String dateString) {
        if (isEmpty(dateString)) {
            Log.w(TAG, "Cannot parse empty FB dateString to date");
            return null;
        }

        final DateTime date;
        final int length = dateString.length();
        if (length == 10) {
            date = FB_DATE_FORMAT.parseDateTime(dateString);
        } else if (length == 5) {
            date = FB_DATE_FORMAT_SHORT.parseDateTime(dateString);
        } else {
            date = null;
        }
        return date;

    }

    public static DateTime parseVKDate(final String dateString) {
        if (isEmpty(dateString)) {
            Log.w(TAG, "Cannot parse empty VK dateString to date");
            return null;
        }

        final DateTime date;
        final int length = dateString.length();
        if (length >= 8) {
            date = VK_DATE_FORMAT.parseDateTime(dateString);
        } else if (length >= 3) {
            date = VK_DATE_FORMAT_SHORT.parseDateTime(dateString);
        } else {
            date = null;
        }
        return date;
    }
}
