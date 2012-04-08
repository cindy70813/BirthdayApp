package com.chuger.bithdayapp.controller.chain.auth.responseListener.impl;

import android.os.Bundle;
import com.chuger.bithdayapp.controller.chain.auth.responseListener.AbstractAuthListener;
import com.chuger.bithdayapp.controller.chain.chain.Chain;

public class FbAuthListener extends AbstractAuthListener {
    public FbAuthListener(Chain chain) {
        super(chain);
    }

    @Override
    public void onComplete(final Bundle values) {
        final Chain chain = getChain();
        final String accessToken = values.getString(chain.getAccessTokenAlias());
        chain.setAccessToken(accessToken);
        chain.getBirthdayCaller().requestBirthday();
    }
}
