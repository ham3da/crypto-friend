package com.ham3da.cryptofreind.rest;

import android.content.Context;

import com.ham3da.cryptofreind.currencydetails.Coins;
import com.ham3da.cryptofreind.models.rest.CMCCoin;
import com.grizzly.rest.GenericRestCall;
import com.grizzly.rest.Model.afterTaskCompletion;
import com.grizzly.rest.Model.afterTaskFailure;

import org.springframework.http.HttpMethod;

/**
 * Created by Ryan on 1/16/2018.
 */

public class CoinMarketCapService
{

    public static void getAllCoins(Context context, afterTaskCompletion<CMCCoin[]> taskCompletion,
                                   afterTaskFailure failure, boolean async, String tickerLink)
    {


        new GenericRestCall<>(Void.class, CMCCoin[].class, String.class)
                .setUrl(tickerLink)
                .setContext(context.getApplicationContext())
                .isCacheEnabled(true)
                .setCacheTime(300000L)
                .setMethodToCall(HttpMethod.GET)
                .setTaskCompletion(taskCompletion)
                .setTaskFailure(failure)
                .setAutomaticCacheRefresh(true).execute(async);
    }


}
