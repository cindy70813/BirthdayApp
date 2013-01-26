package com.chuger.bithdayapp.controller.chain.chain;

import com.chuger.bithdayapp.controller.chain.auth.request.AuthRequest;
import com.chuger.bithdayapp.controller.chain.auth.responseListener.AuthListener;
import com.chuger.bithdayapp.controller.chain.birthday.request.BirthdayCaller;
import com.chuger.bithdayapp.controller.chain.birthday.responseListener.BirthdayResponse;


/**
 * User: Acer5740
 * Date: 25.02.12
 * Time: 4:20
 */
public interface Chain {

    AuthRequest getAuthRequest();

    AuthListener getAuthListener();

    BirthdayCaller getBirthdayCaller();

    BirthdayResponse getBirthdayResponse();

    boolean isAuthorized();

    String getAccessToken();

    void setAccessToken(final String accessToken);

    String getAppId();

    String[] getPermissions();

    String getAccessTokenAlias();
}
