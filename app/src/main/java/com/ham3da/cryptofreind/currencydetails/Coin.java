package com.ham3da.cryptofreind.currencydetails;

public class Coin
{

    public Coin(String symbol, String name, int icon)
    {
        Symbol = symbol;
        Name = name;
        Icon = icon;
    }

    private String Symbol;
    private String Name;
    private int Icon;

    public int getIcon()
    {
        return Icon;
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