package com.chuger.bithdayapp.controller.chain.auth.request.impl;

import android.content.Context;
import com.chuger.bithdayapp.controller.chain.auth.request.AbstractRequestListener;
import com.chuger.bithdayapp.controller.chain.chain.Chain;
import com.chuger.bithdayapp.view.auth.AuthDialog;

/**
 * Created with IntelliJ IDEA.
 * User: ChuGer
 * Date: 08.04.12
 * Time: 13:27
 */
public class GoogleAuthRequest extends AbstractRequestListener {
    public static final String REDIRECT_URI2 = "urn:ietf:wg:oauth:2.0:oob";
    public static final String REDIRECT_URI = "http://localhost";
    public static final String SCOPE = "https://www.google.com/m8/feeds";
    public static final String AUTH_URL = "https://accounts.google.com/o/oauth2/auth?" +
            "scope=%s" +
            "&client_id=%s" +
            "&redirect_uri=%s" +
            "&response_type=code";

    public GoogleAuthRequest(Chain chain) {
        super(chain);
    }

    @Override
    public void doCallBirthdays(Context view) {
        getChain().getBirthdayCaller().requestBirthday();
    }

    @Override
    public void doAuthorize(Context context) {
        final Chain chain = getChain();
        final String authUrl = String.format(AUTH_URL, SCOPE, chain.getAppId(), REDIRECT_URI);
        new AuthDialog(context, authUrl, chain.getAuthListener()).show();
    }

    @Override
    public boolean isAuthorized() {
        return getChain().isAuthorized();
    }
}
