package com.mgosu.callscreen.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferencesUtils {

    private SharedPreferences preferences;
    private static PreferencesUtils instance;

    private PreferencesUtils(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static PreferencesUtils getInstance(Context context) {
        if (instance == null) {
            instance = new PreferencesUtils(context);
        }
        return instance;
    }

    public String getCur3DWallpaper() {
        return getStringValue("key_3dwallpaper");
    }
    public void setCur3DWallpaper(String value) {
        putStringValue("key_3dwallpaper", value);
    }

    public String getUrlVideoPath() {
        return getStringValue("key_videopath");
    }
    public void setUrlVideoPath(String value) {
        putStringValue("key_videopath", value);
    }

    public boolean getStateCallFlashSetting() {
        return getBooleanValue("key_settingcallflash");
    }
    public void setStateCallFlashSetting(boolean value) {
        putBooleanValue("key_settingcallflash", value);
    }


    public String getCurLiveWallpaper() {
        return getStringValue("key_CurLiveWallpaper");
    }
    public void setCurLiveWallpaper(String value) {
        putStringValue("key_CurLiveWallpaper", value);
    }

    public void setCheckChange(boolean idDefault) {
        preferences.edit().putBoolean("KEY_CheckChange", idDefault).apply();
    }

    public boolean getCheckChange() {
        return preferences.getBoolean("KEY_CheckChange", false);
    }

    public void setCheckChangeWallpaper(boolean idDefault,String category) {
        preferences.edit().putBoolean("KEY_CheckChange"+category, idDefault).apply();
    }

    public boolean getCheckChangeWallpaper(String category) {
        return preferences.getBoolean("KEY_CheckChange"+category, false);
    }

    public void setKeyRequestChange(String s) {
        preferences.edit().putString("KEY_KeyRequestChange",s ).apply();
    }

    public String getKeyRequestChange() {
        return preferences.getString("KEY_KeyRequestChange", "");
    }

    public void setChangeSwLed(boolean s) {
        preferences.edit().putBoolean("KEY_ChangeSwLed",s ).apply();
    }

    public boolean getChangeSwLed() {
        return preferences.getBoolean("KEY_ChangeSwLed", true);
    }



    /**
     * @param key
     * @param s
     */
    public void putStringValue(String key, String s) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, s);
        editor.commit();
    }


    public void putFloatValue(String key, Float s) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(key, s);
        editor.commit();
    }

    public void putLongValue(String key, long l) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(key, l);
        editor.commit();
    }

    public Long getLongValue(String key) {
        return preferences.getLong(key, 0);
    }

    public void putIntValue(String key, int value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public void putBooleanValue(String key, Boolean s) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, s);
        editor.commit();
    }

    public boolean getBooleanValue(String key) {
        return preferences.getBoolean(key, false);
    }

    public String getStringValue(String key) {
        return preferences.getString(key, "");
    }

    public Float getFloatValue(String key) {
        return preferences.getFloat(key, (float) 0);
    }

    public int getIntValue(String key) {
        return preferences.getInt(key, -1);
    }

    public int getIntValueWithDefault(String key,int defaultValue) {
        return preferences.getInt(key, defaultValue);
    }

}
