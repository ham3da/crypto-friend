package com.ham3da.cryptofreind;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class PreferenceHelper
{
    private final SharedPreferences mPrefs;

    public PreferenceHelper(Context context) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }


    public String getKey(String key, String def) {
        return mPrefs.getString(key, def);
    }

    public Boolean getKey(String key, Boolean def ) {
        return mPrefs.getBoolean(key, def);
    }

    public int getKey(String key, int def ) {
        return mPrefs.getInt(key, def);
    }


    public long getKey(String key, long def ) {
        return mPrefs.getLong(key, def);
    }

    public void setKey(String key, String value)
    {
        SharedPreferences.Editor mEditor = mPrefs.edit();
        mEditor.putString(key, value);
        mEditor.apply();
        mEditor.commit();
    }

    public void setKey(String key, Boolean value)
    {
        SharedPreferences.Editor mEditor = mPrefs.edit();
        mEditor.putBoolean(key, value);
        mEditor.apply();
        mEditor.commit();
    }

    public void setKey(String key, int value)
    {
        SharedPreferences.Editor mEditor = mPrefs.edit();
        mEditor.putInt(key, value);
        mEditor.apply();
        mEditor.commit();
    }

    public void setKey(String key, long value)
    {
        SharedPreferences.Editor mEditor = mPrefs.edit();
        mEditor.putLong(key, value);
        mEditor.apply();
        mEditor.commit();
    }

    public void removeKey(String key)
    {
        SharedPreferences.Editor mEditor = mPrefs.edit();
        mEditor.remove("ExpiredDate");
        mEditor.apply();
        mEditor.commit();
    }

}