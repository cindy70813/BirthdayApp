package com.chuger.bithdayapp.controller.chain.auth.request.impl;

import android.content.Context;
import com.chuger.bithdayapp.controller.chain.auth.request.AbstractRequestListener;
import com.chuger.bithdayapp.controller.chain.chain.Chain;
import com.chuger.bithdayapp.view.auth.AuthDialog;

/**
 * User: Acer5740
 * Date: 25.02.12
 * Time: 4:04
 */
public class VkAuthRequest extends AbstractRequestListener {
    public static final String AUTH_URL = "http://oauth.vk.com/oauth/authorize?" +
            "client_id=%s" +
            "&scope=%s" +
            "&redirect_uri=blank.html" +
            "&display=%s" +
            "&response_type=token";
    public static final String SETTINGS = "friends";
    public static final String DISPLAY = "touch";

    public VkAuthRequest(Chain chain) {
        super(chain);
    }

    @Override
    public void doCallBirthdays(final Context context) {
        getChain().getBirthdayCaller().requestBirthday();
    }

    @Override
    public void doAuthorize(final Context context) {
        final Chain chain = getChain();
        final String authUrl = String.format(AUTH_URL, chain.getAppId(), SETTINGS, DISPLAY);
        new AuthDialog(context, authUrl, chain.getAuthListener()).show();
    }

    @Override
    public boolean isAuthorized() {
        return getChain().isAuthorized();
    }
}
