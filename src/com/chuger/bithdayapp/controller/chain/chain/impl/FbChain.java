package com.chuger.bithdayapp.controller.chain.chain.impl;

import com.chuger.bithdayapp.controller.chain.auth.responseListener.AuthListener;
import com.chuger.bithdayapp.controller.chain.auth.responseListener.impl.FbAuthListener;
import com.chuger.bithdayapp.controller.chain.birthday.request.BirthdayCaller;
import com.chuger.bithdayapp.controller.chain.birthday.request.impl.FbBirthdayCaller;
import com.chuger.bithdayapp.controller.chain.birthday.responseListener.BirthdayResponse;
import com.chuger.bithdayapp.controller.chain.birthday.responseListener.impl.FbBirthdayResponse;
import com.chuger.bithdayapp.controller.chain.chain.AbstractChain;
import com.chuger.bithdayapp.controller.chain.auth.request.AuthRequest;
import com.chuger.bithdayapp.controller.chain.auth.request.impl.FbAuthRequest;


/**
 * User: Acer5740
 * Date: 25.02.12
 * Time: 4:13
 */
public class FbChain extends AbstractChain {
    private final AuthRequest authRequest = new FbAuthRequest(this);
    private final AuthListener authListener = new FbAuthListener(this);
    private final BirthdayCaller birthdayCaller = new FbBirthdayCaller(this);
    private final BirthdayResponse birthdayResponse = new FbBirthdayResponse(this);
    private static final String APP_ID = "377146995633351";
    public static final String[] PERMISSIONS = {"publish_stream", "friends_birthday"};
    public static final String TOKEN = "access_token";

    public AuthRequest getAuthRequest() {
        return authRequest;
    }

    public AuthListener getAuthListener() {
        return authListener;
    }

    public BirthdayCaller getBirthdayCaller() {
        return birthdayCaller;
    }

    public BirthdayResponse getBirthdayResponse() {
        return birthdayResponse;
    }

    @Override
    public String getAppId() {
        return APP_ID;
    }

    @Override
    public String[] getPermissions() {
        return PERMISSIONS;
    }

    @Override
    public String getAccessTokenAlias() {
        return TOKEN;
    }
}
