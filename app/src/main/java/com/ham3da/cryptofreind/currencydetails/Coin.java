package com.ham3da.cryptofreind.currencydetails;

public class Coin
{

    public Coin(String symbol, String name, int icon, String id)
    {
        Symbol = symbol;
        Name = name;
        Icon = icon;
        ID = id;
    }

    private String Symbol;
    private String Name, ID;
    private int Icon;

    public String getID()
    {
        return ID;
    }

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