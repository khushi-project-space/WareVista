package com.example.warevista;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import java.util.Locale;

public class LanguageManager {

    private static final String PREF_NAME = "WareVista";
    private static final String LANGUAGE_KEY = "language";

    public static void setLanguage(Context context, String languageCode) {

        SharedPreferences preferences =
                context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        preferences.edit()
                .putString(LANGUAGE_KEY, languageCode)
                .apply();

        applyLanguage(context, languageCode);
    }

    public static String getLanguage(Context context) {

        SharedPreferences preferences =
                context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        return preferences.getString(LANGUAGE_KEY, "en");
    }

    public static void applySavedLanguage(Context context) {

        String languageCode = getLanguage(context);

        applyLanguage(context, languageCode);
    }

    private static void applyLanguage(Context context, String languageCode) {

        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Configuration configuration =
                new Configuration(context.getResources().getConfiguration());

        configuration.setLocale(locale);

        context.getResources().updateConfiguration(
                configuration,
                context.getResources().getDisplayMetrics()
        );
    }
}