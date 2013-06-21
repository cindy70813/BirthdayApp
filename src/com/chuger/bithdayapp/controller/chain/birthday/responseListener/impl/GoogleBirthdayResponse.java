package com.chuger.bithdayapp.controller.chain.birthday.responseListener.impl;

import android.util.Log;
import com.chuger.bithdayapp.controller.chain.birthday.responseListener.AbstractBdayResponse;
import com.chuger.bithdayapp.controller.chain.birthday.responseListener.json.AliasHolder;
import com.chuger.bithdayapp.controller.chain.birthday.responseListener.parser.AbstractBirthdayParser;
import com.chuger.bithdayapp.controller.chain.birthday.responseListener.parser.impl.GoogleBdayParser;
import com.chuger.bithdayapp.controller.chain.chain.Chain;
import com.chuger.bithdayapp.model.dataSource.UserDataSource;
import com.chuger.bithdayapp.model.domain.User;
import com.chuger.bithdayapp.model.utils.BirthdayUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import static com.chuger.bithdayapp.model.utils.StringUtils.isNotEmpty;

/**
 * User: Администратор
 * Date: 17.02.13
 * Time: 16:43
 */
public class GoogleBirthdayResponse extends AbstractBdayResponse {
    private final static String TAG = GoogleBirthdayResponse.class.getSimpleName();
    private static final long serialVersionUID = -4414203603311123986L;

    public GoogleBirthdayResponse(Chain chain) {
        super(chain);
    }

    @Override
    public void parseJSONObject(JSONObject jsonObject, UserDataSource writeDataSource, UserDataSource readDataSource) throws JSONException {
        final JSONArray jsonUsers = jsonObject.getJSONObject("feed").getJSONArray("entry");
        Log.e(TAG, jsonUsers.toString());

        for (int i = 0, l = jsonUsers.length(); i < l; i++) {
            final JSONObject user = jsonUsers.getJSONObject(i);

            if (user.has("gContact$birthday") && user.has("gd$name")) {
                final JSONObject birthday = user.getJSONObject("gContact$birthday");
                final String birthdayDate = birthday.getString("when");

                final JSONObject fullName = user.getJSONObject("gd$name");
                if (fullName.has("gd$familyName") && fullName.has("gd$givenName")) {
                    final String lastName = fullName.getJSONObject("gd$familyName").getString("$t");
                    final String firstName = fullName.getJSONObject("gd$givenName").getString("$t");
                    if (isNotEmpty(birthdayDate, lastName, firstName)) {

                        final String uid = user.getJSONObject("id").getString("$t");
                        final User existedUser = findUserById(readDataSource, uid);
                        final User mergedUser;
                        final boolean isExist = existedUser != null;
                        if (isExist) {
                            mergedUser = existedUser;
                            Log.d(TAG, "User with uid= " + uid + " already exist");
                        } else {
                            mergedUser = new User();
                            setUid(mergedUser, uid);
                            Log.d(TAG, "User with uid = " + uid + " created");
                        }

                        mergedUser.setFirstName(firstName);
                        mergedUser.setLastName(lastName);

                        if (user.has("link")) {
                            final JSONArray links = user.getJSONArray("link");
                            for (int j = 0, ln = links.length(); j < ln; j++) {
                                final JSONObject link = links.getJSONObject(j);
                                if ("http://schemas.google.com/contacts/2008/rel#photo".equals(link.getString("rel"))) {
                                    final String href = link.getString("href");
                                    final String picUrl = href + "&access_token=" + getChain().getAccessToken();
                                    Log.e(TAG, picUrl);
                                    mergedUser.setPicUrl(picUrl);
                                }
                            }
                        }

                        BirthdayUtils.setBirthday(mergedUser, getAbstractBdayParser(birthdayDate));
                        if (isExist) {
                            writeDataSource.updateUser(mergedUser);
                        } else {
                            writeDataSource.createUser(mergedUser);
                        }
                        if (i % 10 == 0) {
                            UserDataSource.refreshListActivity();
                        }
                    }
                }

            }
        }
    }

    @Override
    public AliasHolder getAliasHolder() {
        return null;
    }

    @Override
    protected AbstractBirthdayParser getAbstractBdayParser(String bdayString) {
        return new GoogleBdayParser(bdayString);
    }

    @Override
    protected <T extends Serializable> void setUid(User user, T uid) {
        user.setGoogleId((String) uid);
    }

    @Override
    protected <T extends Serializable> User findUserById(UserDataSource readDataSource, T uid) {
        return readDataSource.findByGoogleId((String) uid);
    }
}
