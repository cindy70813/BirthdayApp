package com.chuger.bithdayapp.controller.chain.birthday.request.impl;

import android.os.Bundle;
import com.chuger.bithdayapp.controller.chain.birthday.request.AbstractBirthdayCaller;
import com.chuger.bithdayapp.controller.chain.chain.Chain;

/**
 * User: Acer5740
 * Date: 25.02.12
 * Time: 4:15
 */
public class FbBirthdayCaller extends AbstractBirthdayCaller {
    private final static String FQL_BIRTHDAYS =
            "select uid, first_name, last_name, pic_square, birthday_date from user where uid in " +
                    "(select uid2 from friend where uid1=me())";
    public static final String TOKEN = "access_token";
    protected static String GRAPH_BASE_URL = "https://graph.facebook.com/";

    public FbBirthdayCaller(Chain chain) {
        super(chain);
    }

    @Override
    public String getRequestUrl() {
        return GRAPH_BASE_URL + "fql";
    }

    @Override
    public Bundle getRequestParams() {
        final Bundle params = new Bundle();
        params.putString("q", FQL_BIRTHDAYS);
        params.putString("format", "json");
        params.putString(TOKEN, getChain().getAccessToken());
        return params;
    }
}
