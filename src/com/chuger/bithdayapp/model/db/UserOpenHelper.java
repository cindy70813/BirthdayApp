package com.chuger.bithdayapp.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.LinkedHashMap;
import java.util.Map;

import static java.lang.String.format;

/**
 * User: Acer5740
 * Date: 04.02.12
 * Time: 3:06
 */
public class UserOpenHelper extends SQLiteOpenHelper {

    private final String TAG = UserOpenHelper.class.getSimpleName();

    public static final String DB_NAME = "birthday_db";
    public static final int DB_VERSION = 16;

    public static final String TABLE_USER = "user";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_FB_ID = "fb_id";
    public static final String COLUMN_VK_ID = "vk_id";
    public static final String COLUMN_GOOGLE_ID = "google_id";
    public static final String COLUMN_LAST_NAME = "last_name";
    public static final String COLUMN_FIRST_NAME = "first_name";
    public static final String COLUMN_ADDITIONAL_NAME = "additional_name";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_PIC_URL = "pic_url";
    public static final String COLUMN_BIRTHDAY_DATE = "birthday_date";
    public static final String COLUMN_YEAR_UNKNOWN = "year_unknown";
    public static final String COLUMN_UPDATED = "updated";
    public static final String COLUMN_YEAR_COUNT = "year_count";
    public static final String COLUMN_DAY_COUNT = "day_count";

    public static Map<String, String> COLUMN_DEFINITIONS = new LinkedHashMap<String, String>() {
        {
            put(COLUMN_ID, "integer primary key autoincrement");
            put(COLUMN_FB_ID, "integer");
            put(COLUMN_VK_ID, "integer");
            put(COLUMN_GOOGLE_ID, "text");
            put(COLUMN_LAST_NAME, "text");
            put(COLUMN_FIRST_NAME, "text");
            put(COLUMN_ADDITIONAL_NAME, "text");
            put(COLUMN_TITLE, "text");
            put(COLUMN_TITLE, "text");
            put(COLUMN_PIC_URL, "text");
            put(COLUMN_BIRTHDAY_DATE, "text");
            put(COLUMN_YEAR_UNKNOWN, "integer");
            put(COLUMN_UPDATED, "integer");
            put(COLUMN_YEAR_COUNT, "integer");
            put(COLUMN_DAY_COUNT, "integer");
        }
    };

    public static final String[] ALL_COLUMNS = COLUMN_DEFINITIONS.keySet().toArray(new String[]{});
    public static final String SPACE = " ";

    public UserOpenHelper(final Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(final SQLiteDatabase sqLiteDatabase) {
        final StringBuilder builder = new StringBuilder();
        builder.append("create table ").append(TABLE_USER).append("(");

        String prefix = "";
        for (Map.Entry<String,String> entry : COLUMN_DEFINITIONS.entrySet()) {
            builder.append(prefix);
            builder.append(entry.getKey());
            builder.append(SPACE);
            builder.append(entry.getValue());
            prefix = ", ";
        }

        final String CREATE_SCRIPT = builder.append(")").toString();
        Log.i(TAG, CREATE_SCRIPT);

        sqLiteDatabase.execSQL(CREATE_SCRIPT);
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        Log.w(TAG, format("Upgrading database from version %d to %d, which will destroy all old data", oldVersion,
                newVersion));
        db.execSQL(format("DROP TABLE IF EXISTS %s", TABLE_USER));
        onCreate(db);
    }
}
