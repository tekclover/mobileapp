package com.tvhht.myapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tvhht.myapplication.home.model.DashBoardReportCount;
import com.tvhht.myapplication.login.model.UserDetails;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class WMSSharedPref {
    @Nullable
    private static WMSSharedPref appPrefrence = null;
    @NonNull
    private final Context mContext;
    private Gson gson;
    @Nullable
    private SharedPreferences preferences = null;

    private WMSSharedPref(@NonNull Context context) {
        preferences =
                context.getSharedPreferences("WMS_SHARED_PREF", Context.MODE_PRIVATE);
        this.mContext = context;
    }

    public static WMSSharedPref getAppPrefs(@NonNull Context context) {
        synchronized (WMSSharedPref.class) {
            if (appPrefrence == null) appPrefrence = new WMSSharedPref(context);
        }

        return appPrefrence;
    }

    public void saveStringValue(@NonNull String key, String value) {
        SharedPreferences.Editor editor = Objects.requireNonNull(preferences).edit();
        editor.putString(key, value);
        editor.apply();

    }

    public void saveStringSet(@NonNull String key, Set<String> setValue) {
        SharedPreferences.Editor editor = Objects.requireNonNull(preferences).edit();
        editor.putStringSet(key, setValue);
        editor.apply();
    }


    public Set<String> getStringSet(@NonNull String key) {
        return Objects.requireNonNull(preferences).getStringSet(key, null);
    }

    public int getIntValue(String key) {
        return Objects.requireNonNull(preferences).getInt(key, -1);
    }

    public void saveIntValue(String key, int value) {
        SharedPreferences.Editor editor = Objects.requireNonNull(preferences).edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public void saveLongValue(String key, Long value) {
        SharedPreferences.Editor editor = Objects.requireNonNull(preferences).edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public long getLongValue(String key) {
        return Objects.requireNonNull(preferences).getLong(key, -1);
    }

    public void putDouble(final String key, final double value) {
        SharedPreferences.Editor editor = Objects.requireNonNull(preferences).edit();
        editor.putLong(key, Double.doubleToRawLongBits(value));
        editor.apply();
    }

    public double getDouble(final String key, final double defaultValue) {
        return Double.longBitsToDouble(Objects.requireNonNull(preferences).getLong(key, Double.doubleToLongBits(defaultValue)));
    }

    public String getStringValue(String key) {
        if (null == preferences)
            return "";
        return preferences.getString(key, "");
    }

    public void saveBooleanValue(String key, boolean value) {
        SharedPreferences.Editor editor = Objects.requireNonNull(preferences).edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBooleanValue(String key) {
        return Objects.requireNonNull(preferences).getBoolean(key, false);
    }

    private Gson getGson() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }

    //To save Hash Map
    public void saveMap(Map<String, String> inputMap, String key) {

        if (preferences != null && inputMap != null) {
            String hashMapString = getGson().toJson(inputMap);
            //save in shared prefs
            preferences.edit().putString(key, hashMapString).apply();
        }
    }

    public Map<String, String> getMap(String key) {
        Map<String, String> outputMap = new HashMap<>();
        try {
            //get from shared prefs
            String storedHashMapString = preferences.getString(key, (new JSONObject()).toString());
            java.lang.reflect.Type type = new TypeToken<HashMap<String, String>>() {
            }.getType();
            return getGson().fromJson(storedHashMapString, type);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return outputMap;
    }

    /**
     * Save and get ArrayList in SharedPreference
     */

    public void saveArrayList(List<String> list, String key) {
        if (list != null) {
            SharedPreferences.Editor editor = Objects.requireNonNull(preferences).edit();
            String json = getGson().toJson(list);
            editor.putString(key, json);
            editor.apply();
        }
    }

    public ArrayList<String> getArrayList(String key) {
        String json = preferences.getString(key, null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        return getGson().fromJson(json, type);
    }


    public void clearSharedPrefs(String key) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(key);
        editor.apply();
    }

    public void clearAllSharedPrefs() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

    public void saveUserInfoVO(List<UserDetails> userList) {
        if (userList != null) {
            SharedPreferences.Editor editor = Objects.requireNonNull(preferences).edit();
            String json = getGson().toJson(userList);
            editor.putString("USER_DETAILS_LIST", json);
            editor.apply();
        }
    }

    @Nullable
    public List<UserDetails> getUserInfo() {
        String json = Objects.requireNonNull(preferences).getString("USER_DETAILS_LIST", "");
        if (TextUtils.isEmpty(json)) {
            return null;
        } else {
            Type founderListType = new TypeToken<ArrayList<UserDetails>>() {
            }.getType();

            return getGson().fromJson(json, founderListType);
        }
    }


    public void saveUserInfoVOSingle(UserDetails userList) {
        if (userList != null) {
            SharedPreferences.Editor editor = Objects.requireNonNull(preferences).edit();
            String json = getGson().toJson(userList);
            editor.putString("USER_DETAILS_LIST_SINGLE", json);
            editor.apply();
        }
    }

    @Nullable
    public UserDetails getUserInfoSingle() {
        String json = Objects.requireNonNull(preferences).getString("USER_DETAILS_LIST_SINGLE", "");
        if (TextUtils.isEmpty(json)) {
            return null;
        } else {
            Type founderListType = new TypeToken<UserDetails>() {
            }.getType();

            return getGson().fromJson(json, founderListType);
        }
    }


    public void saveCountInfo(DashBoardReportCount userList) {
        if (userList != null) {
            SharedPreferences.Editor editor = Objects.requireNonNull(preferences).edit();
            String json = getGson().toJson(userList);
            editor.putString("USER_COUNT", json);
            editor.apply();
        }
    }

    @Nullable
    public DashBoardReportCount getCountInfo() {
        String json = Objects.requireNonNull(preferences).getString("USER_COUNT", "");
        if (TextUtils.isEmpty(json)) {
            return null;
        } else {
            Type founderListType = new TypeToken<DashBoardReportCount>() {
            }.getType();

            return getGson().fromJson(json, founderListType);
        }
    }






}
