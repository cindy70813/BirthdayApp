/*
 * Copyright 2010 Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chuger.bithdayapp.view.auth;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.chuger.R;
import com.chuger.bithdayapp.controller.chain.auth.responseListener.AuthListener;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AuthDialog extends Dialog {

    static final FrameLayout.LayoutParams FILL =
            new FrameLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
    static final String DISPLAY_STRING = "touch";

    private String mUrl;
    private AuthListener mListener;
    private ProgressDialog mSpinner;
    private ImageView mCrossImage;
    private WebView mWebView;
    private FrameLayout mContent;

    public AuthDialog(final Context context, final String url, final AuthListener listener) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        mUrl = url;
        mListener = listener;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSpinner = new ProgressDialog(getContext());
        mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mSpinner.setMessage("Loading...");

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mContent = new FrameLayout(getContext());

        /* Create the 'x' image, but don't add to the mContent layout yet
         * at this point, we only need to know its drawable width and height
         * to place the webview
         */
        createCrossImage();

        /* Now we know 'x' drawable width and height,
        * layout the webivew and add it the mContent layout
        */
        final int crossWidth = mCrossImage.getDrawable().getIntrinsicWidth();
        setUpWebView(crossWidth / 2);

        /* Finally add the 'x' image to the mContent layout and
        * add mContent to the Dialog view
        */
        mContent.addView(mCrossImage, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        addContentView(mContent, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    }

    private void createCrossImage() {
        mCrossImage = new ImageView(getContext());
        // Dismiss the dialog when user click on the 'x'
        mCrossImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                mListener.onCancel();
                AuthDialog.this.dismiss();
            }
        });
        final Drawable crossDrawable = getContext().getResources().getDrawable(R.drawable.close);
        mCrossImage.setImageDrawable(crossDrawable);
        /* 'x' should not be visible while webview is loading
         * make it visible only after webview has fully loaded
        */
        mCrossImage.setVisibility(View.INVISIBLE);
    }

    private void setUpWebView(final int margin) {
        final LinearLayout webViewContainer = new LinearLayout(getContext());
        mWebView = new WebView(getContext());
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setWebViewClient(new AuthDialog.FbWebViewClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(mUrl);
        mWebView.setLayoutParams(FILL);
        mWebView.setVisibility(View.INVISIBLE);

        webViewContainer.setPadding(margin, margin, margin, margin);
        webViewContainer.addView(mWebView);
        mContent.addView(webViewContainer);
    }

    private class FbWebViewClient extends WebViewClient {

        private final String VK_OAUTH_HOST = "oauth.vk.com";
        private final String VK_OAUTH_PATH = "/blank.html";
        private final String VK_ACCESS_TOKEN_ALIAS = "access_token";
        private final String TAG = this.getClass().getSimpleName();

        public Bundle getQueryMap(String query) {
            final String[] params = query.split("&");
            final Bundle bundle = new Bundle();
            for (final String param : params) {
                final String[] split = param.split("=");
                final String name = split[0];
                final String value = split[1];
                bundle.putString(name, value);
            }
            return bundle;
        }

        @Override
        public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
            try {
                Log.d("Facebook-WebView", "Redirect URL: " + url);
                if (url.startsWith(Facebook.REDIRECT_URI)) {
                    final Bundle values = Util.parseUrl(url);

                    String error = values.getString("error");
                    if (error == null) {
                        error = values.getString("error_type");
                    }

                    if (error == null) {
                        mListener.onComplete(values);
                    } else if (error.equals("access_denied") || error.equals("OAuthAccessDeniedException")) {
                        mListener.onCancel();
                    } else {
                        mListener.onError(new FacebookError(error));
                    }

                    AuthDialog.this.dismiss();
                    return true;
                } else if (url.startsWith(Facebook.CANCEL_URI)) {
                    mListener.onCancel();
                    AuthDialog.this.dismiss();
                    return true;
                } else if (url.contains(DISPLAY_STRING)) {
                    return false;
                } else if (url.contains(REDIRECT_URI)) {
                    final URL newUrl = new URL(url);
                    final String query = newUrl.getQuery();
                    final Bundle bundle = getQueryMap(query);
                    if (bundle.containsKey(CODE_ALIAS)) {
                        final String code = bundle.getString(CODE_ALIAS);
                        Log.d(CODE_ALIAS, code);
                        final String authUrl =
                                String.format(AUTH_URL, code, mListener.getChain().getAppId(), CLIENT_SECRET,
                                        REDIRECT_URI);
                        //                        mWebView.loadUrl(authUrl);
                        final String accessToken = getAccessToken(code, mListener.getChain().getAppId());
                        final Bundle googleBundle = new Bundle();
                        googleBundle.putString("access_token", accessToken);
                        mListener.onComplete(googleBundle);
                        AuthDialog.this.dismiss();
                        return true;
                    }
                } else {
                    final String urlString = url.replace('#', '?');
                    final URL newUrl = new URL(urlString);
                    if (VK_OAUTH_HOST.equals(newUrl.getHost()) && VK_OAUTH_PATH.equals(newUrl.getPath())) {
                        final String query = newUrl.getQuery();
                        if (query != null) {
                            final Bundle bundle = getQueryMap(query);
                            if (bundle.containsKey(VK_ACCESS_TOKEN_ALIAS)) {
                                mListener.onComplete(bundle);
                            } else {
                                mListener.onCancel();
                            }
                            AuthDialog.this.dismiss();
                            return true;
                        }
                    }

                }
            } catch (MalformedURLException e) {
                mListener.onError(new FacebookError(e.getMessage()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            // launch non-dialog URLs in a full browser
            //            getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            return false;
        }

        public void onPageStarted(final WebView view, final String url, final Bitmap favicon) {
            Log.d("Facebook-WebView", "Webview loading URL: " + url);
            if (url.contains(REDIRECT_URI + "/?" + CODE_ALIAS + "=")) {
                try {
                    final URL newUrl = new URL(url);
                    final String query = newUrl.getQuery();
                    final Bundle bundle = getQueryMap(query);
                    if (bundle.containsKey(CODE_ALIAS)) {
                        final String code = bundle.getString(CODE_ALIAS);
                        Log.d(CODE_ALIAS, code);
                        final String authUrl =
                                String.format(AUTH_URL, code, mListener.getChain().getAppId(), CLIENT_SECRET,
                                        REDIRECT_URI);
                        //                        mWebView.loadUrl(authUrl);
                        final String response = openUrl(authUrl, "POST");
                        Log.d("response", response);
                    }
                } catch (MalformedURLException ignored) {
                } catch (IOException ignored) {
                }
            } else {
                super.onPageStarted(view, url, favicon);
            }
            mSpinner.show();
        }

        public static final String REDIRECT_URI = "http://localhost";
        public static final String AUTH_URL = "https://accounts.google.com/o/oauth2/token?" +
                "code=%s" +
                "&client_id=%s" +
                "&client_secret=%s" +
                "&redirect_uri=%s" +
                "&grant_type=authorization_code";
        private static final String CLIENT_SECRET = "VUyfdv1wR91_QDY27jaqSeUX";
        private final String CODE_ALIAS = "code";

        public String getAccessToken(String code, String clietId) {
            try {
                final String httpsURL = "https://accounts.google.com/o/oauth2/token";
                final DefaultHttpClient client = new DefaultHttpClient();
                final HttpPost httpPost = new HttpPost(httpsURL);

                //authentication block:
                final List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
                nvps.add(new BasicNameValuePair("code", code));
                nvps.add(new BasicNameValuePair("client_id", clietId));
                nvps.add(new BasicNameValuePair("client_secret", CLIENT_SECRET));
                nvps.add(new BasicNameValuePair("redirect_uri", REDIRECT_URI));
                nvps.add(new BasicNameValuePair("grant_type", "authorization_code"));
                final UrlEncodedFormEntity content = new UrlEncodedFormEntity(nvps, HTTP.UTF_8);
                httpPost.setEntity(content);

                //sending the request and retrieving the response:
                HttpResponse response = client.execute(httpPost);
                HttpEntity responseEntity = response.getEntity();

                //handling the response: responseEntity.getContent() is your InputStream
                final InputStream stream = responseEntity.getContent();
                Scanner scanner = new Scanner(stream).useDelimiter("\\A");
                final String jsonString = scanner.hasNext() ? scanner.next() : "";
                final JSONObject jsonObject = new JSONObject(jsonString);
                final String token = jsonObject.getString("access_token");
                Log.e(TAG, "json: " + jsonString);
                Log.e(TAG, "token: " + token);
                return token;
            } catch (UnsupportedEncodingException e) {
                Log.e(TAG, e.getMessage(), e);
            } catch (ClientProtocolException e) {
                Log.e(TAG, e.getMessage(), e);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage(), e);
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage(), e);
            }
            return null;
        }

        public String openUrl(String url, final String method) throws IOException {

            final HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestProperty("Host", "accounts.google.com");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", "1000");
            conn.setRequestMethod(method);

            String response;
            try {
                response = Util.read(conn.getInputStream());
            } catch (FileNotFoundException e) {
                response = Util.read(conn.getErrorStream());
            }
            return response;
        }

        @Override
        public void onPageFinished(final WebView view, final String url) {
            Log.d("DIALOG", "Page loaded complete: " + url);
            super.onPageFinished(view, url);
            mSpinner.dismiss();
            /* 
             * Once webview is fully loaded, set the mContent background to be transparent
             * and make visible the 'x' image. 
             */
            mContent.setBackgroundColor(Color.TRANSPARENT);
            mWebView.setVisibility(View.VISIBLE);
            mCrossImage.setVisibility(View.VISIBLE);
        }
    }
}
