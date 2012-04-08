package com.chuger.bithdayapp.controller.chain.auth.request;

import android.content.Context;
import android.view.View;
import com.chuger.bithdayapp.controller.chain.GetChain;

/**
 * User: Acer5740
 * Date: 25.02.12
 * Time: 4:19
 */
public interface AuthRequest extends GetChain {
    void doCallBirthdays(Context view);

    void doAuthorize(Context context);

    boolean isAuthorized();

    void onClick(final View view);

}
