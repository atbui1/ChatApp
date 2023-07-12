package com.edu.chatapp.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

public class LanguageManager {
    public static final String KEY_LANGUAGE = "KEY_LANGUAGE";
    private Context mContext;

    private AppSharedPreferences mAppSharedPreferences;

    public LanguageManager(Context mContext) {
        this.mContext = mContext;
    }

    public void updateResource(String code) {
        Locale locale = new Locale(code);
        Locale.setDefault(locale);
        Resources resources = mContext.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        mAppSharedPreferences = new AppSharedPreferences(mContext);
        mAppSharedPreferences.putStringValue(KEY_LANGUAGE, code);

    }
}
