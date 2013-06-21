package com.chuger.bithdayapp.controller.chain.birthday.responseListener.parser.impl;

import com.chuger.bithdayapp.controller.chain.birthday.responseListener.parser.AbstractBirthdayParser;
import com.chuger.bithdayapp.model.utils.DateTimeUtils;
import org.joda.time.DateTime;

/**
 * User: chgv
 * Date: 21.06.13
 * Time: 11:39
 */
public class GoogleBdayParser extends AbstractBirthdayParser {
    public GoogleBdayParser(String birthdayString) {
        super(birthdayString);
    }

    @Override
    public boolean isUnknownYear() {
        return false;
    }

    @Override
    public DateTime getBirthday() {
        return DateTimeUtils.parseGoogleDate(birthdayString);
    }
}
