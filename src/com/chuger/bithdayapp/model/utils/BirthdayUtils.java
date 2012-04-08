package com.chuger.bithdayapp.model.utils;

import android.util.Log;
import com.chuger.bithdayapp.controller.chain.birthday.responseListener.parser.AbstractBirthdayParser;
import com.chuger.bithdayapp.model.dataSource.UserDataSource;
import com.chuger.bithdayapp.model.domain.User;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.ReadableInstant;
import org.joda.time.Years;

import java.sql.SQLException;
import java.util.Date;

/**
 * User: Acer5740
 * Date: 17.02.12
 * Time: 14:28
 */
public final class BirthdayUtils {
    private static final String TAG = BirthdayUtils.class.getSimpleName();

    public static User setBirthday(User user, AbstractBirthdayParser bdayParser) {
        if (bdayParser.notNull()) {
            final boolean isUnknownYear = bdayParser.isUnknownYear();
            final DateTime birthday = bdayParser.getBirthday();
            if (birthday != null) {
                user.setBirthday(birthday.toDate());
                user.setYearUnknown(isUnknownYear);
                updateCounts(user);
            }
        }
        return user;
    }

    public static void updateCounts(final User user) {
        final Date birthdayDate = user.getBirthday();
        if (birthdayDate != null) {
            final DateTime now = DateTime.now();
            final DateTime birthday = new DateTime(birthdayDate);
            if (!user.getYearUnknown()) {
                final int years = Years.yearsBetween(birthday, now).getYears();
                user.setYearCount(years + 1);
            } else {
                user.setYearCount(null);
            }
            DateTime birthdayDelta = birthday.year().setCopy(now.getYear());
            if (birthdayDelta.isBefore(now)) {
                birthdayDelta = birthdayDelta.plusYears(1);
            }

            final int countDays = Days.daysBetween(now, birthdayDelta).getDays();
            user.setDayCount(countDays + 1);
            user.setUpdated(now.toDate());
        }
    }

    public static boolean needUpdated(final Date lastUpdated) {
        return lastUpdated == null || needUpdated(new DateTime(lastUpdated));
    }

    public static boolean needUpdated(final ReadableInstant lastUpdated) {
        final DateTime now = DateTime.now();
        final int countDays = Days.daysBetween(lastUpdated, now).getDays();
        if (countDays > 0) {
            Log.d("ALOE",
                    String.format("need updated now: %s lastUpdated: %s ", now.toString(), lastUpdated.toString()));
            return true;
        } else {
            return false;
        }

    }

    public static void updateIfNeeded(final User user) {
        if (needUpdated(user.getUpdated())) {
            updateCounts(user);

            final UserDataSource userDataSource = UserDataSource.getInstance();
            try {
                userDataSource.openWrite();
                userDataSource.updateUser(user);
            } catch (SQLException e) {
                Log.e(TAG, e.getMessage(), e);
            }
            userDataSource.close();
        }
    }
}
