package com.chuger.bithdayapp.controller.chain.auth.request.vkAuth;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import com.chuger.R;
import com.chuger.bithdayapp.controller.chain.auth.responseListener.AuthListener;

import static com.chuger.bithdayapp.controller.chain.locator.ChainLocator.VK_CHAIN_ALIAS;
import static com.chuger.bithdayapp.controller.chain.locator.ChainLocator.getChain;

/**
 * User: Acer5740
 * Date: 20.02.12
 * Time: 10:16
 */
public class VkAuthorizationActivity extends Activity {
    private static final String AUTH_URL =
            "http://oauth.vk.com/oauth/authorize?client_id=%s&scope=%s&display=%s&response_type=token";
    private static final String SETTINGS = "friends";
    private static final String DISPLAY = "touch";
    private String TAG = VkAuthorizationActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final AuthListener authListener = getChain(VK_CHAIN_ALIAS).getAuthListener();
        if (authListener != null) {
            setContentView(R.layout.web_layout);
            setTitle(R.string.authorization);
            WebView webView = (WebView) findViewById(R.id.webView);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebViewClient(new VkontakteWebViewClient(authListener));
            final String authUrl = String.format(AUTH_URL, authListener.getChain().getAppId(), SETTINGS, DISPLAY);
            Log.d(TAG, authUrl);
            webView.loadUrl(authUrl);
        } else {
            Log.e(TAG, "Can't load Auth Listener");
        }
    }
}