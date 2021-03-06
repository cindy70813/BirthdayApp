package com.chuger.bithdayapp.model.db;

import android.database.Cursor;
import android.util.Log;
import com.chuger.bithdayapp.model.domain.User;

import java.util.Date;

import static com.chuger.bithdayapp.model.db.UserOpenHelper.COLUMN_ADDITIONAL_NAME;
import static com.chuger.bithdayapp.model.db.UserOpenHelper.COLUMN_BIRTHDAY_DATE;
import static com.chuger.bithdayapp.model.db.UserOpenHelper.COLUMN_DAY_COUNT;
import static com.chuger.bithdayapp.model.db.UserOpenHelper.COLUMN_FB_ID;
import static com.chuger.bithdayapp.model.db.UserOpenHelper.COLUMN_FIRST_NAME;
import static com.chuger.bithdayapp.model.db.UserOpenHelper.COLUMN_GOOGLE_ID;
import static com.chuger.bithdayapp.model.db.UserOpenHelper.COLUMN_ID;
import static com.chuger.bithdayapp.model.db.UserOpenHelper.COLUMN_LAST_NAME;
import static com.chuger.bithdayapp.model.db.UserOpenHelper.COLUMN_PIC_URL;
import static com.chuger.bithdayapp.model.db.UserOpenHelper.COLUMN_TITLE;
import static com.chuger.bithdayapp.model.db.UserOpenHelper.COLUMN_UPDATED;
import static com.chuger.bithdayapp.model.db.UserOpenHelper.COLUMN_VK_ID;
import static com.chuger.bithdayapp.model.db.UserOpenHelper.COLUMN_YEAR_COUNT;
import static com.chuger.bithdayapp.model.db.UserOpenHelper.COLUMN_YEAR_UNKNOWN;
import static com.chuger.bithdayapp.model.utils.StringUtils.isEmpty;

/**
 * User: Acer5740
 * Date: 13.02.12
 * Time: 13:18
 */
public class CursorHelper {
    private static final String TAG = CursorHelper.class.getSimpleName();
    private final Cursor cursor;

    public CursorHelper(final Cursor cursor){
        this.cursor = cursor;
    }

    public User getUser() {
        final User user = new User();
        user.setId(getLong(COLUMN_ID));
        user.setFacebookId(getLong(COLUMN_FB_ID));
        user.setVkontakteId(getLong(COLUMN_VK_ID));
        user.setGoogleId(getString(COLUMN_GOOGLE_ID));
        user.setLastName(getString(COLUMN_LAST_NAME));
        user.setFirstName(getString(COLUMN_FIRST_NAME));
        user.setAdditionalName(getString(COLUMN_ADDITIONAL_NAME));
        user.setTitle(getString(COLUMN_TITLE));
        user.setPicUrl(getString(COLUMN_PIC_URL));
        final Long birthday = getLong(COLUMN_BIRTHDAY_DATE);
        if (birthday != null) {
            user.setBirthday(new Date(birthday));
        }   
        final Long updated = getLong(COLUMN_UPDATED);
        if (birthday != null) {
            user.setUpdated(new Date(updated));
        }
        user.setYearCount(getInt(COLUMN_YEAR_COUNT));
        user.setDayCount(getInt(COLUMN_DAY_COUNT));
        user.setYearUnknown(getInt(COLUMN_YEAR_UNKNOWN) == 1);
        return user;
    }

    public String getString(final String columnName) {
        if (isEmpty(columnName)) {
            Log.e(TAG, "Can't get String from columnName " + columnName);
            return null;
        }
        return cursor.getString(cursor.getColumnIndex(columnName));
    }

    public Long getLong(final String columnName) {
        if (isEmpty(columnName)) {
            Log.e(TAG, "Can't get Long from columnName " + columnName);
            return null;
        }
        return cursor.getLong(cursor.getColumnIndex(columnName));
    }   
    
    public Integer getInt(final String columnName) {
        if (isEmpty(columnName)) {
            Log.e(TAG, "Can't get Long from columnName " + columnName);
            return null;
        }
        return cursor.getInt(cursor.getColumnIndex(columnName));
    }
}
