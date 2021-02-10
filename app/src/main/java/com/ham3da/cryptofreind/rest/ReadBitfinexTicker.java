package com.ham3da.cryptofreind.rest;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ham3da.cryptofreind.App;
import com.ham3da.cryptofreind.CFUtility;
import com.ham3da.cryptofreind.R;
import com.ham3da.cryptofreind.currencydetails.Coins;
import com.ham3da.cryptofreind.models.rest.CMCCoin;
import com.ham3da.cryptofreind.service.AlarmNotify;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Map;

public class ReadBitfinexTicker
{

    String Tag = "ReadBitfinexTicker";
    Context mContext;

    public ReadBitfinexTicker(Context context)
    {
        mContext = context;
    }

    public void updateCurrencyList(boolean checkAlarms)
    {
        App app = (App) mContext.getApplicationContext();

        if (!CFUtility.isNetworkConnected(mContext))
        {
            app.sendUpdateCurrencyFailNotify(mContext.getString(R.string.no_internet));
            return;
        }

        StringRequest stringRequest;
        RequestQueue queue;
        AlarmNotify alarmNotify = new AlarmNotify(mContext);


        Coins coins = new Coins(mContext);
        String url = "https://api.ham3da.ir/v2/?my_action=get_prices";
        stringRequest = new StringRequest(Request.Method.GET, url,
                response ->
                {
                    if (response.equals("[]"))
                    {
                        Toast.makeText(mContext, R.string.no_items + "[E1]", Toast.LENGTH_SHORT).show();
                        app.sendUpdateCurrencyFailNotify(mContext.getString(R.string.no_items) + "[E1]");
                    }
                    else
                    {
                        try
                        {
                            ArrayList<CMCCoin> cmcCoinArrayList = new ArrayList<>();
                            JSONArray array = new JSONArray(response);
                            for (int i = 0; i < array.length(); i++)
                            {

                                CMCCoin cmcCoin1 = new CMCCoin(array.getJSONArray(i));
                                if (coins.checkCoinExist(cmcCoin1.getSymbol()))
                                {
                                    cmcCoinArrayList.add(cmcCoin1);
                                }
                            }
                            app.setCmcCoins(cmcCoinArrayList);
                            if (checkAlarms)
                            {
                                alarmNotify.checkAlarms(cmcCoinArrayList);
                            }

                        } catch (JSONException e)
                        {
                            Log.e(Tag, "updateCurrencyList: " + e.getMessage());
                            app.sendUpdateCurrencyFailNotify(mContext.getString(R.string.has_error_server) + "[E2]");

                        }
                    }

                },
                error ->
                {
                    app.sendUpdateCurrencyFailNotify(mContext.getString(R.string.has_error_server) + "[E3]");
                    Log.e(Tag, "onErrorResponse: " + error.getMessage());
                }
        )
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                return super.getParams();
            }
        };

        queue = Volley.newRequestQueue(mContext);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, 3,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        stringRequest.setShouldCache(false);
        queue.add(stringRequest);


    }
}
