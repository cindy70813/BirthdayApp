package com.chuger.bithdayapp.controller.chain.birthday.request.impl;

import android.os.Bundle;
import com.chuger.bithdayapp.controller.chain.birthday.request.AbstractBirthdayCaller;
import com.chuger.bithdayapp.controller.chain.chain.Chain;

/**
 * User: Acer5740
 * Date: 24.02.12
 * Time: 10:48
 */
public class VkBirthdayCaller extends AbstractBirthdayCaller{

    public VkBirthdayCaller(Chain chain) {
        super(chain);
    }

    @Override
    public String getRequestUrl() {
        return "https://api.vk.com/method/friends.get";
    }

    @Override
    public Bundle getRequestParams() {
        Bundle params = new Bundle();
        params.putString("fields", "uid,first_name,last_name,bdate,photo");
        params.putString("access_token", getChain().getAccessToken());
        return params;
    }
}
