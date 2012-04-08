package com.chuger.bithdayapp.controller.chain.auth.responseListener.impl;

import android.util.Log;
import com.chuger.bithdayapp.controller.chain.auth.responseListener.AbstractAuthListener;
import com.chuger.bithdayapp.controller.chain.chain.Chain;

/**
 * User: Acer5740
 * Date: 24.02.12
 * Time: 10:16
 */
public class VkAuthListener extends AbstractAuthListener{
    private static final String TAG = VkAuthListener.class.getSimpleName();

    public VkAuthListener(Chain chain) {
        super(chain);
    }

    @Override
    public void onAuthSucceed() {
        getChain().getBirthdayCaller().requestBirthday();
    }

    @Override
    public void onAuthFail(String error) {
        Log.e(TAG, error);
    }
}
