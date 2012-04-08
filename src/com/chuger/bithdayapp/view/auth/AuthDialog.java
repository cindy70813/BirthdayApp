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
import com.chuger.bithdayapp.controller.chain.auth.responseListener.DialogListener;
import com.facebook.android.*;

import java.net.MalformedURLException;
import java.net.URL;

public class AuthDialog extends Dialog {

    static final FrameLayout.LayoutParams FILL =
            new FrameLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
    static final String DISPLAY_STRING = "touch";

    private String mUrl;
    private DialogListener mListener;
    private ProgressDialog mSpinner;
    private ImageView mCrossImage;
    private WebView mWebView;
    private FrameLayout mContent;

    public AuthDialog(final Context context, final String url, final DialogListener listener) {
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
            } else {
                try {
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
                } catch (MalformedURLException e) {
                    mListener.onError(new FacebookError(e.getMessage()));
                }
            }
            // launch non-dialog URLs in a full browser
            //            getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            return false;
        }

        @Override
        public void onPageStarted(final WebView view, final String url, final Bitmap favicon) {
            Log.d("Facebook-WebView", "Webview loading URL: " + url);
            super.onPageStarted(view, url, favicon);
            mSpinner.show();
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
