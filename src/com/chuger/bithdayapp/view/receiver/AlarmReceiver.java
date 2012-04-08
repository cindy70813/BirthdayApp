package com.chuger.bithdayapp.view.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.chuger.R;

public class AlarmReceiver extends BroadcastReceiver {

    private static int NOTIFICATION_ID = 1;

    @Override
    public void onReceive(final Context context, final Intent intent) {

        final NotificationManager manger = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        final Notification notification = new Notification(R.drawable.icon, "Combi Note", System.currentTimeMillis());
        final PendingIntent contentIntent =
                PendingIntent.getActivity(context, NOTIFICATION_ID, new Intent(context, AlarmReceiver.class), 0);
        final Bundle extras = intent.getExtras();
        final String title = extras.getString("title");
        final String note = extras.getString("note");
        notification.setLatestEventInfo(context, note, title, contentIntent);
        notification.flags = Notification.FLAG_INSISTENT;
        notification.defaults |= Notification.DEFAULT_SOUND;
        //here we set the default sound for our
        //notification

        // The PendingIntent to launch our activity if the user selects this notification
        manger.notify(NOTIFICATION_ID++, notification);
    }
}