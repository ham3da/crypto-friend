package com.ham3da.cryptofreind;

import android.app.Application;
import android.content.Intent;
import android.content.res.Configuration;

import androidx.annotation.NonNull;

import com.ham3da.cryptofreind.currencylist.CurrencyListTabsActivity;
import com.ham3da.cryptofreind.models.rest.CMCCoin;
import com.ham3da.cryptofreind.service.TickerService;

import java.util.ArrayList;

public class App extends Application
{

   private ArrayList<CMCCoin> cmcCoins;

    public void setCmcCoins(ArrayList<CMCCoin> cmcCoins)
    {
        this.cmcCoins = cmcCoins;
        sendUpdateCurrencyNotify();
    }

    public ArrayList<CMCCoin> getCmcCoins()
    {
        return cmcCoins;
    }

    private boolean tickerServiceRunning = false;

    public void setTickerServiceRunning(boolean tickerServiceRunning)
    {
        this.tickerServiceRunning = tickerServiceRunning;
    }

    public void sendUpdateCurrencyNotify()
    {
        Intent intent = new Intent("com.ham3da.cryptofreind.broadcast.UPDATE_PRICE");
        sendBroadcast(intent);
    }

    public void sendUpdateCurrencyFailNotify(String msg)
    {
        Intent intent = new Intent("com.ham3da.cryptofreind.broadcast.UPDATE_PRICE_FAIL");
        intent.putExtra("error", msg);
        sendBroadcast(intent);
    }

    public void resetTickerServiceTimer(int RepeatTime)
    {
        Intent serviceIntent = new Intent("com.ham3da.cryptofreind.broadcast.RESET_TIMER");
        serviceIntent.putExtra("RepeatTime", RepeatTime);
        sendBroadcast(serviceIntent);
    }

    public void updatePriceManually()
    {
        Intent serviceIntent = new Intent("com.ham3da.cryptofreind.broadcast.UPDATE_PRICE_MANUALLY");
        sendBroadcast(serviceIntent);
    }

    public void updateAlarmList()
    {
        Intent serviceIntent = new Intent("com.ham3da.cryptofreind.broadcast.UPDATE_ALARM");
        sendBroadcast(serviceIntent);
    }


    public boolean getTickerServiceRunning()
    {
        return  this.tickerServiceRunning;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        this.cmcCoins = new ArrayList<>();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
    }
}
