package com.ham3da.cryptofreind.models.rest;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

//import com.fasterxml.jackson.annotation.JsonProperty;
//import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.gson.annotations.SerializedName;
import com.ham3da.cryptofreind.currencydetails.Coins;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by Javad Ahshamian on 2021-01-11
 */

public class CMCCoin
{


    public CMCCoin()
    {

    }

    public CMCCoin(JSONArray jsonArray) throws JSONException
    {
        symbol = jsonArray.getString(0);
        BID = jsonArray.getDouble(1);
        BID_SIZE = jsonArray.getInt(2);
        ASK = jsonArray.getDouble(3);
        ASK_SIZE = jsonArray.getDouble(4);
        DAILY_CHANGE = jsonArray.getDouble(5);
        DAILY_CHANGE_RELATIVE = jsonArray.getDouble(6);
        LAST_PRICE = jsonArray.getDouble(7);
        VOLUME = jsonArray.getDouble(8);
        HIGH = jsonArray.getDouble(9);
        LOW = jsonArray.getDouble(10);

    }


    public String getId()
    {
        return symbol;
    }

//    SYMBOL,0
//    BID,1
//    BID_SIZE,2
//    ASK,3
//    ASK_SIZE,4
//    DAILY_CHANGE,5
//    DAILY_CHANGE_RELATIVE,6
//    LAST_PRICE,7
//    VOLUME,8
//    HIGH, 9
//    LOW 10


    public String getName(Context context)
    {
        Coins coins = new Coins(context);
        return coins.getCoin(symbol).getName();
    }


    /**
     * symbol of api item
     */
    private String symbol;

    public String getSymbol()
    {
        return symbol;
    }

    public void setSymbol(String symbol)
    {
        this.symbol = symbol;
    }


    /**
     * Price of last highest bid
     */
    private double BID;


    public Double getBID()
    {
        return BID;
    }

    public void setBID(float BID)
    {
        this.BID = BID;
    }


    /**
     * Sum of the 25 highest bid sizes
     */
    private Integer BID_SIZE;

    public Integer getBID_SIZE()
    {
        return BID_SIZE;
    }


    /**
     * Price of last lowest ask
     */
    private Double ASK;

    public Double getASK()
    {
        return ASK;
    }

    /**
     * Sum of the 25 lowest ask sizes
     */
    private Double ASK_SIZE;

    public Double getASK_SIZE()
    {
        return ASK_SIZE;
    }

    /**
     * Amount that the last price has changed since yesterday
     */
    private Double DAILY_CHANGE;

    public Double getDAILY_CHANGE()
    {
        return DAILY_CHANGE;
    }


    /**
     * Percentage change
     */
    private Double DAILY_CHANGE_RELATIVE;

    public Double getDAILY_CHANGE_RELATIVE()
    {
        return DAILY_CHANGE_RELATIVE;
    }


    /**
     * Price of the last trade
     */
    private Double LAST_PRICE;

    public Double getLAST_PRICE()
    {
        return LAST_PRICE;
    }

    /**
     * Daily volume
     */
    private Double VOLUME;

    public Double getVOLUME()
    {
        return VOLUME;
    }


    /**
     * Daily high
     */
    private Double HIGH;

    public Double getHIGH()
    {
        return HIGH;
    }


    /**
     * Daily low
     */
    private Double LOW;

    public Double getLOW()
    {
        return LOW;
    }


}
