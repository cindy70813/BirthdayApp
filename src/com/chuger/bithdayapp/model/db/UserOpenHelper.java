package com.chuger.bithdayapp.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static java.lang.String.format;

/**
 * User: Acer5740
 * Date: 04.02.12
 * Time: 3:06
 */
public class UserOpenHelper extends SQLiteOpenHelper {

    private final String TAG = UserOpenHelper.class.getSimpleName();

    public static final String DB_NAME = "birthday_db";
    public static final int DB_VERSION = 14;

    public static final String TABLE_USER = "user";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_FB_ID = "fb_id";
    public static final String COLUMN_VK_ID = "vk_id";
    public static final String COLUMN_LAST_NAME = "last_name";
    public static final String COLUMN_FIRST_NAME = "first_name";
    public static final String COLUMN_PIC_URL = "pic_url";
    public static final String COLUMN_BIRTHDAY_DATE = "birthday_date";
    public static final String COLUMN_YEAR_UNKNOWN = "year_unknown";
    public static final String COLUMN_UPDATED = "updated";
    public static final String COLUMN_YEAR_COUNT = "year_count";
    public static final String COLUMN_DAY_COUNT = "day_count";

    public static final String[] ALL_COLUMNS =
            {COLUMN_ID, COLUMN_FB_ID, COLUMN_VK_ID, COLUMN_LAST_NAME, COLUMN_FIRST_NAME, COLUMN_PIC_URL, COLUMN_PIC_URL,
                    COLUMN_BIRTHDAY_DATE, COLUMN_YEAR_UNKNOWN, COLUMN_UPDATED, COLUMN_YEAR_COUNT, COLUMN_DAY_COUNT};

    private static final String CREATE_TABLE =
            format("create table %s ( %s integer primary key autoincrement, %s INTEGER, %s INTEGER, %s TEXT, %s TEXT, %s TEXT, %s INTEGER, %s INTEGER, %s INTEGER, %s INTEGER, %s INTEGER)",
                    TABLE_USER, COLUMN_ID, COLUMN_FB_ID, COLUMN_VK_ID, COLUMN_LAST_NAME, COLUMN_FIRST_NAME,
                    COLUMN_PIC_URL, COLUMN_BIRTHDAY_DATE, COLUMN_YEAR_UNKNOWN, COLUMN_UPDATED, COLUMN_YEAR_COUNT,
                    COLUMN_DAY_COUNT);

    public UserOpenHelper(final Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(final SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        Log.w(TAG, format("Upgrading database from version %d to %d, which will destroy all old data", oldVersion,
                newVersion));
        db.execSQL(format("DROP TABLE IF EXISTS %s", TABLE_USER));
        onCreate(db);
    }
}
