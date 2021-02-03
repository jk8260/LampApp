package com.elasalle.lamp.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.elasalle.lamp.LampApp;

public class PreferencesHelper {

    private static final String PREFS_NAME = "LAMP Preferences";
    private static final String KEY_ABOUT_TIME = "About timestamp";
    private static final String KEY_INTRODUCTION_DISPLAYED = "Introduction displayed";
    private static final String KEY_USERNAME = "Username";
    private static final String KEY_NOTIFICATIONS_ON = "Notifications On";
    private static final String KEY_SEARCH_TIME = "Search timestamp";
    private static final String KEY_LOOKUP_TIME = "Lookup timestamp";
    private static final String KEY_SCAN_TIME = "Scan timestamp";
    private static final String KEY_NOTIFICATIONS_TIME = "Notifications timestamp";
    private static final String DEFAULT_TIME = "2000-01-01T12:00:00";
    private static final String KEY_REMEMBER_ME = "KEY_REMEMBER_ME";

    public static boolean turnNotificationsOn(final boolean isOn) {
        return save(getEditor().putBoolean(KEY_NOTIFICATIONS_ON, isOn));
    }

    public static boolean isNotificationsOn() {
        return getSharedPreferences().getBoolean(KEY_NOTIFICATIONS_ON, false);
    }

    public static boolean saveAboutTime(final String aboutTime) {
        return save(getEditor().putString(KEY_ABOUT_TIME, aboutTime));
    }

    public static String getAboutTime() {
        return getSharedPreferences().getString(KEY_ABOUT_TIME, DEFAULT_TIME);
    }

    public static boolean saveIntroductionDisplayed() {
        return save(getEditor().putBoolean(KEY_INTRODUCTION_DISPLAYED, true));
    }

    public static boolean getIntroductionDisplayed() {
        return getSharedPreferences().getBoolean(KEY_INTRODUCTION_DISPLAYED, false);
    }

    public static boolean saveRememberMe(Boolean rememberMe) {
        return save(getEditor().putBoolean(KEY_REMEMBER_ME, rememberMe));
    }

    public static boolean getRememberMe() {
        return getSharedPreferences().getBoolean(KEY_REMEMBER_ME, false);
    }

    public static boolean saveUsername(String username) {
        return save(getEditor().putString(KEY_USERNAME, username));
    }

    public static String getUsername() {
        return getSharedPreferences().getString(KEY_USERNAME, "");
    }

    public static boolean removeUsername() {
        return save(getEditor().remove(KEY_USERNAME));
    }

    private static SharedPreferences.Editor getEditor() {
        return getSharedPreferences().edit();
    }

    private static SharedPreferences getSharedPreferences() {
        return LampApp.getInstance().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    private static boolean save(final SharedPreferences.Editor editor) {
        return editor.commit();
    }

    public static boolean saveSearchTime(final String aboutTime) {
        return save(getEditor().putString(KEY_SEARCH_TIME, aboutTime));
    }

    public static String getSearchTime() {
        return getSharedPreferences().getString(KEY_SEARCH_TIME, DEFAULT_TIME);
    }

    public static boolean saveLookupTime(final String aboutTime) {
        return save(getEditor().putString(KEY_LOOKUP_TIME, aboutTime));
    }

    public static String getLookupTime() {
        return getSharedPreferences().getString(KEY_LOOKUP_TIME, DEFAULT_TIME);
    }

    public static boolean saveScanTime(final String aboutTime) {
        return save(getEditor().putString(KEY_SCAN_TIME, aboutTime));
    }

    public static String getScanTime() {
        return getSharedPreferences().getString(KEY_SCAN_TIME, DEFAULT_TIME);
    }

    public static boolean saveNotificationsTime(final String aboutTime) {
        return save(getEditor().putString(KEY_NOTIFICATIONS_TIME, aboutTime));
    }

    public static String getNotificationsTime() {
        return getSharedPreferences().getString(KEY_NOTIFICATIONS_TIME, DEFAULT_TIME);
    }
}
