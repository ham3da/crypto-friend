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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ham3da.cryptofreind.App;
import com.ham3da.cryptofreind.DBHelper;
import com.ham3da.cryptofreind.R;
import com.ham3da.cryptofreind.alarm.Alarm;
import com.ham3da.cryptofreind.currencydetails.Coin;
import com.ham3da.cryptofreind.currencydetails.Coins;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class FragmentAlarmList extends Fragment implements SwipeRefreshLayout.OnRefreshListener
{

    private View rootView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView currencyRecyclerView;
    private AdapterAlarms adapterAlarms;
    private ArrayList<Alarm> alarmArrayList = new ArrayList<>();
    private AppCompatActivity mContext;
    App app;
    Coins coins;
    DBHelper dbHelper;

    AlarmListFragmentInterface alarmListFragmentInterface;

    public interface AlarmListFragmentInterface
    {
        void updateAlarmList();
    }

    public FragmentAlarmList()
    {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static FragmentAlarmList newInstance()
    {
        return new FragmentAlarmList();
    }

    public void updateAlarmList()
    {
        getAlarmList();
    }

    public void loadData()
    {

        Parcelable recyclerViewState;
        recyclerViewState = Objects.requireNonNull(currencyRecyclerView.getLayoutManager()).onSaveInstanceState();
        adapterAlarms.setAlarmList(alarmArrayList);
        adapterAlarms.notifyDataSetChanged();
        currencyRecyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
        if (swipeRefreshLayout.isRefreshing())
        {
            swipeRefreshLayout.setRefreshing(false);
        }

    }

    public void getAlarmList()
    {

        alarmArrayList.clear();

        try
        {
            alarmArrayList = dbHelper.getGetAlarms();
            loadData();

        } catch (Exception e)
        {
            e.printStackTrace();
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
        alarmListFragmentInterface = (AlarmListFragmentInterface) getActivity();

    }

    LinearLayout internet_connectivity_error;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.fragment_favorite_currency_list, container, false);
        setHasOptionsMenu(true);

        dbHelper = new DBHelper(mContext);
        alarmArrayList = new ArrayList<>();
        coins = new Coins(mContext);

        currencyRecyclerView = rootView.findViewById(R.id.currency_favs_recycler_view);
        internet_connectivity_error = rootView.findViewById(R.id.internet_connectivity_error);
        internet_connectivity_error.setVisibility(View.GONE);

//        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
//        llm.setOrientation(LinearLayoutManager.VERTICAL);
//        currencyRecyclerView.setLayoutManager(llm);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        currencyRecyclerView.setLayoutManager(llm);


        adapterAlarms = new AdapterAlarms(alarmArrayList, (AppCompatActivity) requireActivity(), (position, v) -> {

        });

        currencyRecyclerView.setAdapter(adapterAlarms);
        swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(() ->
        {
            swipeRefreshLayout.setRefreshing(true);
            getAlarmList();
        });


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
        getAlarmList();
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater)
    {
        requireActivity().getMenuInflater().inflate(R.menu.alarm_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.add_alarm_button)
        {
            addAlarm();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    Spinner spinner_coins;
    AdapterCoinSpinner adapterCoinSpinner;
    Coin selectedAlarmCoin;

    private void addAlarm()
    {

        selectedAlarmCoin = null;
        List<Coin> coinList = coins.getCoinList();

        AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
        builder.setCancelable(true);
        builder.setTitle(mContext.getString(R.string.add_alert));
        builder.setIcon(R.drawable.ic_baseline_add_alert_24);
        View alarm_dialog = getLayoutInflater().inflate(R.layout.alarm_dialog, null);
        builder.setView(alarm_dialog);

        AlertDialog alertDialog = builder.create();

        Button button_add = alarm_dialog.findViewById(R.id.add_button);
        Button cancel_button = alarm_dialog.findViewById(R.id.cancel_button);


        RadioButton radio_more_than = alarm_dialog.findViewById(R.id.radio_more_than);
        RadioButton radio_less_than = alarm_dialog.findViewById(R.id.radio_less_than);

        RadioButton radio_once = alarm_dialog.findViewById(R.id.radio_once);
        RadioButton radio_always = alarm_dialog.findViewById(R.id.radio_always);

        spinner_coins = alarm_dialog.findViewById(R.id.spinner_coins);
        adapterCoinSpinner = new AdapterCoinSpinner(this.mContext, coinList);

        spinner_coins.setAdapter(adapterCoinSpinner);
        spinner_coins.setOnItemSelectedListener(new AlarmSpinnerSelectedListener());


        EditText text_price = alarm_dialog.findViewById(R.id.text_price);
        text_price.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE)
            {
                // Handle return key here
                if (selectedAlarmCoin != null)
                {
                    int type_alarm = radio_more_than.isChecked() ? DBHelper.ALARM_TYPE_MORE_THAN : DBHelper.ALARM_TYPE_LESS_THAN;
                    int type_repeat = radio_once.isChecked() ? DBHelper.ALARM_REPEAT_ONCE : DBHelper.ALARM_REPEAT_ALWAYS;

                    String txt_price = text_price.getText().toString();
                    if (txt_price.length() > 0)
                    {
                        dbHelper.addAlarm(selectedAlarmCoin.getSymbol(), Double.parseDouble(txt_price), type_alarm, type_repeat);
                        updateAlarmList();
                    }
                    else
                    {
                        Toast.makeText(mContext, mContext.getString(R.string.invalid_price), Toast.LENGTH_SHORT).show();
                    }
                    alertDialog.dismiss();
                    InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
                }
                return true;
            }
            return false;
        });


        button_add.setOnClickListener(v -> {

            if (selectedAlarmCoin != null)
            {
                int type_alarm = radio_more_than.isChecked() ? DBHelper.ALARM_TYPE_MORE_THAN : DBHelper.ALARM_TYPE_LESS_THAN;
                int type_repeat = radio_once.isChecked() ? DBHelper.ALARM_REPEAT_ONCE : DBHelper.ALARM_REPEAT_ALWAYS;

                String txt_price = text_price.getText().toString();
                if (txt_price.length() > 0)
                {
                    dbHelper.addAlarm(
                            selectedAlarmCoin.getSymbol(),
                            Double.parseDouble(txt_price),
                            type_alarm,
                            type_repeat);
                    updateAlarmList();
                }
                else
                {
                    Toast.makeText(mContext, mContext.getString(R.string.invalid_price), Toast.LENGTH_SHORT).show();
                }

                alertDialog.dismiss();
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
            }
        });

        cancel_button.setOnClickListener(v -> {
            alertDialog.dismiss();
        });

        alertDialog.show();
        spinner_coins.setSelection(0);
    }


    @Override
    public void onRefresh()
    {
        swipeRefreshLayout.setRefreshing(true);
        getAlarmList();
    }


    private class AlarmSpinnerSelectedListener implements android.widget.AdapterView.OnItemSelectedListener
    {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            selectedAlarmCoin = (Coin) parent.getItemAtPosition(position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent)
        {
            Log.e("country_Spinner", "onNothingSelected");
        }
    }

}
