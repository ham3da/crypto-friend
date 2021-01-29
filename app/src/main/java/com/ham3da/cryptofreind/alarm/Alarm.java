package com.ham3da.cryptofreind.alarm;

public class Alarm
{
    public Alarm()
    {

    }

    public Alarm(int id, String symbol, Double price, int status, String rcorded_date, int alarm_type, int alarm_repeat)
    {
        this.id = id;
        this.symbol = symbol;
        this.price = price;
        this.status = status;
        this.rcorded_date = rcorded_date;
        this.alarm_type = alarm_type;
        this.alarm_repeat = alarm_repeat;
    }

    private int id;
    private String symbol;
    private Double price;
    private int status;
    private String rcorded_date;
    private int alarm_type, alarm_repeat;


    public int getId()
    {
        return id;
    }

    public Double getPrice()
    {
        return price;
    }

    public String getSymbol()
    {
        return symbol;
    }

    public int getStatus()
    {
        return status;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public void setPrice(Double price)
    {
        this.price = price;
    }

    public void setSymbol(String symbol)
    {
        this.symbol = symbol;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

    public int getAlarm_type()
    {
        return alarm_type;
    }

    public int getAlarm_repeat()
    {
        return alarm_repeat;
    }

    public String getRecorded_date()
    {
        return rcorded_date;
    }
}
