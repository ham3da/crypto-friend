package com.ham3da.cryptofreind.currencylist;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.ham3da.cryptofreind.CustomItemClickListener;
import com.ham3da.cryptofreind.R;
import com.ham3da.cryptofreind.currencydetails.Coin;
import com.ham3da.cryptofreind.currencydetails.Coins;
import com.ham3da.cryptofreind.models.rest.CMCCoin;
import com.ham3da.cryptofreind.models.rest.CoinFavoritesStructures;
import com.ham3da.cryptofreind.singletons.DatabaseHelperSingleton;
import java.util.ArrayList;


public class AdapterFavsCurrencyList extends RecyclerView.Adapter<AdapterFavsCurrencyList.ViewHolder>
{
    private ArrayList<CMCCoin> currencyList;
    private final String pctChangeNotAvailableStringResource;
    private final String negativePercentStringResource;
    private final String positivePercentStringResource;
    private final String priceStringResource;
    private final String msgRemoveFav;

    private final int positiveGreenColor;
    private final int negativeRedColor;
    private final CustomItemClickListener rowListener;
    private final AppCompatActivity contextRef;
    private final DatabaseHelperSingleton dbRef;
    private final FragmentFavoriteCurrencyList.AllCoinsListUpdater favsUpdateCallbackRef;
    private final Coins coins;

    public AdapterFavsCurrencyList(FragmentFavoriteCurrencyList.AllCoinsListUpdater favsUpdateCallback, ArrayList<CMCCoin> currencyList,
                                   DatabaseHelperSingleton db, AppCompatActivity context, CustomItemClickListener listener)
    {

        this.currencyList = currencyList;
        this.contextRef =  context;
        this.rowListener = listener;
        this.dbRef = db;
        String mktCapStringResource = this.contextRef.getString(R.string.mkt_cap_format);
        String volumeStringResource = this.contextRef.getString(R.string.volume_format);
        this.negativePercentStringResource = this.contextRef.getString(R.string.negative_pct_change_format);
        this.positivePercentStringResource = this.contextRef.getString(R.string.positive_pct_change_format);
        this.priceStringResource = this.contextRef.getString(R.string.unrounded_price_format);
        this.pctChangeNotAvailableStringResource = this.contextRef.getString(R.string.not_available_pct_change_text_with_time);
        String symbolAndFullNameStringResource = this.contextRef.getString(R.string.nameAndSymbol);
        this.msgRemoveFav =  this.contextRef.getString(R.string.remove_fav);

        this.negativeRedColor = this.contextRef.getResources().getColor(R.color.percentNegativeRed);
        this.positiveGreenColor = this.contextRef.getResources().getColor(R.color.percentPositiveGreen);
        this.favsUpdateCallbackRef = favsUpdateCallback;
        AdapterFavsCurrencyList me = this;

        this.coins = new Coins(context);


    }

    public void setFavoriteButtonClickListener(final AdapterFavsCurrencyList.ViewHolder holder, final int position)
    {
        holder.trashButton.setOnClickListener(new View.OnClickListener()
        {
            final CMCCoin item = currencyList.get(position);
            final String CoinSymbol =  holder.CoinSymbol;
            @Override
            public void onClick(View v)
            {
                //remove_fav
                String msg = String.format(msgRemoveFav, holder.coinName);
                new AlertDialog.Builder(contextRef)
                        .setTitle(R.string.Unfavorite)
                        .setMessage(msg)
                        .setIcon(R.drawable.ic_baseline_warning_24)
                        .setPositiveButton(R.string.yes, (dialog, whichButton) -> {
                            CoinFavoritesStructures favs = dbRef.getFavorites();
                            favs.favoritesMap.remove(CoinSymbol);
                            favs.favoriteList.remove(CoinSymbol);
                            dbRef.saveCoinFavorites(favs);
                            currencyList.remove(position);
                            notifyDataSetChanged();
                            favsUpdateCallbackRef.allCoinsModifyFavorites(item);
                        })
                        .setNegativeButton(R.string.no, null).show();
            }
        });
    }

    @Override
    public void onBindViewHolder(final AdapterFavsCurrencyList.ViewHolder holder, final int position)
    {
        CMCCoin item = currencyList.get(position);
        Coin coin = this.coins.getCoin(item.getSymbol());
        holder.coin_icon.setImageResource(coin.getIcon());

        if (item.getLAST_PRICE().doubleValue() == 0) {
            holder.coin_last_price.setText("N/A");
        } else {
            holder.coin_last_price.setText(String.format(priceStringResource, item.getLAST_PRICE()));
        }

        holder.coin_name.setText(coin.getName());
        holder.coin_symbol.setText(coin.getSymbol());

        holder.CoinSymbol = coin.getSymbol();
        holder.coinName = coin.getName();
        holder.coin = coin;
        CurrencyListAdapterUtils.setPercentChangeImageView( holder.coin_change_status_img,
                item.getDAILY_CHANGE_RELATIVE(), R.drawable.ic_baseline_arrow_drop_down_24,
                R.drawable.ic_baseline_arrow_drop_up_24);

        CurrencyListAdapterUtils.setPercentChangeTextView(holder.coin_daily_change_relative,
                item.getDAILY_CHANGE_RELATIVE(),
                CurrencyListTabsActivity.DAY, negativePercentStringResource, positivePercentStringResource,
                negativeRedColor, positiveGreenColor, pctChangeNotAvailableStringResource);

        setFavoriteButtonClickListener(holder, position);
    }

    @Override
    public AdapterFavsCurrencyList.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_favs_currency_list,
                parent, false);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView, rowListener);
        return viewHolder;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private final TextView coin_name;
        private final TextView coin_symbol;
        private final TextView coin_daily_change_relative;
        private final TextView coin_last_price;

        private final ImageView coin_change_status_img;
        private final ImageView coin_icon;


        private final CustomItemClickListener listener;

        public String CoinSymbol, coinName;

        Coin coin;

        protected ImageView trashButton;

        private ViewHolder(View itemLayoutView, CustomItemClickListener listener)
        {
            super(itemLayoutView);
            itemLayoutView.setOnClickListener(this);


            coin_name = itemLayoutView.findViewById(R.id.coin_name);
            coin_symbol = itemLayoutView.findViewById(R.id.coin_symbol);
            coin_daily_change_relative = itemLayoutView.findViewById(R.id.coin_daily_change_relative);

            coin_change_status_img = itemLayoutView.findViewById(R.id.coin_change_status_img);

            coin_icon = itemLayoutView.findViewById(R.id.coin_icon);
            trashButton = itemLayoutView.findViewById(R.id.coin_delete_icon);

            coin_last_price = itemLayoutView.findViewById(R.id.coin_last_price);



            this.listener = listener;
        }

        @Override
        public void onClick(View v)
        {
            listener.onItemClick(getAdapterPosition(), v);
        }
    }

    public int getItemCount()
    {
        return currencyList.size();
    }

    public void setCurrencyList(ArrayList<CMCCoin> newCurrencyList)
    {
        this.currencyList = newCurrencyList;
    }

    public ArrayList<CMCCoin> getCurrencyList()
    {
        return currencyList;
    }
}
