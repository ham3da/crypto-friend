package com.ham3da.cryptofreind;

import android.content.Context;
import android.graphics.Typeface;

import androidx.core.content.res.ResourcesCompat;

public class AppSettings
{

    private static PreferenceHelper PreferenceManager1 = null;

    public AppSettings(Context context)
    {
       PreferenceManager1 = new PreferenceHelper(context);
    }

    public int getUpdateTime()
    {
        String s = PreferenceManager1.getKey("price_updates_frequency", "15");
        return  Integer.parseInt( s);
    }

    public boolean getServiceUpdate()
    {
        return PreferenceManager1.getKey("price_updates_service_status", true);
    }
}
