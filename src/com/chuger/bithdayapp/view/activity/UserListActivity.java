package com.chuger.bithdayapp.view.activity;

import android.app.Dialog;
import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.chuger.R;
import com.chuger.bithdayapp.controller.chain.locator.ChainLocator;
import com.chuger.bithdayapp.model.dataSource.UserDataSource;
import com.chuger.bithdayapp.model.domain.User;
import com.chuger.bithdayapp.model.utils.DateTimeUtils;
import com.chuger.bithdayapp.model.utils.StringUtils;
import com.chuger.bithdayapp.view.activity.asyncTask.ConnectToDatabaseTask;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

import java.sql.SQLException;
import java.util.Date;

import static com.chuger.bithdayapp.controller.chain.locator.ChainLocator.getChain;
import static java.lang.String.valueOf;

/**
 * User: Acer5740
 * Date: 12.02.12
 * Time: 2:58
 */
public class UserListActivity extends ListActivity {
    private final static String TAG = UserListActivity.class.getSimpleName();

    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.app_name);
        setContentView(R.layout.list_layout);
        UserDataSource.onInit(this);
        new ConnectToDatabaseTask().execute();
    }

    @Override
    protected void onDestroy() {
        UserDataSource.closeInstances();
        super.onDestroy();
    }

    @Override
    protected void onListItemClick(final ListView listView, final View view, final int position, final long id) {
        try {
            final UserDataSource instance = UserDataSource.getInstance();
            instance.openRead();
            final User user = instance.findByid(id);
            if (user != null) {
                final Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.dialog);
                dialog.setTitle("User info:");
                dialog.setCancelable(true);

                final TextView userInfo = (TextView) dialog.findViewById(R.id.userInfo);
                final TextView dayCount = (TextView) dialog.findViewById(R.id.dayCount);
                final TextView yearCount = (TextView) dialog.findViewById(R.id.yearCount);
                final TextView birthday = (TextView) dialog.findViewById(R.id.birthday);
                final Button dialogBtn = (Button) dialog.findViewById(R.id.dialogBtn);

                final Date birthdayDate = user.getBirthday();

                if (birthdayDate != null) {
                    final boolean isUnknownYear = user.getYearUnknown();
                    final DateTimeFormatter formatter =
                            isUnknownYear ? DateTimeUtils.SHORT_DATE_FORMAT : DateTimeUtils.LONG_DATE_FORMAT;
                    final Integer yearCountInt = user.getYearCount();
                    final Integer dayCountInt = user.getDayCount();

                    userInfo.setText(user.getLastName() + " " + user.getFirstName());
                    yearCount.setText(
                            !isUnknownYear && yearCountInt != null && yearCountInt > 0 ? yearCountInt + " year" :
                                    "n/a");
                    dayCount.setText(dayCountInt != null ? valueOf(dayCountInt) : "TODAY");
                    birthday.setText(new DateTime(birthdayDate).toString(formatter));
                    dialogBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void onClick(final View view) {
        final int viewId = view.getId();
        final String chainAlias;
        switch (viewId) {
            case R.id.fbBtn:
                chainAlias = ChainLocator.FB_CHAIN_ALIAS;
                break;
            case R.id.vkBtn:
                chainAlias = ChainLocator.VK_CHAIN_ALIAS;
                break;
            default:
                chainAlias = null;
                Log.d(TAG, String.format("OnClick action for view with id[%d] not found", viewId));
        }
        if (StringUtils.isNotEmpty(chainAlias)) {
            getChain(chainAlias).getAuthRequest().onClick(view);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        super.onCreateOptionsMenu(menu);
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        final String chainAlias;
        final int itemId = item.getItemId();
        switch (itemId) {
            case R.id.fbMenuItem:
                chainAlias = ChainLocator.FB_CHAIN_ALIAS;
                break;
            case R.id.vkMenuItem:
                chainAlias = ChainLocator.VK_CHAIN_ALIAS;
                break;
            case R.id.clMenuItem:
                try {
                    final UserDataSource instance = UserDataSource.getInstance();
                    instance.openWrite();
                    instance.deleteAllData();
                    instance.close();
                    UserDataSource.refreshListActivity();
                } catch (SQLException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
                chainAlias = null;
                break;
            default:
                chainAlias = null;
                Log.d(TAG, String.format("OnClick action for view with id[%d] not found", itemId));
        }
        if (StringUtils.isNotEmpty(chainAlias)) {
            getChain(chainAlias).getAuthRequest().onClick(this.getListView());
        }
        return false;
    }

    //    private static final int NOTIFY_ID = 1;
    //
    //    private void setAlarm() {
    //        final Calendar calendar = Calendar.getInstance();
    //        calendar.add(Calendar.MINUTE, 1);
    //        //        calendar.set(Calendar.MONTH, 4);
    //        //        calendar.set(Calendar.YEAR, 2011);
    //        //        calendar.set(Calendar.DAY_OF_MONTH, 5);
    //        //        calendar.set(Calendar.HOUR_OF_DAY, 21);
    //        //        calendar.set(Calendar.MINUTE, 43);
    //        //calendar.set will set the alarm to trigger exactly at: 21:43, 5 May 2011
    //        //if you want to trigger the alarm after let's say 5 minutes after is activated you need to put
    //        //calendar.add(Calendar.MINUTE, 5);
    //        final Intent alarmIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
    //        alarmIntent.putExtra("title", "Title of our Notification");
    //        alarmIntent.putExtra("note", "Description of our  Notification");
    //        //HELLO_ID is a static variable that must be initialised at the BEGINNING OF CLASS with 1;
    //
    //        //example:protected static int HELLO_ID =1;
    //        final PendingIntent sender = PendingIntent.getBroadcast(getApplicationContext(), NOTIFY_ID, alarmIntent,
    //                PendingIntent.FLAG_UPDATE_CURRENT | Intent.FILL_IN_DATA);
    //        //VERY IMPORTANT TO SET FLAG_UPDATE_CURRENT... this will send correct extra's informations to
    //        //AlarmReceiver Class
    //        // Get the AlarmManager service
    //
    //        final AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
    //        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
    //    }
    //
    //    private void sendNotification() {
    //        final NotificationManager mNotificationManager =
    //                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    //        final int icon = android.R.drawable.sym_action_email;
    //        final CharSequence tickerText = "Hello Habrahabr";
    //        final long when = System.currentTimeMillis();
    //        final Notification notification = new Notification(icon, tickerText, when);
    //        final Context context = getApplicationContext();
    //        final CharSequence contentTitle = "Habrahabr";
    //        final CharSequence contentText = "Пример простого уведомления";
    //        final Intent notificationIntent = new Intent(this, UserListActivity.class);
    //        final PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
    //        notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
    //        mNotificationManager.notify(NOTIFY_ID, notification);
    //    }
    //
    //    private void sendMail() {
    //        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
    //
    //        emailIntent.setType("plain/text");
    //        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"i.chuger@gmail.com"});
    //        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Android Test");
    //        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Hello!");
    //
    //        startActivity(Intent.createChooser(emailIntent, "Отправка письма..."));
    //    }
}
