package com.chuger.bithdayapp.controller.chain.auth.responseListener;

import android.util.Log;
import com.chuger.bithdayapp.controller.chain.GetChain;
import com.chuger.bithdayapp.controller.chain.chain.Chain;

/**
 * User: Acer5740
 * Date: 25.02.12
 * Time: 5:17
 */
public abstract class AbstractAuthListener implements AuthListener {
    private Chain chain;
    private static final String TAG = AbstractAuthListener.class.getSimpleName();

    public AbstractAuthListener(Chain chain) {
        this.chain = chain;
    }

    @Override
    public Chain getChain() {
        return chain;
    }

    @Override
    public void onError(Throwable e) {
        Log.e(TAG, e.getMessage(), e);
    }

    @Override
    public void onCancel() {
    }
}
