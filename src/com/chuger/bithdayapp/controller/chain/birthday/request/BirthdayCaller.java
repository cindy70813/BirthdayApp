package com.chuger.bithdayapp.controller.chain.birthday.request;

import android.os.Bundle;
import com.chuger.bithdayapp.controller.chain.GetChain;

/**
 * User: Acer5740
 * Date: 24.02.12
 * Time: 10:32
 */
public interface BirthdayCaller extends GetChain {

    String getRequestUrl();

    Bundle getRequestParams();

    void requestBirthday();
}
