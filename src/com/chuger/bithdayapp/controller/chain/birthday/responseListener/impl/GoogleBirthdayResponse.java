package com.chuger.bithdayapp.controller.chain.birthday.responseListener.impl;

import com.chuger.bithdayapp.controller.chain.birthday.responseListener.AbstractBdayResponse;
import com.chuger.bithdayapp.controller.chain.birthday.responseListener.json.AliasHolder;
import com.chuger.bithdayapp.controller.chain.birthday.responseListener.parser.AbstractBirthdayParser;
import com.chuger.bithdayapp.controller.chain.chain.Chain;
import com.chuger.bithdayapp.model.dataSource.UserDataSource;
import com.chuger.bithdayapp.model.domain.User;

/**
 * User: Администратор
 * Date: 17.02.13
 * Time: 16:43
 */
public class GoogleBirthdayResponse extends AbstractBdayResponse {

    public GoogleBirthdayResponse(Chain chain) {
        super(chain);
    }

    @Override
    public AliasHolder getAliasHolder() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected AbstractBirthdayParser getAbstractBdayParser(String bdayString) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void setUid(User user, Long uid) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected User findUserById(UserDataSource readDataSource, Long uid) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
