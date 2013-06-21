package com.chuger.bithdayapp.controller.chain.birthday.responseListener.impl;

import com.chuger.bithdayapp.controller.chain.birthday.responseListener.AbstractBdayResponse;
import com.chuger.bithdayapp.controller.chain.birthday.responseListener.json.AliasHolder;
import com.chuger.bithdayapp.controller.chain.birthday.responseListener.json.impl.FbAliasHolder;
import com.chuger.bithdayapp.controller.chain.birthday.responseListener.parser.AbstractBirthdayParser;
import com.chuger.bithdayapp.controller.chain.birthday.responseListener.parser.impl.FbBdayParser;
import com.chuger.bithdayapp.controller.chain.chain.Chain;
import com.chuger.bithdayapp.model.dataSource.UserDataSource;
import com.chuger.bithdayapp.model.domain.User;

import java.io.Serializable;

/**
 * User: Acer5740
 * Date: 24.02.12
 * Time: 10:51
 */
public class FbBirthdayResponse extends AbstractBdayResponse {

    @Override
    public AliasHolder getAliasHolder() {
        return new FbAliasHolder();
    }

    public FbBirthdayResponse(Chain chain) {
        super(chain);
    }

    @Override
    protected AbstractBirthdayParser getAbstractBdayParser(String bdayString) {
        return new FbBdayParser(bdayString);
    }

    @Override
    protected <T extends Serializable> void setUid(User user, T uid) {
        user.setFacebookId((Long) uid);
    }

    @Override
    protected <T extends Serializable> User findUserById(UserDataSource readDataSource, T uid){
        return readDataSource.findByFbUid((Long)uid);
    }
}
