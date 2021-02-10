package com.ham3da.cryptofreind.currencylist;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

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
import android.widget.TextView;
import android.widget.Toast;

import com.ham3da.cryptofreind.App;
import com.ham3da.cryptofreind.CFUtility;
import com.ham3da.cryptofreind.PreferenceHelper;
import com.ham3da.cryptofreind.R;
import com.ham3da.cryptofreind.currencydetails.Coin;
import com.ham3da.cryptofreind.currencydetails.Coins;
import com.ham3da.cryptofreind.models.rest.CMCCoin;
import com.ham3da.cryptofreind.singletons.DatabaseHelperSingleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static com.ham3da.cryptofreind.SortUtil.sortList;
import static com.ham3da.cryptofreind.currencylist.CurrencyListTabsActivity.SORT_SETTING;

public class FragmentAllCurrencyList extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        SearchView.OnQueryTextListener
{

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView currencyRecyclerView;
    private AdapterAllCurrencyList adapter;
    private ArrayList<CMCCoin> currencyItemList;
    private final ArrayList<CMCCoin> filteredList = new ArrayList<>();
    private MenuItem searchItem;
    private SearchView searchView;
    private View rootView;
    private Context mContext;
    public static String currQuery = "";
    ArrayList<CMCCoin> searchList;
    private final HashMap<String, String> searchedSymbols = new HashMap<>();
    public static boolean searchViewFocused = false;
    private FavoritesListUpdater favsUpdateCallback;
    private PreferenceHelper sharedPreferences;
    // private DrawerController drawerController;
    private Coins coins;
    App app;
    LinearLayout internet_connectivity_error;
    Button retryConnBtn;
    TextView connect_note;

    public interface FavoritesListUpdater
    {
        void removeFavorite(CMCCoin coin);

        void addFavorite(CMCCoin coin);

        void performFavsSort();

        void updateFavCurrencyList();
    }

    public FragmentAllCurrencyList()
    {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        this.mContext = getContext();
        this.favsUpdateCallback = (FavoritesListUpdater) mContext;
        app = (App) requireActivity().getApplicationContext();

        rootView = inflater.inflate(R.layout.fragment_all_currency_list, container, false);

        this.coins = new Coins(mContext);
        setHasOptionsMenu(true);
        DatabaseHelperSingleton db = DatabaseHelperSingleton.getInstance(mContext);
        sharedPreferences = new PreferenceHelper(mContext);
        searchList = new ArrayList<>();
        // Setup currency list
        currencyRecyclerView = rootView.findViewById(R.id.currency_list_recycler_view);
        internet_connectivity_error = rootView.findViewById(R.id.internet_connectivity_error);

        connect_note = rootView.findViewById(R.id.connect_note);


        LinearLayoutManager llm = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
        currencyRecyclerView.setLayoutManager(llm);

        currencyItemList = new ArrayList<>();
        adapter = new AdapterAllCurrencyList(favsUpdateCallback, currencyItemList, db, (AppCompatActivity) mContext, (position, v) -> {
        });
        currencyRecyclerView.setAdapter(adapter);
        // Setup swipe refresh layout
        swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(this);


        retryConnBtn = rootView.findViewById(R.id.retryConnBtn);
        retryConnBtn.setOnClickListener(v -> checkConn());

        if (CFUtility.isNetworkConnected(mContext))
        {
            swipeRefreshLayout.setRefreshing(true);
            swipeRefreshLayout.post(this::updatePriceManually);
        }
        else
        {
            showItError(null);
        }

        return rootView;
    }


    public void performAllCoinsSort()
    {
        int sortType = sharedPreferences.getKey(SORT_SETTING, 1);
        sortList(adapter.getCurrencyList(), sortType, mContext);
        adapter.notifyDataSetChanged();
    }

    public void stopLoading()
    {
        if (swipeRefreshLayout != null)
        {
            swipeRefreshLayout.setRefreshing(false);
        }
    }


    public void updateAllCoinCurrencyList()
    {
        updateData();
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }


    private void loadData(boolean searching)
    {

        Parcelable recyclerViewState;
        recyclerViewState = Objects.requireNonNull(currencyRecyclerView.getLayoutManager()).onSaveInstanceState();

        if (searching)
        {
            String query = currQuery.toLowerCase();
            filteredList.clear();
            for (CMCCoin item : currencyItemList)
            {
                Coin coin = this.coins.getCoin(item.getSymbol());
                if (coin.getSymbol().toLowerCase().contains(query) || coin.getName().toLowerCase().contains(query))
                {
                    filteredList.add(item);
                }
            }
            adapter.setCurrencyList(filteredList);
        }
        else
        {
            adapter.setCurrencyList(currencyItemList);

        }


        int sortType = sharedPreferences.getKey(SORT_SETTING, 1);
        sortList(adapter.getCurrencyList(), sortType, mContext);
        adapter.notifyDataSetChanged();
        favsUpdateCallback.performFavsSort();
        currencyRecyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
        if (swipeRefreshLayout.isRefreshing())
        {
            swipeRefreshLayout.setRefreshing(false);
        }


    }

    private App getApp()
    {
        if (app == null)
        {
            app = (App) requireActivity().getApplicationContext();
        }
        return app;
    }

    String TAG = "FragmentAllCurrencyList";
    public void updateData()
    {

        app = getApp();
        ArrayList<CMCCoin> cmcCoins = app.getCmcCoins();
        if (cmcCoins == null)
        {
            return;
        }
        currencyItemList.clear();
        currencyItemList.addAll(cmcCoins);

        if (searchViewFocused)
        {
            searchedSymbols.clear();
            searchList.clear();

            for (CMCCoin coin : filteredList)
            {
                searchedSymbols.put(coin.getSymbol(), coin.getSymbol());
            }

            for (CMCCoin cmcCoin1 : cmcCoins)
            {
                if (searchedSymbols.get(cmcCoin1.getSymbol()) != null)
                {
                    searchList.add(cmcCoin1);
                }
            }
            loadData(true);
        }
        else
        {

            loadData(false);
        }


    }

    @Override
    public void onRefresh()
    {
        updatePriceManually();
    }

    public static FragmentAllCurrencyList newInstance()
    {
        return new FragmentAllCurrencyList();
    }


    public void updatePriceManually()
    {
        Log.e("TAG", "updatePriceManually: 3");
        if (!swipeRefreshLayout.isRefreshing())
        {
            swipeRefreshLayout.setRefreshing(true);
        }
        app.updatePriceManually();
    }


    @Override
    public void onResume()
    {
        super.onResume();
        Log.e("onResume", "onResume: 1");
    }


    public void checkConn()
    {
        if (CFUtility.isNetworkConnected(mContext))
        {
            hideItError();
            updatePriceManually();
        }
    }

    public void showItError(String msg)
    {
        if (internet_connectivity_error != null)
        {
            if (internet_connectivity_error.getVisibility() != View.VISIBLE)
            {
                internet_connectivity_error.setVisibility(View.VISIBLE);
                if(msg == null)
                {
                    connect_note.setText(R.string.no_internet);
                }
                else
                {
                    connect_note.setText(msg);
                }

                swipeRefreshLayout.setVisibility(View.GONE);
            }
        }
    }

    public void hideItError()
    {
        if (internet_connectivity_error != null)
        {
            if (internet_connectivity_error.getVisibility() == View.VISIBLE)
            {
                internet_connectivity_error.setVisibility(View.GONE);
                swipeRefreshLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    AlertDialog alertDialog = null;

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        if (item.getItemId() == R.id.sort_button)
        {

            int sortType = sharedPreferences.getKey(SORT_SETTING, 1);
            String[] strings = getResources().getStringArray(R.array.sort_options);

            AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext, R.style.MyAlertDialogTheme);
            builder.setCancelable(true);


            builder.setTitle(R.string.sort_by)
                    .setSingleChoiceItems(strings, sortType, (dialog, which) -> {
                        sortList(adapter.getCurrencyList(), which, mContext);
                        adapter.notifyDataSetChanged();

                        sharedPreferences.setKey(SORT_SETTING, which);

                        favsUpdateCallback.performFavsSort();

                        String txt = getString(R.string.sorting_by) + strings[which];
                        Toast toast = Toast.makeText(getContext(), txt, Toast.LENGTH_SHORT);
                        alertDialog.dismiss();
                        toast.show();

                    });

            alertDialog = builder.create();
            alertDialog.show();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query)
    {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query)
    {
        currQuery = query;
        query = query.toLowerCase();
        filteredList.clear();
        for (CMCCoin item : currencyItemList)
        {
            Coin coin = this.coins.getCoin(item.getSymbol());

            if (coin.getSymbol().toLowerCase().contains(query) || coin.getName().toLowerCase().contains(query))
            {
                filteredList.add(item);
            }
        }
        adapter.setCurrencyList(filteredList);
        return true;
    }

    private void showInputMethod(View view)
    {
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null)
        {
            imm.showSoftInput(view, 0);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
        if (searchView != null && searchViewFocused)
        {
            ((AppCompatActivity) mContext).getSupportActionBar().setTitle("");
            searchView.requestFocusFromTouch();
            searchView.setIconified(false);
            searchView.setIconified(false);
            searchView.setQuery(currQuery, false);
            showInputMethod(rootView);
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Log.e("onStart", "onStart: ");
    }

    @Override
    public void onAttachFragment(@NonNull Fragment childFragment)
    {
        super.onAttachFragment(childFragment);
        if (getActivity() == null)
        {
            Log.e("getActivity", "onAttach: null" );
            CFUtility.restartApp(getContext());
        }
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        Log.e("attach", "onAttach: 1");

        super.onAttach(context);
        app = (App) context.getApplicationContext();

    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater)
    {
        requireActivity().getMenuInflater().inflate(R.menu.all_currency_list_tab_menu, menu);
        searchItem = menu.findItem(R.id.action_search);

        searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(this);
        // Detect SearchView icon clicks
        searchView.setOnSearchClickListener(v -> {
            searchViewFocused = true;
            setItemsVisibility(menu, searchItem, false);
            //  drawerController.hideHamburger();
        });
        // Detect SearchView close
        searchView.setOnCloseListener(() -> {
            searchViewFocused = false;
            setItemsVisibility(menu, searchItem, true);
            //  drawerController.showHamburger();
            return false;
        });
        if (searchViewFocused) ((AppCompatActivity) mContext).getSupportActionBar().setTitle("");
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void setItemsVisibility(Menu menu, MenuItem exception, boolean visible)
    {
        for (int i = 0; i < menu.size(); ++i)
        {
            MenuItem item = menu.getItem(i);
            if (item != exception) item.setVisible(visible);
        }
        if (!visible)
        {
            ((AppCompatActivity) mContext).getSupportActionBar().setTitle("");
        }
        else
        {
            ((AppCompatActivity) mContext).getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        searchViewFocused = false;
    }

    public AdapterAllCurrencyList getAdapter()
    {
        return this.adapter;
    }

}
