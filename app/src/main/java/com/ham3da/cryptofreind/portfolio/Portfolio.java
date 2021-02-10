package com.ham3da.cryptofreind.portfolio;

public class Portfolio
{
    public Portfolio()
    {

    }

    public Portfolio(int id, String symbol, Double amount, String recorded_date)
    {
        this.id = id;
        this.symbol = symbol;
        this.amount = amount;
        this.recorded_date = recorded_date;

    }

    private int id;
    private String symbol;
    private Double amount;
    private String recorded_date;



    public int getId()
    {
        return id;
    }
    public void setId(int id)
    {
        this.id = id;
    }

    public Double getAmount()
    {
        return amount;
    }
    public void setAmount(Double amount)
    {
        this.amount = amount;
    }

    public String getSymbol()
    {
        return symbol;
    }

    public void setSymbol(String symbol)
    {
        this.symbol = symbol;
    }

    public String getRecorded_date()
    {
        return recorded_date;
    }

    public void setRecorded_date(String recorded_date)
    {
        this.recorded_date = recorded_date;
    }
}
