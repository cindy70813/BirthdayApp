package com.chuger.bithdayapp.controller.chain.birthday.request;

import android.util.Log;
import com.chuger.bithdayapp.controller.chain.chain.Chain;
import com.facebook.android.Util;

import java.io.IOException;

/**
 * User: Acer5740
 * Date: 24.02.12
 * Time: 10:43
 */
public abstract class AbstractBirthdayCaller implements BirthdayCaller {
    private static final String TAG = AbstractBirthdayCaller.class.getSimpleName();

    private Chain chain;

    @Override
    public Chain getChain() {
        return chain;
    }

    public AbstractBirthdayCaller(Chain chain) {
        this.chain = chain;
    }

    @Override
    public void requestBirthday() {
        new Runnable() {
            @Override
            public void run() {
                try {
                    final String response = Util.openUrl(getRequestUrl(), "GET", getRequestParams());
                    getChain().getBirthdayResponse().processResponse(response);
                } catch (IOException e) {
                    Log.d(TAG, e.getMessage(), e);
                    e.printStackTrace();
                }
            }
        }.run();
    }

}
