package com.chuger.bithdayapp.controller.chain.auth.responseListener;

import android.os.Bundle;
import com.facebook.android.DialogError;
import com.facebook.android.FacebookError;

/**
 * Created with IntelliJ IDEA.
 * User: ChuGer
 * Date: 08.04.12
 * Time: 7:59
 */
public interface DialogListener {

    void onComplete(Bundle values);

    void onError(Throwable e);

    void onCancel();
}
