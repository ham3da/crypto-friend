package com.ham3da.cryptofreind.currencylist;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ham3da.cryptofreind.App;
import com.ham3da.cryptofreind.CFUtility;
import com.ham3da.cryptofreind.DBHelper;
import com.ham3da.cryptofreind.R;
import com.ham3da.cryptofreind.alarm.Alarm;
import com.ham3da.cryptofreind.currencydetails.Coin;
import com.ham3da.cryptofreind.currencydetails.Coins;
import com.ham3da.cryptofreind.models.rest.CMCCoin;
import com.ham3da.cryptofreind.portfolio.Portfolio;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class FragmentPortfolio extends Fragment implements SwipeRefreshLayout.OnRefreshListener
{

    private View rootView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView currencyRecyclerView;
    private AdapterPortfolio adapterPortfolio;
    private ArrayList<Portfolio> portfolioArrayList = new ArrayList<>();
    private AppCompatActivity mContext;
    App app;
    Coins coins;
    DBHelper dbHelper;


    PortfolioListFragmentInterface portfolioListFragmentInterface;

    public interface PortfolioListFragmentInterface
    {
        void updatePortfolio();

        void portfolioRefreshAllCoins();
    }

    public FragmentPortfolio()
    {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static FragmentPortfolio newInstance()
    {
        return new FragmentPortfolio();
    }

    public void updatePortfolioList()
    {
        getPortfolioList();
    }

    public void loadData()
    {
        Parcelable recyclerViewState;
        recyclerViewState = Objects.requireNonNull(currencyRecyclerView.getLayoutManager()).onSaveInstanceState();
        adapterPortfolio.setPortfolioList(portfolioArrayList);
        adapterPortfolio.notifyDataSetChanged();
        currencyRecyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
        swipeRefreshLayout.setRefreshing(false);

    }

    public void getPortfolioList()
    {
        portfolioArrayList.clear();
        try
        {
            portfolioArrayList = dbHelper.getPortfolios();
            loadData();

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private void checkConn()
    {
        if (CFUtility.isNetworkConnected(mContext))
        {
            portfolioListFragmentInterface.portfolioRefreshAllCoins();
            hideItError();
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

    @Override
    public void onStart()
    {
        super.onStart();

    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);
        mContext = (AppCompatActivity) getActivity();
        app = (App) this.requireActivity().getApplicationContext();
        portfolioListFragmentInterface = (PortfolioListFragmentInterface) getActivity();
    }

    LinearLayout internet_connectivity_error;
    Button retryConnBtn;
    TextView connect_note;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.fragment_portfolio_list, container, false);
        setHasOptionsMenu(true);

        dbHelper = new DBHelper(mContext);
        portfolioArrayList = new ArrayList<>();
        coins = new Coins(mContext);

        currencyRecyclerView = rootView.findViewById(R.id.portfolio_recycler_view);
        internet_connectivity_error = rootView.findViewById(R.id.internet_connectivity_error);
        internet_connectivity_error.setVisibility(View.GONE);

        retryConnBtn = rootView.findViewById(R.id.retryConnBtn);
        connect_note = rootView.findViewById(R.id.connect_note);

        retryConnBtn.setOnClickListener(v -> {
            checkConn();
        });



        LinearLayoutManager llm = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        currencyRecyclerView.setLayoutManager(llm);


        adapterPortfolio = new AdapterPortfolio(portfolioArrayList, (AppCompatActivity) requireActivity(), this, (position, v) -> {
            addEditPortfolio(portfolioArrayList.get(position));
        });

        currencyRecyclerView.setAdapter(adapterPortfolio);
        swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(this);

        if (CFUtility.isNetworkConnected(mContext))
        {
            swipeRefreshLayout.post(() ->
            {
                swipeRefreshLayout.setRefreshing(true);
                getPortfolioList();
            });
        }
        else
        {
            showItError(null);
        }


        return rootView;
    }

    @Override
    public void onPause()
    {
        // updateTickerHandler.removeCallbacks(tickerRunnable);
        super.onPause();
        if (rootView != null)
        { // Hide keyboard when we enter this tab
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
        }

    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (rootView != null)
        {
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
        }

    }

    public void updateData()
    {
        getPortfolioList();
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater)
    {
        requireActivity().getMenuInflater().inflate(R.menu.portfolio_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.add_purchase_button)
        {
            addEditPortfolio(null);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    Spinner spinner_coins;
    AdapterCoinSpinner adapterCoinSpinner;
    Coin selectedPortfolioCoin;


    public void addEditPortfolio(Portfolio portfolio)
    {

        selectedPortfolioCoin = null;
        List<Coin> coinList = coins.getCoinList();

        AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
        builder.setCancelable(true);
        builder.setTitle(mContext.getString(R.string.add_purchase));
        builder.setIcon(R.drawable.ic_baseline_add_box_24);
        View alarm_dialog = getLayoutInflater().inflate(R.layout.portfolio_dialog, null);
        builder.setView(alarm_dialog);

        AlertDialog alertDialog = builder.create();

        Button button_add = alarm_dialog.findViewById(R.id.add_button);
        Button cancel_button = alarm_dialog.findViewById(R.id.cancel_button);

        spinner_coins = alarm_dialog.findViewById(R.id.spinner_coins);
        adapterCoinSpinner = new AdapterCoinSpinner(this.mContext, coinList);

        spinner_coins.setAdapter(adapterCoinSpinner);
        spinner_coins.setOnItemSelectedListener(new PortfolioSpinnerSelectedListener());
        EditText text_price = alarm_dialog.findViewById(R.id.text_price);

        if (portfolio != null)
        {
            String amount = portfolio.getAmount().toString();
            text_price.setText(amount);
            int index = coins.getCoinIndex(portfolio.getSymbol());
            Log.e("index", "addEditPortfolio: " + index);
            spinner_coins.setSelection(index);
        }
        else
        {
            spinner_coins.setSelection(0);
        }

        text_price.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE)
            {
                // Handle return key here
                String txt_price = text_price.getText().toString();
                savePortfolio(selectedPortfolioCoin.getSymbol(), txt_price, portfolio);
                alertDialog.dismiss();
                return true;
            }
            return false;
        });


        button_add.setOnClickListener(v -> {

            String txt_price = text_price.getText().toString();
            savePortfolio(selectedPortfolioCoin.getSymbol(), txt_price, portfolio);
            alertDialog.dismiss();

        });

        cancel_button.setOnClickListener(v -> {
            alertDialog.dismiss();
        });

        alertDialog.show();

    }


    public void savePortfolio(String symbol, String txt_price, Portfolio portfolio)
    {
        if (selectedPortfolioCoin != null)
        {

            if (txt_price.length() > 0)
            {
                if (portfolio == null)
                {
                    dbHelper.addPortFolio(selectedPortfolioCoin.getSymbol(), Double.parseDouble(txt_price));
                }
                else
                {
                    dbHelper.updatePortFolio(symbol, Double.parseDouble(txt_price), portfolio.getId());
                }
                updatePortfolioList();
            }
            else
            {
                Toast.makeText(mContext, mContext.getString(R.string.invalid_price), Toast.LENGTH_SHORT).show();
            }

            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
        }
    }


    @Override
    public void onRefresh()
    {
        swipeRefreshLayout.setRefreshing(true);
        getPortfolioList();
    }


    private class PortfolioSpinnerSelectedListener implements AdapterView.OnItemSelectedListener
    {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            selectedPortfolioCoin = (Coin) parent.getItemAtPosition(position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent)
        {
            Log.e("PortfolioSpinner", "onNothingSelected");
        }
    }

}
