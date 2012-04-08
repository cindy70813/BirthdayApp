package com.chuger.bithdayapp.controller.chain.auth.request.impl;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.CookieSyncManager;
import com.chuger.bithdayapp.controller.chain.auth.request.AbstractRequestListener;
import com.chuger.bithdayapp.controller.chain.auth.responseListener.DialogListener;
import com.chuger.bithdayapp.controller.chain.chain.Chain;
import com.chuger.bithdayapp.view.auth.AuthDialog;
import com.facebook.android.Facebook;
import com.facebook.android.Util;

/**
 * User: Acer5740
 * Date: 24.02.12
 * Time: 11:10
 */
public class FbAuthRequest extends AbstractRequestListener {
    protected static String DIALOG_BASE_URL = "https://m.facebook.com/dialog/";
    public static final String REDIRECT_URI = "fbconnect://success";
    private static final String LOGIN = "oauth";

    private final Facebook facebook;

    public FbAuthRequest(Chain chain) {
        super(chain);
        facebook = new Facebook(getChain().getAppId());
    }

    @Override
    public void doCallBirthdays(final Context context) {
        final Chain chain = getChain();
        chain.setAccessToken(facebook.getAccessToken());
        chain.getBirthdayCaller().requestBirthday();
    }

    @Override
    public void doAuthorize(final Context context) {
        final Chain chain = getChain();
        startDialogAuth(context, chain.getPermissions(), chain.getAuthListener());
    }

    private void startDialogAuth(final Context activity, final String[] permissions, DialogListener listener) {
        final Bundle params = new Bundle();
        if (permissions.length > 0) {
            params.putString("scope", TextUtils.join(",", permissions));
        }
        CookieSyncManager.createInstance(activity);
        dialog(activity, LOGIN, params, listener);
    }

    public void dialog(final Context context, final String action, final Bundle params,
                       final DialogListener listener) {

        final String endpoint = DIALOG_BASE_URL + action;
        params.putString("display", "touch");
        params.putString("redirect_uri", REDIRECT_URI);

        final String appId = getChain().getAppId();
        if (action.equals(LOGIN)) {
            params.putString("type", "user_agent");
            params.putString("client_id", appId);
        } else {
            params.putString("app_id", appId);
        }

        final String url = endpoint + "?" + Util.encodeUrl(params);
        if (context.checkCallingOrSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            Util.showAlert(context, "Error", "Application requires permission to access the Internet");
        } else {
            new AuthDialog(context, url, listener).show();
        }
    }

    @Override
    public boolean isAuthorized() {
        return facebook.isSessionValid();
    }
}
