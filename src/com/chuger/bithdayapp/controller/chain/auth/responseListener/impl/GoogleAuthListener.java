package com.chuger.bithdayapp.controller.chain.auth.responseListener.impl;

import android.os.Bundle;
import com.chuger.bithdayapp.controller.chain.auth.responseListener.AbstractAuthListener;
import com.chuger.bithdayapp.controller.chain.chain.Chain;

/**
 * Created with IntelliJ IDEA.
 * User: ChuGer
 * Date: 08.04.12
 * Time: 13:49
 */
public class GoogleAuthListener extends AbstractAuthListener {
    public GoogleAuthListener(Chain chain) {
        super(chain);
    }

    @Override
    public void onComplete(Bundle values) {
        final Chain chain = getChain();
        final String accessToken = values.getString(chain.getAccessTokenAlias());
        chain.setAccessToken(accessToken);
        chain.getBirthdayCaller().requestBirthday();
    }
}
