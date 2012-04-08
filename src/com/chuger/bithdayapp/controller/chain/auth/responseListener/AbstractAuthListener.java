package com.chuger.bithdayapp.controller.chain.auth.responseListener;

import com.chuger.bithdayapp.controller.chain.chain.Chain;

/**
 * User: Acer5740
 * Date: 25.02.12
 * Time: 5:17
 */
public abstract class AbstractAuthListener implements AuthListener{
    private Chain chain;

    public AbstractAuthListener(Chain chain) {
        this.chain = chain;
    }

    @Override
    public Chain getChain() {
        return chain;
    }
}
