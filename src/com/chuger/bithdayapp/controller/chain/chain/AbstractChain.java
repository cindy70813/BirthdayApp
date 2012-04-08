package com.chuger.bithdayapp.controller.chain.chain;

import com.chuger.bithdayapp.model.utils.StringUtils;

/**
 * User: Acer5740
 * Date: 25.02.12
 * Time: 3:58
 */
public abstract class AbstractChain implements Chain {
    private String accessToken;

    public boolean isAuthorized() {
        return StringUtils.isNotEmpty(accessToken);
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
