package com.chuger.bithdayapp.controller.chain.auth.request.impl;

import android.content.Context;
import android.content.Intent;
import com.chuger.bithdayapp.controller.chain.chain.Chain;
import com.chuger.bithdayapp.controller.chain.auth.request.AbstractRequestListener;
import com.chuger.bithdayapp.controller.chain.auth.request.vkAuth.VkAuthorizationActivity;

/**
 * User: Acer5740
 * Date: 25.02.12
 * Time: 4:04
 */
public class VkAuthRequest extends AbstractRequestListener {
    public VkAuthRequest(Chain chain) {
        super(chain);
    }

    @Override
    public void doCallBirthdays(final Context context) {
        getChain().getBirthdayCaller().requestBirthday();
    }

    @Override
    public void doAuthorize(final Context context) {
        final Intent intent = new Intent(context, VkAuthorizationActivity.class);
        context.startActivity(intent);
    }

    @Override
    public boolean isAuthorized() {
        return getChain().isAuthorized();
    }
}
