package com.chuger.bithdayapp.controller.chain.auth.responseListener;

import com.chuger.bithdayapp.controller.chain.GetChain;

/**
 * Callback interface for authorization events.
 *
 */
public interface AuthListener extends GetChain {

	public void onAuthSucceed();

	public void onAuthFail(String error);
}
