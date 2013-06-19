package com.chuger.bithdayapp.controller.chain.chain.impl;

import com.chuger.bithdayapp.controller.chain.auth.request.AuthRequest;
import com.chuger.bithdayapp.controller.chain.auth.request.impl.GoogleAuthRequest;
import com.chuger.bithdayapp.controller.chain.auth.responseListener.AuthListener;
import com.chuger.bithdayapp.controller.chain.auth.responseListener.impl.GoogleAuthListener;
import com.chuger.bithdayapp.controller.chain.birthday.request.BirthdayCaller;
import com.chuger.bithdayapp.controller.chain.birthday.request.impl.GoogleBirthdayCaller;
import com.chuger.bithdayapp.controller.chain.birthday.responseListener.BirthdayResponse;
import com.chuger.bithdayapp.controller.chain.birthday.responseListener.impl.GoogleBirthdayResponse;
import com.chuger.bithdayapp.controller.chain.chain.AbstractChain;

/**
 * Created with IntelliJ IDEA.
 * User: ChuGer
 * Date: 08.04.12
 * Time: 13:28
 */
public class GoogleChain extends AbstractChain {
    private static final String APP_ID = "321839693270.apps.googleusercontent.com";
    private static final String CLIENT_SECRET = "VUyfdv1wR91_QDY27jaqSeUX";
    public static final String[] PERMISSIONS = {"friends"};
    public static final String TOKEN = "access_token";

    private final AuthRequest authRequest = new GoogleAuthRequest(this);
    private final AuthListener authListener = new GoogleAuthListener(this);
    private final BirthdayCaller birthdayCaller = new GoogleBirthdayCaller(this);
    private final BirthdayResponse birthdayResponse = new GoogleBirthdayResponse(this);

    @Override
    public BirthdayResponse getBirthdayResponse() {
        return birthdayResponse;
    }

    @Override
    public String getAppId() {
        return APP_ID;
    }

    @Override
    public AuthRequest getAuthRequest() {
        return authRequest;
    }

    @Override
    public AuthListener getAuthListener() {
        return authListener;
    }

    @Override
    public BirthdayCaller getBirthdayCaller() {
        return birthdayCaller;
    }

    @Override
    public String[] getPermissions() {
        return new String[0];
    }

    @Override
    public String getAccessTokenAlias() {
        return TOKEN;
    }
}
