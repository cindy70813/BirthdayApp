package com.chuger.bithdayapp.view.activity.adapter;

import android.app.Activity;
import android.database.Cursor;
import android.util.Log;
import com.chuger.bithdayapp.model.db.CursorHelper;
import com.chuger.bithdayapp.model.domain.User;
import com.chuger.bithdayapp.model.utils.BirthdayUtils;
import com.chuger.bithdayapp.model.utils.DateTimeUtils;
import com.fedorvlasov.lazylist.ImageLoader;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.WeakHashMap;

import static java.lang.String.valueOf;

/**
 * User: Acer5740
 * Date: 18.02.12
 * Time: 6:05
 */
public class UserInfoLoader {
    private Map<Integer, User> userMap;
    private final Activity activity;
//    private final ImageLoader imageLoader;

    public UserInfoLoader(final Activity activity) {
        this.activity = activity;
//        imageLoader = new ImageLoader(activity);
        userMap = Collections.synchronizedMap(new WeakHashMap<Integer, User>());
    }

    public void displayUser(final ViewHolder viewHolder, final Cursor cursor) {
        final int position = cursor.getPosition();
        if (userMap.containsKey(position)) {
            activity.runOnUiThread(new UserDisplayThread(viewHolder, userMap.get(position)));
        } else {
            new UserLoadThread(viewHolder, cursor).run();
        }
    }

    class UserLoadThread implements Runnable {

        private final ViewHolder viewHolder;
        private final Cursor cursor;

        public UserLoadThread(final ViewHolder viewHolder, final Cursor cursor) {
            this.viewHolder = viewHolder;
            this.cursor = cursor;
        }

        @Override
        public void run() {
            final User user = new CursorHelper(cursor).getUser();
            BirthdayUtils.updateIfNeeded(user);
            userMap.put(cursor.getPosition(), user);
            activity.runOnUiThread(new UserDisplayThread(viewHolder, user));
        }
    }

    class UserDisplayThread implements Runnable {

        private final ViewHolder viewHolder;
        private final User user;
        private final String TAG = UserDisplayThread.class.getSimpleName();

        public UserDisplayThread(final ViewHolder viewHolder, final User user) {
            this.viewHolder = viewHolder;
            this.user = user;
        }

        @Override
        public void run() {
            try {
                if (user != null) {
                    final Date birthdayDate = user.getBirthday();

                    if (birthdayDate != null) {
                        final boolean isUnknownYear = user.getYearUnknown();
                        final DateTimeFormatter formatter =
                                isUnknownYear ? DateTimeUtils.SHORT_DATE_FORMAT : DateTimeUtils.LONG_DATE_FORMAT;
                        final Integer yearCount = user.getYearCount();
                        final Integer dayCount = user.getDayCount();
                        viewHolder.yearCount.setText(
                                !isUnknownYear && yearCount != null && yearCount > 0 ? yearCount + " year" : "n/a");
                        viewHolder.dayCount.setText(dayCount != null ? valueOf(dayCount) : "TODAY");
                        viewHolder.birthday.setText(new DateTime(birthdayDate).toString(formatter));
                    }

                    final String surnameName = user.getLastName() + " " + user.getFirstName();
                    viewHolder.userInfo.setText(surnameName.trim());
//                    imageLoader.displayImage(user.getPicUrl(), viewHolder.imageView);
                }
            } catch (NullPointerException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }
}
