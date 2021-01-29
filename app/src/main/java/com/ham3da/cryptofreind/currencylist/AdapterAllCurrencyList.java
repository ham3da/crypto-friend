package com.ham3da.cryptofreind.currencylist;

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
import com.github.ivbaranov.mfb.MaterialFavoriteButton;



import java.util.ArrayList;



public class AdapterAllCurrencyList extends RecyclerView.Adapter<AdapterAllCurrencyList.ViewHolder>
{

    private ArrayList<CMCCoin> currencyList;
    private final String priceStringResource;
    private final String pctChangeNotAvailableStringResource;
    private final String negativePercentStringResource;
    private final String positivePercentStringResource;
    private final int positiveGreenColor;
    private final int negativeRedColor;
    private final CustomItemClickListener rowListener;

    private final DatabaseHelperSingleton dbRef;
    private final FragmentAllCurrencyList.FavoritesListUpdater favsUpdateCallbackRef;

    private final Coins coins;

    public AdapterAllCurrencyList(FragmentAllCurrencyList.FavoritesListUpdater favsUpdateCallback, ArrayList<CMCCoin> currencyList,
                                  DatabaseHelperSingleton db, AppCompatActivity context, CustomItemClickListener listener)
    {
        this.currencyList = currencyList;
        this.rowListener = listener;
        this.dbRef = db;
        String mktCapStringResource = context.getString(R.string.mkt_cap_format);
        String volumeStringResource = context.getString(R.string.volume_format);
        this.negativePercentStringResource = context.getString(R.string.negative_pct_change_format);
        this.positivePercentStringResource = context.getString(R.string.positive_pct_change_format);
        this.priceStringResource = context.getString(R.string.unrounded_price_format);
        this.pctChangeNotAvailableStringResource = context.getString(R.string.not_available_pct_change_text_with_time);
        String symbolAndFullNameStringResource = context.getString(R.string.nameAndSymbol);
        this.negativeRedColor = context.getResources().getColor(R.color.percentNegativeRed);
        this.positiveGreenColor = context.getResources().getColor(R.color.percentPositiveGreen);
        this.favsUpdateCallbackRef = favsUpdateCallback;
        this.coins = new Coins(context);
    }

    public void setFavoriteButtonClickListener(final AdapterAllCurrencyList.ViewHolder holder, final int position)
    {
        holder.favButton.setOnClickListener(v ->
        {
            CoinFavoritesStructures favs = dbRef.getFavorites();
            CMCCoin item = currencyList.get(position);

            String CoinSymbol = holder.CoinSymbol;

            if (favs.favoritesMap.get(CoinSymbol) == null)
            { // Coin is not a favorite yet. Add it.
                favs.favoritesMap.put(CoinSymbol, CoinSymbol);
                favs.favoriteList.add(CoinSymbol);
                holder.favButton.setFavorite(true);
                holder.favButton.setAnimateFavorite(true);
                favsUpdateCallbackRef.addFavorite(item);
            }
            else
            { // Coin is already a favorite, remove it
                favs.favoritesMap.remove(CoinSymbol);
                favs.favoriteList.remove(CoinSymbol);
                holder.favButton.setFavorite(false);
                holder.favButton.setAnimateFavorite(false);
                favsUpdateCallbackRef.removeFavorite(item);
            }
            dbRef.saveCoinFavorites(favs);
        });
    }

    @Override
    public void onBindViewHolder(final AdapterAllCurrencyList.ViewHolder holder, final int position)
    {

        CMCCoin item = currencyList.get(position);

        Coin coin = this.coins.getCoin(item.getSymbol());


        holder.coin_icon.setImageResource(coin.getIcon());

        if (item.getLAST_PRICE() == null)
        {
            holder.coin_last_price.setText("N/A");
        }
        else
        {
            holder.coin_last_price.setText(String.format(priceStringResource, item.getLAST_PRICE()));
        }

        holder.coin_name.setText(coin.getName());
        holder.coin_symbol.setText(coin.getSymbol());

        holder.CoinSymbol = coin.getSymbol();
        holder.coinName = coin.getName();
        holder.coin = coin;

        CurrencyListAdapterUtils.setPercentChangeImageView(holder.coin_change_status_img,
                item.getDAILY_CHANGE_RELATIVE(), R.drawable.ic_baseline_arrow_drop_down_24,
                R.drawable.ic_baseline_arrow_drop_up_24);

        CurrencyListAdapterUtils.setPercentChangeTextView(holder.coin_daily_change_relative,
                item.getDAILY_CHANGE_RELATIVE(),
                CurrencyListTabsActivity.DAY, negativePercentStringResource, positivePercentStringResource,
                negativeRedColor, positiveGreenColor, pctChangeNotAvailableStringResource);


        CoinFavoritesStructures favs = this.dbRef.getFavorites();
        boolean isFav = favs.favoritesMap.get(coin.getSymbol()) != null;

        holder.favButton.setFavorite(isFav);
        holder.favButton.setAnimateFavorite(false);
        setFavoriteButtonClickListener(holder, position);
    }

    @Override
    public AdapterAllCurrencyList.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_currency_list_item, parent, false);
        return new ViewHolder(itemLayoutView, rowListener);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private final TextView coin_name;
        private final TextView coin_symbol;
        private final TextView coin_daily_change_relative;
        private final TextView coin_last_price;

        private final ImageView coin_change_status_img;
        private final ImageView coin_icon;

        private final MaterialFavoriteButton favButton;
        private final CustomItemClickListener listener;

        public String CoinSymbol, coinName;
        Coin coin;

        private ViewHolder(View itemLayoutView, CustomItemClickListener listener)
        {
            super(itemLayoutView);
            itemLayoutView.setOnClickListener(this);

            coin_name = itemLayoutView.findViewById(R.id.coin_name);
            coin_symbol = itemLayoutView.findViewById(R.id.coin_symbol);
            coin_daily_change_relative = itemLayoutView.findViewById(R.id.coin_daily_change_relative);

            coin_change_status_img = itemLayoutView.findViewById(R.id.coin_change_status_img);

            coin_icon = itemLayoutView.findViewById(R.id.coin_icon);
            favButton = itemLayoutView.findViewById(R.id.favButton);

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
        notifyDataSetChanged();
    }

    public ArrayList<CMCCoin> getCurrencyList()
    {
        return currencyList;
    }

}
