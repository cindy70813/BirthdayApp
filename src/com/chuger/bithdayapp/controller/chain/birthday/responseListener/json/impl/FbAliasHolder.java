package com.chuger.bithdayapp.controller.chain.birthday.responseListener.json.impl;

import com.chuger.bithdayapp.controller.chain.birthday.responseListener.json.AliasHolder;

/**
 * User: Acer5740
 * Date: 26.02.12
 * Time: 9:36
 */
public final class FbAliasHolder implements AliasHolder{

    @Override
    public String getJsonRootAlias() {
        return "data";
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
        return "pic_square";
    }

    @Override
    public String getBirthdayAlias() {
        return "birthday_date";
    }
}
