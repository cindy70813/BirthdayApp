package com.chuger.bithdayapp.view.activity.asyncTask;

import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import com.chuger.bithdayapp.model.dataSource.UserDataSource;
import com.chuger.bithdayapp.view.activity.adapter.UsersCursorAdapter;

import java.sql.SQLException;

import static com.chuger.bithdayapp.model.dataSource.UserDataSource.getMainDataSource;
import static com.chuger.bithdayapp.model.dataSource.UserDataSource.listActivity;

/**
 * User: Acer5740
 * Date: 18.02.12
 * Time: 9:51
 */
public class ConnectToDatabaseTask extends AsyncTask<Void, Void, Cursor> {
    private final static String TAG = ConnectToDatabaseTask.class.getSimpleName();

    @Override
    protected Cursor doInBackground(final Void... voids) {
        try {
            final UserDataSource userDataSource = getMainDataSource();
            userDataSource.openRead();
            final Cursor cursor = userDataSource.getAllUserCursor();
            listActivity.startManagingCursor(cursor);
            return cursor;
        } catch (SQLException e) {
            Log.e(TAG, "Cannot open database");
        }
        return null;
    }

    @Override
    protected void onPostExecute(final Cursor cursor) {
        final UsersCursorAdapter cursorAdapter = new UsersCursorAdapter(listActivity, cursor);
        listActivity.setListAdapter(cursorAdapter);
    }
}