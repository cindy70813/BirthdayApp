package com.chuger.bithdayapp.controller.chain.auth.request.impl;

import android.content.Context;
import com.chuger.bithdayapp.controller.chain.auth.request.AbstractRequestListener;
import com.chuger.bithdayapp.controller.chain.chain.Chain;
import com.facebook.android.Facebook;

import static com.facebook.android.Facebook.FORCE_DIALOG_AUTH;

/**
 * User: Acer5740
 * Date: 24.02.12
 * Time: 11:10
 */
public class FbAuthRequest extends AbstractRequestListener {

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
        final Facebook.DialogListener authListener = (Facebook.DialogListener) chain.getAuthListener();
        facebook.authorize(context, chain.getPermissions(), FORCE_DIALOG_AUTH, authListener);
    }

    @Override
    public boolean isAuthorized() {
        return facebook.isSessionValid();
    }
}
