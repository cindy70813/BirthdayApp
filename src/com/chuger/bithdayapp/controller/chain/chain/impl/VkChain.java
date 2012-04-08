package com.chuger.bithdayapp.controller.chain.chain.impl;

import com.chuger.bithdayapp.controller.chain.auth.responseListener.AuthListener;
import com.chuger.bithdayapp.controller.chain.auth.responseListener.impl.VkAuthListener;
import com.chuger.bithdayapp.controller.chain.birthday.request.BirthdayCaller;
import com.chuger.bithdayapp.controller.chain.birthday.request.impl.VkBirthdayCaller;
import com.chuger.bithdayapp.controller.chain.birthday.responseListener.BirthdayResponse;
import com.chuger.bithdayapp.controller.chain.birthday.responseListener.impl.VkBirthdayResponse;
import com.chuger.bithdayapp.controller.chain.chain.AbstractChain;
import com.chuger.bithdayapp.controller.chain.auth.request.AuthRequest;
import com.chuger.bithdayapp.controller.chain.auth.request.impl.VkAuthRequest;

/**
 * User: Acer5740
 * Date: 25.02.12
 * Time: 4:03
 */
public class VkChain extends AbstractChain {

    private final AuthRequest authRequest = new VkAuthRequest(this);
    private final AuthListener authListener = new VkAuthListener(this);
    private final BirthdayCaller birthdayCaller = new VkBirthdayCaller(this);
    private final BirthdayResponse birthdayResponse = new VkBirthdayResponse(this);
    private static final String APP_ID = "2815097";
    public static final String[] PERMISSIONS = {"friends"};

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
}
