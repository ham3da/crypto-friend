package com.ham3da.cryptofreind.service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;

import com.ham3da.cryptofreind.App;
import com.ham3da.cryptofreind.AppSettings;
import com.ham3da.cryptofreind.currencylist.CurrencyListTabsActivity;

public class AutoStart extends BroadcastReceiver
{
    AppSettings appSettings;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        appSettings = new AppSettings(context.getApplicationContext());
        int  delayTime = appSettings.getUpdateTime();

        String action = intent.getAction();
        if(Intent.ACTION_BOOT_COMPLETED.equals(action))
        {
            App app = (App) context.getApplicationContext();
            if (appSettings.getServiceUpdate())
            {
                if (!app.getTickerServiceRunning())
                {
                    Intent service = new Intent(context, TickerService.class);
                    service.putExtra("delay", delayTime);
                    ContextCompat.startForegroundService(context, service);
                }
            }
        }
    }
}


