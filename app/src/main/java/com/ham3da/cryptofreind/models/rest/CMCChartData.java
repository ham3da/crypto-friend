package com.ham3da.cryptofreind.models.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


public class CMCChartData {


    @JsonProperty("price_btc")
    List<List<Float>> priceBTC;
    @JsonProperty("price_usd")
    List<List<Float>> priceUSD;


    public List<List<Float>> getPriceBTC() {
        return priceBTC;
    }

    public List<List<Float>> getPriceUSD() {
        return priceUSD;
    }

    public void setPriceBTC(List<List<Float>> priceBTC) {
        this.priceBTC = priceBTC;
    }

    public void setPriceUSD(List<List<Float>> priceUSD) {
        this.priceUSD = priceUSD;
    }
}
