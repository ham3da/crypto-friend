package com.ham3da.cryptofreind.currencydetails;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.ham3da.cryptofreind.R;


import java.util.ArrayList;
import java.util.List;

public class Coins
{
    List<Coin> coinList;
    String tickerLink;
    Context mContext;


    public Coin getCoin(String symbolId)
    {

        Coin  res = null;
        for (Coin coin : coinList)
        {
            if (symbolId.equals("t" + coin.getSymbol() + "USD"))
            {
                res = coin;
                break;
            }
        }

        return res;
    }
    public Coin getCoinByMainSymbol(String symbolId)
    {

        Coin  res = null;
        for (Coin coin : coinList)
        {
            if (symbolId.equals(coin.getSymbol()))
            {
                res = coin;
                break;
            }
        }

        return res;
    }


    public Coins(Context context)
    {
        mContext = context;
        setCoinList();
    }


    private void setCoinList()
    {
        List<Coin> coinList = new ArrayList<>();

        coinList.add(new Coin("BTC", mContext.getString(R.string.Bitcoin), R.drawable.ic_btc));
        coinList.add(new Coin("ETH", mContext.getString(R.string.Ethereum), R.drawable.ic_eth));
        coinList.add(new Coin("ETC", mContext.getString(R.string.ETC), R.drawable.ic_etc));//

        coinList.add(new Coin("BSV", mContext.getString(R.string.Bitcoin_SV), R.drawable.ic_bsv));
        coinList.add(new Coin("LTC", mContext.getString(R.string.Litecoin), R.drawable.ic_ltc));
        coinList.add(new Coin("XMR", mContext.getString(R.string.Monero), R.drawable.ic_xmr));

        coinList.add(new Coin("ZEC", mContext.getString(R.string.ZEC), R.drawable.ic_zec));//
        coinList.add(new Coin("XRP", mContext.getString(R.string.Ripple), R.drawable.ic_xrp));
        coinList.add(new Coin("DSH", mContext.getString(R.string.dashcoin), R.drawable.ic_dash));

        coinList.add(new Coin("EOS", mContext.getString(R.string.eos), R.drawable.ic_eos));
        coinList.add(new Coin("NEO", mContext.getString(R.string.neo), R.drawable.ic_neo));
        coinList.add(new Coin("BTG", mContext.getString(R.string.btg), R.drawable.ic_btg));

        coinList.add(new Coin("REP", mContext.getString(R.string.rep), R.drawable.ic_rep));
        coinList.add(new Coin("MKR", mContext.getString(R.string.mkr), R.drawable.ic_mkr));
        coinList.add(new Coin("RBT", mContext.getString(R.string.rimbit), R.drawable.ic_rbt));

        this.coinList = coinList;
    }


    public String getTickerLinkSymboles()
    {
        List<String> list = new ArrayList<String>();

        for (Coin coin : this.coinList)
        {
            list.add("t" + coin.getSymbol() + "USD");
        }

        tickerLink = TextUtils.join(",", list);
        return tickerLink;
    }

    public List<Coin> getCoinList()
    {
        return coinList;
    }


}
