package com.chuger.bithdayapp.controller.chain.birthday.responseListener.parser;

import org.joda.time.DateTime;

import static com.chuger.bithdayapp.model.utils.StringUtils.isNotEmpty;

/**
 * User: Acer5740
 * Date: 26.02.12
 * Time: 10:02
 */
public abstract class AbstractBirthdayParser {
    protected String birthdayString;

    public AbstractBirthdayParser(String birthdayString) {
        this.birthdayString = birthdayString;
    }

    public abstract boolean isUnknownYear();
    public abstract DateTime getBirthday();

    public boolean notNull() {
        return isNotEmpty(birthdayString) && !"null".equals(birthdayString);
    }
}
