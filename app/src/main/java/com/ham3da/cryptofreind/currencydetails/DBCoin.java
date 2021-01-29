package com.ham3da.cryptofreind.currencydetails;

public class DBCoin
{

    public DBCoin()
    {
    }

    public DBCoin(String symbol, String name, Double price, Double dailyChangeRelative, Double dailyChange)
    {
        Symbol = symbol;
        Name = name;
        Price = price;
        DailyChangeRelative = dailyChangeRelative;
        DailyChange = dailyChange;
    }

    private String Symbol;
    private String Name;
    private Double Price;
    private Double DailyChangeRelative;
    private Double DailyChange;

    public void setSymbol(String symbol)
    {
        Symbol = symbol;
    }

    public void setName(String name)
    {
        Name = name;
    }

    public void setDailyChange(Double dailyChange)
    {
        DailyChange = dailyChange;
    }

    public void setDailyChangeRelative(Double dailyChangeRelative)
    {
        DailyChangeRelative = dailyChangeRelative;
    }

    public void setPrice(Double price)
    {
        Price = price;
    }

    public Double getPrice()
    {
        return Price;
    }

    public Double getDailyChange()
    {
        return DailyChange;
    }

    public Double getDailyChangeRelative()
    {
        return DailyChangeRelative;
    }


    public String getSymbol()
    {
        return Symbol;
    }

    public String getName()
    {
        return Name;
    }
}