package com.ham3da.cryptofreind.currencylist;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.ham3da.cryptofreind.App;
import com.ham3da.cryptofreind.CustomItemClickListener;
import com.ham3da.cryptofreind.DBHelper;
import com.ham3da.cryptofreind.R;
import com.ham3da.cryptofreind.alarm.Alarm;
import com.ham3da.cryptofreind.currencydetails.Coin;
import com.ham3da.cryptofreind.currencydetails.Coins;
import com.ham3da.cryptofreind.models.rest.CMCCoin;
import com.ham3da.cryptofreind.portfolio.Portfolio;

import java.util.ArrayList;
import java.util.Locale;


public class AdapterPortfolio extends RecyclerView.Adapter<AdapterPortfolio.ViewHolder>
{

    private ArrayList<Portfolio> portfolioList;
    private final String recordedStringResource;
    private final String priceStringResource;
    private final CustomItemClickListener rowListener;
    DBHelper dbHelper;
    App app;

    private final Coins coins;

    private final Context mContext;

    Fragment fmContext;

    private ArrayList<CMCCoin> cmcCoinArrayList;
    int negativeRedColor, positiveGreenColor;

    String negativePercentStringResource, positivePercentStringResource, pctChangeNotAvailableStringResource;

    public AdapterPortfolio(ArrayList<Portfolio> portfolios, AppCompatActivity context, Fragment fm_context,  CustomItemClickListener listener)
    {
        this.portfolioList = portfolios;
        this.rowListener = listener;

        this.priceStringResource = context.getString(R.string.unrounded_price_format);
        this.recordedStringResource = context.getString(R.string.recorded_s);

        negativeRedColor = context.getResources().getColor(R.color.percentNegativeRed);
         positiveGreenColor = context.getResources().getColor(R.color.percentPositiveGreen);

        this.negativePercentStringResource = context.getString(R.string.negative_pct_change_format);
        this.positivePercentStringResource = context.getString(R.string.positive_pct_change_format);
        this.pctChangeNotAvailableStringResource = context.getString(R.string.not_available_pct_change_text_with_time);

        this.coins = new Coins(context);
        dbHelper = new DBHelper(context);
        mContext = context;
        fmContext = fm_context;

        app = (App) context.getApplicationContext();

       // cmcCoinArrayList = app.getCmcCoins();

    }


    private CMCCoin getCMCCoin(String symbol)
    {
        cmcCoinArrayList = app.getCmcCoins();
        CMCCoin cmcCoin1 = null;
        for (CMCCoin cmcCoin : cmcCoinArrayList)
        {
            if (cmcCoin.getSymbol().equals("t" + symbol + "USD") || cmcCoin.getSymbol().equals("t" + symbol + ":USD"))
            {
                cmcCoin1 = cmcCoin;
                break;
            }
        }
        return cmcCoin1;
    }

    public void setRemovePortfolio(final AdapterPortfolio.ViewHolder holder, final int position)
    {
        Portfolio portfolio = portfolioList.get(position);
        holder.trashButton.setOnClickListener(v ->
        {
            String msg = mContext.getString(R.string.delete_q);

            new AlertDialog.Builder(mContext)
                    .setTitle(R.string.remove_alarm)
                    .setMessage(msg)
                    .setIcon(R.drawable.ic_baseline_warning_24)
                    .setPositiveButton(R.string.yes, (dialog, whichButton) -> {
                        dbHelper.deletePortfolio(portfolio.getId());
                        portfolioList.remove(portfolio);
                        notifyDataSetChanged();
                    })
                    .setNegativeButton(R.string.no, null)
                    .show();
        });
    }


//    private void setEditPortfolio(final AdapterPortfolio.ViewHolder holder, final int position)
//    {
//        FragmentPortfolio fragmentPortfolio = (FragmentPortfolio) fmContext;
//
//        Portfolio portfolio = portfolioList.get(position);
//        holder.edit_icon.setOnClickListener(v ->
//        {
//            fragmentPortfolio.addEditPortfolio(portfolio.getId());
//        });
//    }

    @Override
    public void onBindViewHolder(final AdapterPortfolio.ViewHolder holder, final int position)
    {

        Portfolio item = portfolioList.get(position);
        holder.portfolio = item;

        double amount = item.getAmount();


        Coin coin = this.coins.getCoinByMainSymbol(item.getSymbol());

        CMCCoin cmcCoin = getCMCCoin(item.getSymbol());

        Double amount2 = null;
        if(cmcCoin != null)
        {
            amount2 = amount * cmcCoin.getLAST_PRICE();
        }

        holder.CoinSymbol = coin.getSymbol();

        holder.coin_icon.setImageResource(coin.getIcon());
        holder.coin_name.setText(coin.getSymbol());
        holder.coin_amount.setText(String.valueOf(item.getAmount()));

        holder.coin_price.setText( String.format(priceStringResource, amount2));



        CurrencyListAdapterUtils.setPercentChangeImageView(holder.coin_change_status_img,
                cmcCoin.getDAILY_CHANGE_RELATIVE(), R.drawable.ic_baseline_arrow_drop_down_24,
                R.drawable.ic_baseline_arrow_drop_up_24);

        CurrencyListAdapterUtils.setPercentChangeTextView(holder.coin_daily_change_relative,
                cmcCoin.getDAILY_CHANGE_RELATIVE(),
                CurrencyListTabsActivity.DAY, negativePercentStringResource, positivePercentStringResource,
                negativeRedColor, positiveGreenColor, pctChangeNotAvailableStringResource);

        setRemovePortfolio(holder, position);
        //setEditPortfolio(holder, position);
    }

    @Override
    public AdapterPortfolio.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_portfolio_list,
                parent, false);
        return new ViewHolder(itemLayoutView, rowListener);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private final TextView coin_name, coin_amount, coin_price, coin_daily_change_relative;
        private final ImageView coin_icon, coin_change_status_img, trashButton;// edit_icon;
        private final CustomItemClickListener listener;

        private String CoinSymbol, coinName;
        private Portfolio portfolio;

        private ViewHolder(View itemLayoutView, CustomItemClickListener listener)
        {
            super(itemLayoutView);
            itemLayoutView.setOnClickListener(this);

            coin_name = itemLayoutView.findViewById(R.id.coin_name);
            coin_icon = itemLayoutView.findViewById(R.id.coin_icon);
            coin_price =  itemLayoutView.findViewById(R.id.coin_price);
            coin_daily_change_relative = itemLayoutView.findViewById(R.id.coin_daily_change_relative);

            coin_amount = itemLayoutView.findViewById(R.id.coin_amount);
            coin_change_status_img = itemLayoutView.findViewById(R.id.coin_change_status_img);
            trashButton = itemLayoutView.findViewById(R.id.coin_delete_icon);

            //edit_icon = itemLayoutView.findViewById(R.id.edit_icon);

            this.listener = listener;
        }

        @Override
        public void onClick(View v)
        {
            listener.onItemClick(getAbsoluteAdapterPosition(), v);
        }


    }

    public int getItemCount()
    {
        return portfolioList.size();
    }

    public void setPortfolioList(ArrayList<Portfolio> portfolios)
    {
        this.portfolioList = portfolios;
        notifyDataSetChanged();
    }

    public ArrayList<Portfolio> getPortfolioListList()
    {
        return portfolioList;
    }

}