package com.chuger.bithdayapp.controller.chain.auth.responseListener.impl;

import android.os.Bundle;
import com.chuger.bithdayapp.controller.chain.auth.responseListener.AbstractAuthListener;
import com.chuger.bithdayapp.controller.chain.chain.Chain;

/**
 * User: Acer5740
 * Date: 24.02.12
 * Time: 10:16
 */
public class VkAuthListener extends AbstractAuthListener {
    public VkAuthListener(Chain chain) {
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
