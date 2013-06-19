package com.chuger.bithdayapp.controller.chain.birthday.request.impl;

import android.os.Bundle;
import com.chuger.bithdayapp.controller.chain.birthday.request.AbstractBirthdayCaller;
import com.chuger.bithdayapp.controller.chain.chain.Chain;

/**
 * User: Администратор
 * Date: 17.02.13
 * Time: 16:32
 */
public class GoogleBirthdayCaller extends AbstractBirthdayCaller {
    public GoogleBirthdayCaller(Chain chain) {
        super(chain);
    }

    @Override
    public String getRequestUrl() {
        return "https://www.google.com/m8/feeds/contacts/default/full";
    }

    @Override
    public Bundle getRequestParams() {
        Bundle params = new Bundle();
        params.putString("access_token", getChain().getAccessToken());
        params.putString("alt", "json");
        return params;
    }
}
