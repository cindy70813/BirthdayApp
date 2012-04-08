package com.chuger.bithdayapp.controller.chain.auth.request.vkAuth;

import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.chuger.bithdayapp.controller.chain.auth.responseListener.AuthListener;
import com.chuger.bithdayapp.controller.chain.chain.Chain;
import com.chuger.bithdayapp.model.utils.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class VkontakteWebViewClient extends WebViewClient {
    private final String TAG = VkontakteWebViewClient.class.getSimpleName();
    private final String VK_OAUTH_HOST = "oauth.vk.com";
    private final String VK_OAUTH_PATH = "/blank.html";
    private final String VK_ACCESS_TOKEN_ALIAS = "access_token";
    private final AuthListener authListener;

    public VkontakteWebViewClient(AuthListener authListener) {
        this.authListener = authListener;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        return super.shouldOverrideUrlLoading(view, url);
    }

    @Override
    public void onPageFinished(WebView view, String urlString) {
        Log.d(TAG, "Page loaded complete: " + urlString);
        try {
            urlString = urlString.replace('#', '?');
            final URL url = new URL(urlString);
            if (VK_OAUTH_HOST.equals(url.getHost()) && VK_OAUTH_PATH.equals(url.getPath())) {
                final String query = url.getQuery();
                if (StringUtils.isNotEmpty(query)) {
                    final Map<String, String> queryMap = getQueryMap(query);
                    final Chain chain = authListener.getChain();
                    for (String key : queryMap.keySet()) {
                        if (VK_ACCESS_TOKEN_ALIAS.equals(key)) {
                            final String accessToken = queryMap.get(key);
                            Log.d(TAG, "Access token: " + accessToken);
                            chain.setAccessToken(accessToken);
                            authListener.onAuthSucceed();
                            break;
                        }
                    }
                    if (StringUtils.isEmpty(chain.getAccessToken())) {
                        authListener.onAuthFail("Can't get access token");
                    }
                }
            }
        } catch (MalformedURLException e) {
            Log.d(TAG, e.getMessage(), e);
        }
    }

    public static Map<String, String> getQueryMap(String query) {
        final String[] params = query.split("&");
        final Map<String, String> map = new HashMap<String, String>();
        for (final String param : params) {
            final String[] split = param.split("=");
            final String name = split[0];
            final String value = split[1];
            map.put(name, value);
        }
        return map;
    }
}
