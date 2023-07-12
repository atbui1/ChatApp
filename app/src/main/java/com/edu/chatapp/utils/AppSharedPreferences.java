package com.edu.chatapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class AppSharedPreferences {
    public static final String APP_SHARE_PREFERENCES = "APP_SHARE_PREFERENCES";
    public static final String APP_SHARE_LANGUAGE = "APP_SHARE_LANGUAGE";
    private Context mContext;

    public AppSharedPreferences(Context mContext) {
        this.mContext = mContext;
    }

    public void putStringValue(String key, String value) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(APP_SHARE_LANGUAGE, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getStringValue(String key) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(APP_SHARE_LANGUAGE, 0);
        return sharedPreferences.getString(key, null);
    }
}
