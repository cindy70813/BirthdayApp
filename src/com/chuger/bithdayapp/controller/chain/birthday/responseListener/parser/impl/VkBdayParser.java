package com.chuger.bithdayapp.controller.chain.birthday.responseListener.parser.impl;

import com.chuger.bithdayapp.controller.chain.birthday.responseListener.parser.AbstractBirthdayParser;
import com.chuger.bithdayapp.model.utils.DateTimeUtils;
import org.joda.time.DateTime;

/**
 * User: Acer5740
 * Date: 26.02.12
 * Time: 10:03
 */
public class VkBdayParser extends AbstractBirthdayParser {
    public VkBdayParser(String birthdayString) {
        super(birthdayString);
    }

    @Override
    public boolean isUnknownYear() {
        return birthdayString.length() < 8;
    }

    @Override
    public DateTime getBirthday() {
        return DateTimeUtils.parseVKDate(birthdayString);
    }
}
