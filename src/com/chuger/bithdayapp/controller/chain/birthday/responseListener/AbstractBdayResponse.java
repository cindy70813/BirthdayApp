package com.chuger.bithdayapp.controller.chain.birthday.responseListener;

import android.util.Log;
import com.chuger.bithdayapp.controller.chain.birthday.responseListener.json.AliasHolder;
import com.chuger.bithdayapp.controller.chain.birthday.responseListener.parser.AbstractBirthdayParser;
import com.chuger.bithdayapp.controller.chain.chain.Chain;
import com.chuger.bithdayapp.model.dataSource.UserDataSource;
import com.chuger.bithdayapp.model.domain.User;
import com.chuger.bithdayapp.model.utils.BirthdayUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.sql.SQLException;

import static com.chuger.bithdayapp.model.dataSource.UserDataSource.getInstance;
import static com.chuger.bithdayapp.model.utils.StringUtils.isNotEmpty;

/**
 * User: Acer5740
 * Date: 25.02.12
 * Time: 5:24
 */
public abstract class AbstractBdayResponse implements BirthdayResponse {
    private Chain chain;
    private final static String TAG = AbstractBdayResponse.class.getSimpleName();

    public abstract AliasHolder getAliasHolder();

    public AbstractBdayResponse(Chain chain) {
        this.chain = chain;
    }

    @Override
    public void processResponse(String response) {
        final UserDataSource writeDataSource = getInstance();
        final UserDataSource readDataSource = getInstance();
        try {
            writeDataSource.openWrite();
            readDataSource.openRead();
            final JSONObject jsonObject = new JSONObject(response);
            parseJSONObject(jsonObject, writeDataSource, readDataSource);

            writeDataSource.close();
            readDataSource.close();

        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (ClassCastException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage(), e);
            writeDataSource.close();
            readDataSource.close();
        }
    }

    public void parseJSONObject(JSONObject jsonObject, UserDataSource writeDataSource, UserDataSource readDataSource) throws JSONException {
        final AliasHolder aliasHolder = getAliasHolder();
        final JSONArray jsonUsers = jsonObject.getJSONArray(getAliasHolder().getJsonRootAlias());
        Log.e(TAG, jsonUsers.toString());

        for (int i = 0, l = jsonUsers.length(); i < l; i++) {
            final JSONObject user = (JSONObject) jsonUsers.get(i);
            if (user.has(aliasHolder.getBirthdayAlias())) {
                final String birthdayDate = user.getString(aliasHolder.getBirthdayAlias());
                if (isNotEmpty(birthdayDate) && !"null".equals(birthdayDate)) {
                    final Long uid = Long.valueOf(user.getString(aliasHolder.getIdAlias()));
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

                    mergedUser.setFirstName(user.getString(aliasHolder.getFirstNameAlias()));
                    mergedUser.setLastName(user.getString(aliasHolder.getLastNameAlias()));
                    mergedUser.setPicUrl(user.getString(aliasHolder.getPicUrlAlias()).replaceAll("\\/", "/"));
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

    protected abstract AbstractBirthdayParser getAbstractBdayParser(final String bdayString);

    protected abstract <T extends Serializable> void setUid(User user, T uid);

    protected abstract <T extends Serializable> User findUserById(UserDataSource readDataSource, T uid);

    @Override
    public Chain getChain() {
        return chain;
    }
}
