package com.chuger.bithdayapp.model.dataSource;

import android.app.ListActivity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.CursorAdapter;
import com.chuger.bithdayapp.model.db.CursorHelper;
import com.chuger.bithdayapp.model.db.UserOpenHelper;
import com.chuger.bithdayapp.model.domain.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.chuger.bithdayapp.model.db.UserOpenHelper.ALL_COLUMNS;
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
import static com.chuger.bithdayapp.model.db.UserOpenHelper.TABLE_USER;
import static com.chuger.bithdayapp.model.utils.StringUtils.isNotEmpty;
import static java.lang.Boolean.TRUE;

/**
 * User: Acer5740
 * Date: 13.02.12
 * Time: 10:21
 */
public class UserDataSource {
    private SQLiteDatabase database;
    private UserOpenHelper dbHelper;
    public static ListActivity listActivity;
    private static final String TAG = UserDataSource.class.getSimpleName();
    private static final List<UserDataSource> instances = new ArrayList<UserDataSource>();
    private static UserDataSource mainDataSource;

    private UserDataSource() {
    }

    public static UserDataSource getInstance() {
        final UserDataSource userDataSource = new UserDataSource();
        instances.add(userDataSource);
        return userDataSource;
    }

    public static UserDataSource getMainDataSource() {
        return mainDataSource;
    }

    public static void onInit(final ListActivity listActivity) {
        UserDataSource.listActivity = listActivity;
        mainDataSource = getInstance();
    }

    public static void closeInstances() {
        for (final UserDataSource userDataSource : instances) {
            userDataSource.close();
        }
    }

    public ListActivity getListActivity() {
        return listActivity;
    }

    public static void refreshListActivity() {
        if (listActivity != null) {
            final CursorAdapter cursorAdapter = (CursorAdapter) listActivity.getListAdapter();
            cursorAdapter.getCursor().requery();
        }
    }

    public void openWrite() throws SQLException {
        dbHelper = new UserOpenHelper(listActivity);
        database = dbHelper.getWritableDatabase();
    }

    public void openRead() throws SQLException {
        dbHelper = new UserOpenHelper(listActivity);
        database = dbHelper.getReadableDatabase();
    }

    public void close() {
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    public ContentValues contentValuesFromUser(final User user) {
        final ContentValues values = new ContentValues();
        final Long id = user.getId();
        if (id != null) {
            values.put(COLUMN_ID, id);
        }
        final Long facebookId = user.getFacebookId();
        if (facebookId != null) {
            values.put(COLUMN_FB_ID, facebookId);
        }
        final Long vkontakteId = user.getVkontakteId();
        if (vkontakteId != null) {
            values.put(COLUMN_VK_ID, vkontakteId);
        }
        final String googleId = user.getGoogleId();
        if (isNotEmpty(googleId)) {
            values.put(COLUMN_GOOGLE_ID, googleId);
        }
        final String lastName = user.getLastName();
        if (isNotEmpty(lastName)) {
            values.put(COLUMN_LAST_NAME, lastName.trim());
        }
        final String firstName = user.getFirstName();
        if (isNotEmpty(firstName)) {
            values.put(COLUMN_FIRST_NAME, firstName.trim());
        }
        final String additionalName = user.getAdditionalName();
        if (isNotEmpty(additionalName)) {
            values.put(COLUMN_ADDITIONAL_NAME, additionalName.trim());
        }
        final String title = user.getTitle();
        if (isNotEmpty(title)) {
            values.put(COLUMN_TITLE, title.trim());
        }
        final String picUrl = user.getPicUrl();
        if (isNotEmpty(picUrl)) {
            values.put(COLUMN_PIC_URL, picUrl);
        }
        final Date birthday = user.getBirthday();
        if (birthday != null) {
            values.put(COLUMN_BIRTHDAY_DATE, birthday.getTime());
        }
        final Date updated = user.getUpdated();
        if (updated != null) {
            values.put(COLUMN_UPDATED, updated.getTime());
        }
        final Integer yearCount = user.getYearCount();
        if (yearCount != null) {
            values.put(COLUMN_YEAR_COUNT, yearCount);
        }
        final Integer dayCount = user.getDayCount();
        if (dayCount != null) {
            values.put(COLUMN_DAY_COUNT, dayCount);
        }
        values.put(COLUMN_YEAR_UNKNOWN, TRUE.equals(user.getYearUnknown()));

        return values;
    }

    public User createUser(final User user) {
        final ContentValues contentValues = contentValuesFromUser(user);
        final Long id = database.insert(TABLE_USER, null, contentValues);
        final Cursor cursor = database.query(TABLE_USER, ALL_COLUMNS, COLUMN_ID + " = " + id, null, null, null, null);
        cursor.moveToFirst();
        final User createdUser = new CursorHelper(cursor).getUser();
        Log.d(TAG, "Create user complete: %s" + createdUser.toString());
        cursor.close();
        return createdUser;
    }

    public void updateUser(final User user) {
        final ContentValues contentValues = contentValuesFromUser(user);
        database.update(TABLE_USER, contentValues, COLUMN_ID + " = " + user.getId(), null);
    }

    public Cursor getAllUserCursor() {
        return database.query(TABLE_USER, ALL_COLUMNS, null, null, null, null, COLUMN_DAY_COUNT + " ASC");
    }

    public User findByFbUid(final Long uid) {
        final Cursor cursor = database.query(TABLE_USER, ALL_COLUMNS, COLUMN_FB_ID + "=" + uid, null, null, null, null);
        return findUser(cursor);
    }

    public User findByVkUid(final Long uid) {
        final Cursor cursor = database.query(TABLE_USER, ALL_COLUMNS, COLUMN_VK_ID + "=" + uid, null, null, null, null);
        return findUser(cursor);
    }

    public User findByGoogleId(final String id) {
        final Cursor cursor =
                database.query(TABLE_USER, ALL_COLUMNS, COLUMN_GOOGLE_ID + "='" + id + "'", null, null, null, null);
        return findUser(cursor);
    }

    public User findByid(final Long uid) {
        final Cursor cursor = database.query(TABLE_USER, ALL_COLUMNS, COLUMN_ID + "=" + uid, null, null, null, null);
        return findUser(cursor);
    }

    public User findUser(final Cursor cursor) {
        cursor.moveToFirst();
        final User user = !cursor.isAfterLast() ? new CursorHelper(cursor).getUser() : null;
        cursor.close();
        return user;
    }

    public void deleteAllData() {
        database.execSQL("DELETE FROM " + TABLE_USER + ";");
    }
}
