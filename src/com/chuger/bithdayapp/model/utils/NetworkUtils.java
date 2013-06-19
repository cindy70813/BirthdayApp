package com.chuger.bithdayapp.model.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * User: chgv
 * Date: 19.06.13
 * Time: 15:51
 */
public final class NetworkUtils {
    private NetworkUtils() {
    }

    public static boolean isOnline(final Activity activity) {
        boolean result = false;
        if (activity != null) {
            ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                result = netInfo != null && netInfo.isConnected();
            }
        }
        return result;
    }
}
