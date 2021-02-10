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
            if ( symbolId.equals("t" + coin.getSymbol() + "USD") || symbolId.equals("t" + coin.getSymbol() + ":USD"))
            {
                res = coin;
                break;
            }
        }
        return res;
    }

    public Integer getCoinIndex(String symbolId)
    {
        int index_res = 0;
        int  res = 0;
        for (Coin coin : coinList)
        {
            if (symbolId.equals(coin.getSymbol()))
            {
                index_res = res;
                break;
            }
            res++;
        }
        return index_res;
    }

    public boolean checkCoinExist(String symbolId)
    {
        boolean  res = false;
        for (Coin coin : coinList)
        {
            if ( symbolId.equals("t" + coin.getSymbol() + "USD") || symbolId.equals("t" + coin.getSymbol() + ":USD"))
            {
                res = true;
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

        //V 1
        coinList.add(new Coin("BTC", mContext.getString(R.string.Bitcoin), R.drawable.ic_btc));
        coinList.add(new Coin("ETH", mContext.getString(R.string.Ethereum), R.drawable.ic_eth));
        coinList.add(new Coin("ETC", mContext.getString(R.string.ETC), R.drawable.ic_etc));
        coinList.add(new Coin("BSV", mContext.getString(R.string.Bitcoin_SV), R.drawable.ic_bsv));
        coinList.add(new Coin("LTC", mContext.getString(R.string.Litecoin), R.drawable.ic_ltc));
        coinList.add(new Coin("XMR", mContext.getString(R.string.Monero), R.drawable.ic_xmr));
        coinList.add(new Coin("ZEC", mContext.getString(R.string.ZEC), R.drawable.ic_zec));
        coinList.add(new Coin("XRP", mContext.getString(R.string.Ripple), R.drawable.ic_xrp));
        coinList.add(new Coin("DSH", mContext.getString(R.string.dashcoin), R.drawable.ic_dash));
        coinList.add(new Coin("EOS", mContext.getString(R.string.eos), R.drawable.ic_eos));
        coinList.add(new Coin("NEO", mContext.getString(R.string.neo), R.drawable.ic_neo));
        coinList.add(new Coin("BTG", mContext.getString(R.string.btg), R.drawable.ic_btg));
        coinList.add(new Coin("REP", mContext.getString(R.string.rep), R.drawable.ic_rep));
        coinList.add(new Coin("MKR", mContext.getString(R.string.mkr), R.drawable.ic_mkr));
        coinList.add(new Coin("RBT", mContext.getString(R.string.rimbit), R.drawable.ic_rbt));

        //V 2
        coinList.add(new Coin("UNI", mContext.getString(R.string.uniswap ), R.drawable.ic_uni));
        coinList.add(new Coin("OMG", mContext.getString(R.string.omg_network), R.drawable.ic_omg));
        coinList.add(new Coin("XLM", mContext.getString(R.string.stellar), R.drawable.ic_xlm));
        coinList.add(new Coin("DOT", mContext.getString(R.string.polkadot), R.drawable.ic_dot));
        coinList.add(new Coin("LINK", mContext.getString(R.string.chainlink), R.drawable.ic_link));//:
        coinList.add(new Coin("XTZ", mContext.getString(R.string.tezos), R.drawable.ic_xtz));
        coinList.add(new Coin("TRX", mContext.getString(R.string.tron), R.drawable.ic_trx));
        coinList.add(new Coin("ADA", mContext.getString(R.string.cardano), R.drawable.ic_ada));
        coinList.add(new Coin("YFI", mContext.getString(R.string.yearn_finance), R.drawable.ic_yfi));
        coinList.add(new Coin("LEO", mContext.getString(R.string.LEOcoin), R.drawable.ic_leo));


        this.coinList = coinList;
    }

    public List<Coin> getCoinList()
    {
        return coinList;
    }


}
