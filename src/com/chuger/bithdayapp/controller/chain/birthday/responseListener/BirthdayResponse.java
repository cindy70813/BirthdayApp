package com.chuger.bithdayapp.controller.chain.birthday.responseListener;

import com.chuger.bithdayapp.controller.chain.GetChain;

/**
 * User: Acer5740
 * Date: 24.02.12
 * Time: 10:33
 */
public interface BirthdayResponse extends GetChain {
    void processResponse(final String response);
}
