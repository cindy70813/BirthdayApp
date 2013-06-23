package com.chuger.bithdayapp.controller.chain.birthday.responseListener.json.impl;

import com.chuger.bithdayapp.controller.chain.birthday.responseListener.json.AliasHolder;

/**
 * User: Acer5740
 * Date: 26.02.12
 * Time: 9:39
 */
public final class VkAliasHolder implements AliasHolder{
    @Override
    public String getJsonRootAlias() {
        return "response";
    }

    @Override
    public String getIdAlias() {
        return "uid";
    }

    @Override
    public String getFirstNameAlias() {
        return "first_name";
    }

    @Override
    public String getLastNameAlias() {
        return "last_name";
    }

    @Override
    public String getPicUrlAlias() {
        return "photo";
    }

    @Override
    public String getBirthdayAlias() {
        return "bdate";
    }

    @Override
    public String getTitleAlias() {
        return null;
    }

    @Override
    public String getAdditionalNameAlias() {
        return null;
    }
}
