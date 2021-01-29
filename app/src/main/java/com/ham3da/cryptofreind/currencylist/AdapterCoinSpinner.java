package com.ham3da.cryptofreind.currencylist;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.ham3da.cryptofreind.R;
import com.ham3da.cryptofreind.currencydetails.Coin;

import java.util.List;
import java.util.Locale;

import static android.view.View.inflate;


public class AdapterCoinSpinner extends BaseAdapter implements SpinnerAdapter
{

    List<Coin> coins_list;
    Context context;

    public AdapterCoinSpinner(Context context, List<Coin> coins)
    {

        this.context = context;
        this.coins_list = coins;
    }

    @Override
    public int getCount()
    {
        return this.coins_list.size();
    }

    @Override
    public Coin getItem(int position)
    {
        return coins_list.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = inflate(context, R.layout.alarm_spiner_textview, null);
        TextView textView = view.findViewById(R.id.main);
        Coin coin = getItem(position);
        String name = coin.getName();
        textView.setText(name);
        final ImageView imageFlag = view.findViewById(R.id.imageFlag);
        imageFlag.setImageResource(coin.getIcon());

        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent)
    {

        View view;
        view = inflate(context, R.layout.alarm_spiner_item, null);
        final TextView textView = view.findViewById(R.id.dropdown);
        final ImageView imageFlag = view.findViewById(R.id.imageIcon);
        Coin coin = getItem(position);
        textView.setText(coin.getName());
        imageFlag.setImageResource(coin.getIcon());

        return view;
    }

    public int getAlarmIndex(String symbol)
    {
        int index = 0;
        if (symbol.isEmpty())
        {
            return index;
        }
        for (Coin coin : this.coins_list)
        {

            if (coin.getSymbol().equals(symbol))
            {
                break;
            }
            index++;
        }
        return index;
    }

    public int getCountryIndex(Coin coin)
    {
        int index = 0;
        if (coin == null)
        {
            return index;
        }
        return coins_list.indexOf(coin);
    }

}
