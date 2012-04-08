package com.chuger.bithdayapp.controller.chain.auth.request;

import android.content.Context;
import android.view.View;
import com.chuger.bithdayapp.controller.chain.chain.Chain;

/**
 * User: Acer5740
 * Date: 07.02.12
 * Time: 15:39
 */
public abstract class AbstractRequestListener implements View.OnClickListener, AuthRequest {

    @Override
    public void onClick(final View view) {
        final Context context = view.getContext();
        if (isAuthorized()) {
            doCallBirthdays(context);
        } else {
            doAuthorize(context);
        }
    }

    private Chain chain;

    protected AbstractRequestListener(Chain chain) {
        this.chain = chain;
    }

    @Override
    public Chain getChain() {
        return chain;
    }
}
