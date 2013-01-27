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

package com.facebook.android;

import android.Manifest;
import android.app.Activity;
import android.content.*;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.os.*;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieSyncManager;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Main Facebook object for interacting with the Facebook developer API.
 * Provides methods to log in and log out a user, make requests using the REST
 * and Graph APIs, and start user interface interactions with the API (such as
 * pop-ups promoting for credentials, permissions, stream posts, etc.)
 *
 * @author Jim Brusstar (jimbru@facebook.com),
 *         Yariv Sadan (yariv@facebook.com),
 *         Luke Shepard (lshepard@facebook.com)
 */
public class Facebook {

    // Strings used in the authorization flow
    public static final String REDIRECT_URI = "fbconnect://success";
    public static final String CANCEL_URI = "fbconnect://cancel";
    public static final String TOKEN = "access_token";
    public static final String EXPIRES = "expires_in";
    public static final String SINGLE_SIGN_ON_DISABLED = "service_disabled";

    public static final int FORCE_DIALOG_AUTH = -1;

    private static final String LOGIN = "oauth";

    // Used as default activityCode by authorize(). See authorize() below.
    private static final int DEFAULT_AUTH_ACTIVITY_CODE = 32665;

    // Facebook server endpoints: may be modified in a subclass for testing
    protected static String DIALOG_BASE_URL = "https://m.facebook.com/dialog/";
    protected static String GRAPH_BASE_URL = "https://graph.facebook.com/";
    protected static String RESTSERVER_URL = "https://api.facebook.com/restserver.php";

    private String mAccessToken = null;
    private long mLastAccessUpdate = 0;
    private long mAccessExpires = 0;
    private String mAppId;

    private Context mAuthActivity;
    private String[] mAuthPermissions;
    private int mAuthActivityCode;

    // If the last time we extended the access token was more than 24 hours ago
    // we try to refresh the access token again.
    final private long REFRESH_TOKEN_BARRIER = 24L * 60L * 60L * 1000L;

    /**
     * Constructor for Facebook object.
     *
     * @param appId Your Facebook application ID. Found at
     *              www.facebook.com/developers/apps.php.
     */
    public Facebook(final String appId) {
        if (appId == null) {
            throw new IllegalArgumentException("You must specify your application ID when instantiating " +
                    "a Facebook object. See README for details.");
        }
        mAppId = appId;
    }

    /**
     * Internal method to handle single sign-on backend for authorize().
     *
     * @param activity      The Android Activity that will parent the ProxyAuth Activity.
     * @param applicationId The Facebook application identifier.
     * @param permissions   A list of permissions required for this application. If you do
     *                      not require any permissions, pass an empty String array.
     * @param activityCode  Activity code to uniquely identify the result Intent in the
     *                      callback.
     */
    private boolean startSingleSignOn(final Context activity, final String applicationId, final String[] permissions,
                                      final int activityCode) {
        boolean didSucceed = true;
        final Intent intent = new Intent();

        intent.setClassName("com.facebook.katana", "com.facebook.katana.ProxyAuth");
        intent.putExtra("client_id", applicationId);
        if (permissions.length > 0) {
            intent.putExtra("scope", TextUtils.join(",", permissions));
        }

        // Verify that the application whose package name is
        // com.facebook.katana.ProxyAuth
        // has the expected FB app signature.
        if (!validateAppSignatureForIntent(activity, intent)) {
            return false;
        }

        mAuthActivity = activity;
        mAuthPermissions = permissions;
        mAuthActivityCode = activityCode;
        try {
            activity.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            didSucceed = false;
        }

        return didSucceed;
    }

    /**
     * Query the signature for the application that would be invoked by the
     * given intent and verify that it matches the FB application's signature.
     *
     * @param context
     * @param intent
     * @return true if the app's signature matches the expected signature.
     */
    private boolean validateAppSignatureForIntent(final Context context, final Intent intent) {

        final ResolveInfo resolveInfo = context.getPackageManager().resolveActivity(intent, 0);
        if (resolveInfo == null) {
            return false;
        }

        final String packageName = resolveInfo.activityInfo.packageName;
        final PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
        } catch (NameNotFoundException e) {
            return false;
        }

        for (final Signature signature : packageInfo.signatures) {
            if (signature.toCharsString().equals(FB_APP_SIGNATURE)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Refresh OAuth access token method. Binds to Facebook for Android
     * stand-alone application application to refresh the access token. This
     * method tries to connect to the Facebook App which will handle the
     * authentication flow, and return a new OAuth access token. This method
     * will automatically replace the old token with a new one. Note that this
     * method is asynchronous and the callback will be invoked in the original
     * calling thread (not in a background thread).
     *
     * @param context         The Android Context that will be used to bind to the Facebook
     *                        RefreshToken Service
     * @param serviceListener Callback interface for notifying the calling application when
     *                        the refresh request has completed or failed (can be null). In
     *                        case of a success a new token can be found inside the result
     *                        Bundle under Facebook.ACCESS_TOKEN key.
     * @return true if the binding to the RefreshToken Service was created
     */
    public boolean extendAccessToken(final Context context, final ServiceListener serviceListener) {
        final Intent intent = new Intent();

        intent.setClassName("com.facebook.katana", "com.facebook.katana.platform.TokenRefreshService");

        // Verify that the application whose package name is
        // com.facebook.katana
        // has the expected FB app signature.
        if (!validateAppSignatureForIntent(context, intent)) {
            return false;
        }

        return context.bindService(intent, new TokenRefreshServiceConnection(context, serviceListener),
                Context.BIND_AUTO_CREATE);
    }

    /**
     * Calls extendAccessToken if shouldExtendAccessToken returns true.
     *
     * @return the same value as extendAccessToken if the the token requires
     *         refreshing, true otherwise
     */
    public boolean extendAccessTokenIfNeeded(final Context context, final ServiceListener serviceListener) {
        if (shouldExtendAccessToken()) {
            return extendAccessToken(context, serviceListener);
        }
        return true;
    }

    /**
     * Check if the access token requires refreshing.
     *
     * @return true if the last time a new token was obtained was over 24 hours ago.
     */
    public boolean shouldExtendAccessToken() {
        return isSessionValid() && (System.currentTimeMillis() - mLastAccessUpdate >= REFRESH_TOKEN_BARRIER);
    }

    /**
     * Handles connection to the token refresh service (this service is a part
     * of Facebook App).
     */
    private class TokenRefreshServiceConnection implements ServiceConnection {

        final Messenger messageReceiver = new Messenger(new Handler() {
            @Override
            public void handleMessage(final Message msg) {
                final String token = msg.getData().getString(TOKEN);
                final long expiresAt = msg.getData().getLong(EXPIRES) * 1000L;

                // To avoid confusion we should return the expiration time in
                // the same format as the getAccessExpires() function - that
                // is in milliseconds.
                final Bundle resultBundle = (Bundle) msg.getData().clone();
                resultBundle.putLong(EXPIRES, expiresAt);

                if (token != null) {
                    setAccessToken(token);
                    setAccessExpires(expiresAt);
                    if (serviceListener != null) {
                        serviceListener.onComplete(resultBundle);
                    }
                } else if (serviceListener != null) { // extract errors only if client wants them
                    final String error = msg.getData().getString("error");
                    if (msg.getData().containsKey("error_code")) {
                        final int errorCode = msg.getData().getInt("error_code");
                        serviceListener.onFacebookError(new FacebookError(error, null, errorCode));
                    } else {
                        serviceListener.onError(new Error(error != null ? error : "Unknown service error"));
                    }
                }

                // The refreshToken function should be called rarely,
                // so there is no point in keeping the binding open.
                applicationsContext.unbindService(TokenRefreshServiceConnection.this);
            }
        });

        final ServiceListener serviceListener;
        final Context applicationsContext;

        Messenger messageSender = null;

        public TokenRefreshServiceConnection(final Context applicationsContext, final ServiceListener serviceListener) {
            this.applicationsContext = applicationsContext;
            this.serviceListener = serviceListener;
        }

        @Override
        public void onServiceConnected(final ComponentName className, final IBinder service) {
            messageSender = new Messenger(service);
            refreshToken();
        }

        @Override
        public void onServiceDisconnected(final ComponentName arg) {
            serviceListener.onError(new Error("Service disconnected"));
            // We returned an error so there's no point in
            // keeping the binding open.
            mAuthActivity.unbindService(TokenRefreshServiceConnection.this);
        }

        private void refreshToken() {
            final Bundle requestData = new Bundle();
            requestData.putString(TOKEN, mAccessToken);

            final Message request = Message.obtain();
            request.setData(requestData);
            request.replyTo = messageReceiver;

            try {
                messageSender.send(request);
            } catch (RemoteException e) {
                serviceListener.onError(new Error("Service connection error"));
            }
        }
    }

    ;

    /**
     * Invalidate the current user session by removing the access token in
     * memory, clearing the browser cookie, and calling auth.expireSession
     * through the API.
     * <p/>
     * Note that this method blocks waiting for a network response, so do not
     * call it in a UI thread.
     *
     * @param context The Android context in which the logout should be called: it
     *                should be the same context in which the login occurred in
     *                order to clear any stored cookies
     * @return JSON string representation of the auth.expireSession response
     *         ("true" if successful)
     * @throws IOException
     * @throws MalformedURLException
     */
    public String logout(final Context context) throws MalformedURLException, IOException {
        Util.clearCookies(context);
        final Bundle b = new Bundle();
        b.putString("method", "auth.expireSession");
        final String response = request(b);
        setAccessToken(null);
        setAccessExpires(0);
        return response;
    }

    /**
     * Make a request to Facebook's old (pre-graph) API with the given
     * parameters. One of the parameter keys must be "method" and its value
     * should be a valid REST server API method.
     * <p/>
     * See http://developers.facebook.com/docs/reference/rest/
     * <p/>
     * Note that this method blocks waiting for a network response, so do not
     * call it in a UI thread.
     * <p/>
     * Example:
     * <code>
     * Bundle parameters = new Bundle();
     * parameters.putString("method", "auth.expireSession");
     * String response = request(parameters);
     * </code>
     *
     * @param parameters Key-value pairs of parameters to the request. Refer to the
     *                   documentation: one of the parameters must be "method".
     * @return JSON string representation of the response
     * @throws IOException              if a network error occurs
     * @throws MalformedURLException    if accessing an invalid endpoint
     * @throws IllegalArgumentException if one of the parameters is not "method"
     */
    public String request(final Bundle parameters) throws MalformedURLException, IOException {
        if (!parameters.containsKey("method")) {
            throw new IllegalArgumentException(
                    "API method must be specified. " + "(parameters must contain key \"method\" and value). See" +
                            " http://developers.facebook.com/docs/reference/rest/");
        }
        return request(null, parameters, "GET");
    }

    /**
     * Make a request to the Facebook Graph API without any parameters.
     * <p/>
     * See http://developers.facebook.com/docs/api
     * <p/>
     * Note that this method blocks waiting for a network response, so do not
     * call it in a UI thread.
     *
     * @param graphPath Path to resource in the Facebook graph, e.g., to fetch data
     *                  about the currently logged authenticated user, provide "me",
     *                  which will fetch http://graph.facebook.com/me
     * @return JSON string representation of the response
     * @throws IOException
     * @throws MalformedURLException
     */
    public String request(final String graphPath) throws MalformedURLException, IOException {
        return request(graphPath, new Bundle(), "GET");
    }

    /**
     * Make a request to the Facebook Graph API with the given string parameters
     * using an HTTP GET (default method).
     * <p/>
     * See http://developers.facebook.com/docs/api
     * <p/>
     * Note that this method blocks waiting for a network response, so do not
     * call it in a UI thread.
     *
     * @param graphPath  Path to resource in the Facebook graph, e.g., to fetch data
     *                   about the currently logged authenticated user, provide "me",
     *                   which will fetch http://graph.facebook.com/me
     * @param parameters key-value string parameters, e.g. the path "search" with
     *                   parameters "q" : "facebook" would produce a query for the
     *                   following graph resource:
     *                   https://graph.facebook.com/search?q=facebook
     * @return JSON string representation of the response
     * @throws IOException
     * @throws MalformedURLException
     */
    public String request(final String graphPath, final Bundle parameters) throws MalformedURLException, IOException {
        return request(graphPath, parameters, "GET");
    }

    /**
     * Synchronously make a request to the Facebook Graph API with the given
     * HTTP method and string parameters. Note that binary data parameters
     * (e.g. pictures) are not yet supported by this helper function.
     * <p/>
     * See http://developers.facebook.com/docs/api
     * <p/>
     * Note that this method blocks waiting for a network response, so do not
     * call it in a UI thread.
     *
     * @param graphPath  Path to resource in the Facebook graph, e.g., to fetch data
     *                   about the currently logged authenticated user, provide "me",
     *                   which will fetch http://graph.facebook.com/me
     * @param params     Key-value string parameters, e.g. the path "search" with
     *                   parameters {"q" : "facebook"} would produce a query for the
     *                   following graph resource:
     *                   https://graph.facebook.com/search?q=facebook
     * @param httpMethod http verb, e.g. "GET", "POST", "DELETE"
     * @return JSON string representation of the response
     * @throws IOException
     * @throws MalformedURLException
     */
    public String request(final String graphPath, final Bundle params, final String httpMethod) throws IOException {
        params.putString("format", "json");
        if (isSessionValid()) {
            params.putString(TOKEN, getAccessToken());
        }
        final String url = graphPath != null ? GRAPH_BASE_URL + graphPath : RESTSERVER_URL;
        return Util.openUrl(url, httpMethod, params);
    }

    /**
     * @return boolean - whether this object has an non-expired session token
     */
    public boolean isSessionValid() {
        return (getAccessToken() != null) &&
                ((getAccessExpires() == 0) || (System.currentTimeMillis() < getAccessExpires()));
    }

    /**
     * Retrieve the OAuth 2.0 access token for API access: treat with care.
     * Returns null if no session exists.
     *
     * @return String - access token
     */
    public String getAccessToken() {
        return mAccessToken;
    }

    /**
     * Retrieve the current session's expiration time (in milliseconds since
     * Unix epoch), or 0 if the session doesn't expire or doesn't exist.
     *
     * @return long - session expiration time
     */
    public long getAccessExpires() {
        return mAccessExpires;
    }

    /**
     * Set the OAuth 2.0 access token for API access.
     *
     * @param token - access token
     */
    public void setAccessToken(final String token) {
        mAccessToken = token;
        mLastAccessUpdate = System.currentTimeMillis();
    }

    /**
     * Set the current session's expiration time (in milliseconds since Unix
     * epoch), or 0 if the session doesn't expire.
     *
     * @param time - timestamp in milliseconds
     */
    public void setAccessExpires(final long time) {
        mAccessExpires = time;
    }

    /**
     * Set the current session's duration (in seconds since Unix epoch), or "0"
     * if session doesn't expire.
     *
     * @param expiresIn - duration in seconds (or 0 if the session doesn't expire)
     */
    public void setAccessExpiresIn(final String expiresIn) {
        if (expiresIn != null) {
            final long expires =
                    expiresIn.equals("0") ? 0 : System.currentTimeMillis() + Long.parseLong(expiresIn) * 1000L;
            setAccessExpires(expires);
        }
    }

    public String getAppId() {
        return mAppId;
    }

    public void setAppId(final String appId) {
        mAppId = appId;
    }

    /**
     * Callback interface for service requests.
     */
    public static interface ServiceListener {

        /**
         * Called when a service request completes.
         *
         * @param values Key-value string pairs extracted from the response.
         */
        public void onComplete(Bundle values);

        /**
         * Called when a Facebook server responds to the request with an error.
         */
        public void onFacebookError(FacebookError e);

        /**
         * Called when a Facebook Service responds to the request with an error.
         */
        public void onError(Error e);

    }

    public static final String FB_APP_SIGNATURE = "30820268308201d102044a9c4610300d06092a864886f70d0101040500307a310" +
            "b3009060355040613025553310b30090603550408130243413112301006035504" +
            "07130950616c6f20416c746f31183016060355040a130f46616365626f6f6b204" +
            "d6f62696c653111300f060355040b130846616365626f6f6b311d301b06035504" +
            "03131446616365626f6f6b20436f72706f726174696f6e3020170d30393038333" +
            "13231353231365a180f32303530303932353231353231365a307a310b30090603" +
            "55040613025553310b30090603550408130243413112301006035504071309506" +
            "16c6f20416c746f31183016060355040a130f46616365626f6f6b204d6f62696c" +
            "653111300f060355040b130846616365626f6f6b311d301b06035504031314466" +
            "16365626f6f6b20436f72706f726174696f6e30819f300d06092a864886f70d01" +
            "0101050003818d0030818902818100c207d51df8eb8c97d93ba0c8c1002c928fa" +
            "b00dc1b42fca5e66e99cc3023ed2d214d822bc59e8e35ddcf5f44c7ae8ade50d7" +
            "e0c434f500e6c131f4a2834f987fc46406115de2018ebbb0d5a3c261bd97581cc" +
            "fef76afc7135a6d59e8855ecd7eacc8f8737e794c60a761c536b72b11fac8e603" +
            "f5da1a2d54aa103b8a13c0dbc10203010001300d06092a864886f70d010104050" +
            "0038181005ee9be8bcbb250648d3b741290a82a1c9dc2e76a0af2f2228f1d9f9c" +
            "4007529c446a70175c5a900d5141812866db46be6559e2141616483998211f4a6" +
            "73149fb2232a10d247663b26a9031e15f84bc1c74d141ff98a02d76f85b2c8ab2" +
            "571b6469b232d8e768a7f7ca04f7abe4a775615916c07940656b58717457b42bd" + "928a2";

}
