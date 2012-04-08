package com.chuger.bithdayapp.controller.chain.auth.responseListener.impl;

import android.os.Bundle;
import android.util.Log;
import com.chuger.bithdayapp.controller.chain.auth.responseListener.AbstractAuthListener;
import com.chuger.bithdayapp.controller.chain.chain.Chain;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class FbAuthListener extends AbstractAuthListener implements DialogListener {

    private static final String TAG = FbAuthListener.class.getSimpleName();

    public FbAuthListener(Chain chain) {
        super(chain);
    }

    @Override
    public void onAuthSucceed() {
        getChain().getBirthdayCaller().requestBirthday();
    }

    @Override
    public void onFacebookError(final FacebookError e) {
        Log.e(TAG, e.getMessage(), e);
        onAuthFail(e.getMessage());
    }

    @Override
    public void onError(final DialogError e) {
        Log.e(TAG, e.getMessage(), e);
        onAuthFail(e.getMessage());
    }

    @Override
    public void onComplete(final Bundle values) {
        final String accessToken = values.getString(Facebook.TOKEN);
        getChain().setAccessToken(accessToken);
        onAuthSucceed();
    }

    @Override
    public void onCancel() {
    }

    @Override
    public void onAuthFail(String error) {
    }
}
