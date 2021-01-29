package com.ham3da.cryptofreind.rest;

import android.content.Context;
import android.util.Log;

import com.ham3da.cryptofreind.models.rest.CoinList;
import com.ham3da.cryptofreind.models.rest.MarketsResponse;
import com.ham3da.cryptofreind.models.rest.TradingPair;
import com.grizzly.rest.GenericRestCall;
import com.grizzly.rest.Model.afterTaskCompletion;
import com.grizzly.rest.Model.afterTaskFailure;

import org.springframework.http.HttpMethod;


/**
 * Created by fco on 11-01-18.
 */

public class CryptoCompareCoinService {

    public static final String ALL_COINS_LIST_URL = "https://min-api.cryptocompare.com/data/all/coinlist";
    public static final String TOP_PAIRS_URL = "https://min-api.cryptocompare.com/data/top/pairs?fsym=%s&limit=20";
    public static final String PAIR_MARKET_URL = "https://min-api.cryptocompare.com/data/top/exchanges/full?fsym=%s&tsym=%s&limit=25";

    public static void getAllCoins(Context context, afterTaskCompletion<CoinList> taskCompletion, afterTaskFailure failure, boolean async) {
        new GenericRestCall<>(Void.class, CoinList.class, String.class)
                .setUrl(ALL_COINS_LIST_URL)
                .setContext(context.getApplicationContext())
                .isCacheEnabled(true)
                .setCacheTime(604800000L)
                .setMethodToCall(HttpMethod.GET)
                .setTaskCompletion(taskCompletion)
                .setTaskFailure(failure)
                .setAutomaticCacheRefresh(true).execute(async);
    }

    public static void getTopPairs(Context context, String symbol, afterTaskCompletion<TradingPair> taskCompletion, afterTaskFailure failure) {
        String url = String.format(TOP_PAIRS_URL, symbol);
        new GenericRestCall<>(Void.class, TradingPair.class, String.class)
                .setUrl(url)
                .setContext(context.getApplicationContext())
                .isCacheEnabled(true)
                // Cache top pairs for 1hr
                .setCacheTime(3600000L)
                .setMethodToCall(HttpMethod.GET)
                .setTaskCompletion(taskCompletion)
                .setTaskFailure(failure)
                .setAutomaticCacheRefresh(false).execute(true);
    }

    public static void getPairsMarket(Context context, String tsymbol, String fsymbol, afterTaskCompletion<MarketsResponse> taskCompletion, afterTaskFailure failure) {
        String url = String.format(PAIR_MARKET_URL, tsymbol, fsymbol);
        Log.d("I", "Markets URL: " + url);
        new GenericRestCall<>(Void.class, MarketsResponse.class, String.class)
                .setUrl(url)
                .setContext(context.getApplicationContext())
                .isCacheEnabled(true)
                // Cache markets for 5min
                .setCacheTime(300000L)
                .setMethodToCall(HttpMethod.GET)
                .setTaskCompletion(taskCompletion)
                .setTaskFailure(failure)
                .setAutomaticCacheRefresh(false).execute(true);
    }


}
