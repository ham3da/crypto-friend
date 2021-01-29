package com.ham3da.cryptofreind.currencylist;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import com.ham3da.cryptofreind.App;
import com.ham3da.cryptofreind.CFUtility;
import com.ham3da.cryptofreind.CustomItemClickListener;
import com.ham3da.cryptofreind.PreferenceHelper;
import com.ham3da.cryptofreind.R;

import com.ham3da.cryptofreind.currencydetails.Coin;
import com.ham3da.cryptofreind.currencydetails.Coins;
import com.ham3da.cryptofreind.models.rest.CMCCoin;
import com.ham3da.cryptofreind.models.rest.CoinFavoritesStructures;
import com.ham3da.cryptofreind.singletons.DatabaseHelperSingleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import java.util.Objects;

import static com.ham3da.cryptofreind.SortUtil.sortList;
import static com.ham3da.cryptofreind.currencylist.CurrencyListTabsActivity.SORT_SETTING;


public class FragmentFavoriteCurrencyList extends Fragment implements SwipeRefreshLayout.OnRefreshListener
{

    private View rootView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private DatabaseHelperSingleton db;
    private RecyclerView currencyRecyclerView;
    private AdapterFavsCurrencyList adapter;
    private ArrayList<CMCCoin> currencyItemFavsList = new ArrayList<>();
    private AllCoinsListUpdater allCoinsListUpdater;
    private AppCompatActivity mContext;
    private HashMap<String, String> slugToIDMap = new HashMap<>();

    private PreferenceHelper sharedPreferences;

    private Coins coins;
    App app;
    Handler updateTickerHandler = new Handler();
    Runnable tickerRunnable;

    LinearLayout internet_connectivity_error;

    public interface AllCoinsListUpdater
    {
        void allCoinsModifyFavorites(CMCCoin coin);

        void performAllCoinsSort();

        void updateAllCoinCurrencyList();

        void refreshAllCoins();
    }



    public FragmentFavoriteCurrencyList()
    {
    }

    public void stopLoading()
    {
        if(swipeRefreshLayout != null)
        {
            swipeRefreshLayout.setRefreshing(false);
        }
    }


    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static FragmentFavoriteCurrencyList newInstance()
    {
        return new FragmentFavoriteCurrencyList();
    }

    public void performFavsSort()
    {
        int sortType = sharedPreferences.getKey(SORT_SETTING, 1);
        sortList(adapter.getCurrencyList(), sortType, mContext);
        adapter.notifyDataSetChanged();
    }

    private void checkConn()
    {
        if (CFUtility.isNetworkConnected(mContext))
        {
            allCoinsListUpdater.refreshAllCoins();
            hideItError();
        }

    }


    public void showItError()
    {
        if(internet_connectivity_error != null)
        {
            if (internet_connectivity_error.getVisibility() != View.VISIBLE)
            {
                internet_connectivity_error.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setVisibility(View.GONE);
            }
        }
    }

    public void hideItError()
    {
        if(internet_connectivity_error != null)
        {
            if (internet_connectivity_error.getVisibility() == View.VISIBLE)
            {
                internet_connectivity_error.setVisibility(View.GONE);
                swipeRefreshLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    public void getCurrencyList()
    {
        currencyItemFavsList.clear();
        db = DatabaseHelperSingleton.getInstance(mContext);
        CoinFavoritesStructures favorites = db.getFavorites();
        try
        {
            ArrayList<CMCCoin> cmcCoinList = app.getCmcCoins();
            for (CMCCoin item : cmcCoinList)
            {
                Coin coin = this.coins.getCoin(item.getSymbol());

                if (favorites.favoritesMap.get(coin.getSymbol()) != null)
                {
                    currencyItemFavsList.add(item);
                }
            }
            getQuickSearch();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }


    public void getQuickSearch()
    {

        slugToIDMap = new HashMap<>();
        Parcelable recyclerViewState;
        recyclerViewState = Objects.requireNonNull(currencyRecyclerView.getLayoutManager()).onSaveInstanceState();

        adapter.setCurrencyList(currencyItemFavsList);
        int sortType = sharedPreferences.getKey(SORT_SETTING, 1);
        sortList(adapter.getCurrencyList(), sortType, mContext);
        adapter.notifyDataSetChanged();
        currencyRecyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
        if(swipeRefreshLayout.isRefreshing())
        {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);
        allCoinsListUpdater = (AllCoinsListUpdater) getActivity();
        mContext = (AppCompatActivity) getActivity();
        app = (App) context.getApplicationContext();
    }


    @Override
    public void onStart()
    {
        super.onStart();

    }

    Button retryConnBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        rootView = inflater.inflate(R.layout.fragment_favorite_currency_list, container, false);
        setHasOptionsMenu(true);
        coins = new Coins(mContext);
        this.db =  DatabaseHelperSingleton.getInstance(mContext);

        sharedPreferences = new PreferenceHelper(mContext);

        currencyRecyclerView = rootView.findViewById(R.id.currency_favs_recycler_view);
        internet_connectivity_error = rootView.findViewById(R.id.internet_connectivity_error);

        retryConnBtn = rootView.findViewById(R.id.retryConnBtn);
        retryConnBtn.setOnClickListener(v -> {
            checkConn();
        });


        LinearLayoutManager llm = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        currencyRecyclerView.setLayoutManager(llm);

        currencyRecyclerView.setLayoutManager(llm);
        currencyItemFavsList = new ArrayList<>();
        adapter = new AdapterFavsCurrencyList(allCoinsListUpdater, currencyItemFavsList, db, (AppCompatActivity) getActivity(), new CustomItemClickListener()
        {
            @Override
            public void onItemClick(int position, View v)
            {

            }
        });
        currencyRecyclerView.setAdapter(adapter);
        swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(this);


        if (CFUtility.isNetworkConnected(mContext))
        {
            swipeRefreshLayout.setRefreshing(true);
            swipeRefreshLayout.post(this::getCurrencyList);
        }
        else
        {
            showItError();
        }
        mContext.getSupportActionBar().setTitle(getResources().getString(R.string.app_name));

        return rootView;
    }

    @Override
    public void onPause()
    {
        updateTickerHandler.removeCallbacks(tickerRunnable);
        super.onPause();
    }

    @Override
    public void onResume()
    {
        Log.e("onResume", "onResume: 2" );

//        updateTickerHandler.postDelayed(tickerRunnable = () -> {
//            updateTickerHandler.postDelayed(tickerRunnable, tickerDelay);
//            updateData();
//        }, tickerDelay);

        super.onResume();
        if (rootView != null)
        { // Hide keyboard when we enter this tab
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
        }


    }

    public void updateData()
    {
        getCurrencyList();
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater)
    {
        requireActivity().getMenuInflater().inflate(R.menu.favorite_currency_list_tab_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.sort_favs_button)
        {
            int sortType = sharedPreferences.getKey(SORT_SETTING, 1);
            new MaterialDialog.Builder(requireContext())
                    .title(R.string.sort_by)
                    .items(R.array.sort_options)
                    .buttonRippleColor(getContext().getResources().getColor(R.color.colorPrimary))
                    .itemsCallbackSingleChoice(sortType, (dialog, view, which, text) -> {
                        sortList(adapter.getCurrencyList(), which, mContext);
                        adapter.notifyDataSetChanged();

                        sharedPreferences.setKey(SORT_SETTING, which);
                        allCoinsListUpdater.performAllCoinsSort();

                        Toast toast = Toast.makeText(getContext(), String.format(getString(R.string.sorting_by2), text), Toast.LENGTH_SHORT);
                        toast.show();
                        return true;
                    })
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh()
    {
        swipeRefreshLayout.setRefreshing(true);
        getCurrencyList();
    }

    public void removeFavorite(CMCCoin coin)
    {
        ArrayList<CMCCoin> currentFavs = adapter.getCurrencyList();
        Iterator<CMCCoin> currFavsIterator = currentFavs.iterator();
        while (currFavsIterator.hasNext())
        {
            CMCCoin currCoin = currFavsIterator.next();
            if (currCoin.getId().equals(coin.getId()))
            {
                currFavsIterator.remove();
                adapter.notifyDataSetChanged();
                return;
            }
        }
    }

    public void addFavorite(CMCCoin coin)
    {
        currencyItemFavsList.add(0, coin);
        adapter.notifyDataSetChanged();
    }


    public void updateFavCurrencyList()
    {
        getCurrencyList();
    }


}
