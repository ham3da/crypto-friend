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
import androidx.recyclerview.widget.RecyclerView;

import com.ham3da.cryptofreind.CustomItemClickListener;
import com.ham3da.cryptofreind.DBHelper;
import com.ham3da.cryptofreind.R;
import com.ham3da.cryptofreind.alarm.Alarm;
import com.ham3da.cryptofreind.currencydetails.Coin;
import com.ham3da.cryptofreind.currencydetails.Coins;

import java.util.ArrayList;
import java.util.Locale;


public class AdapterAlarms extends RecyclerView.Adapter<AdapterAlarms.ViewHolder>
{

    private ArrayList<Alarm> alarmList;
    private final String recordedStringResource;
    private final String alarm_price_format, alarm_repeat_format , r_once, r_always;
    private final CustomItemClickListener rowListener;
    DBHelper dbHelper;


    private final Coins coins;

    private final Context mContext;

    public AdapterAlarms(ArrayList<Alarm> alarmList, AppCompatActivity context, CustomItemClickListener listener)
    {
        this.alarmList = alarmList;
        this.rowListener = listener;

        String priceStringResource = context.getString(R.string.unrounded_price_format);
        this.recordedStringResource = context.getString(R.string.recorded_s);
        int negativeRedColor = context.getResources().getColor(R.color.percentNegativeRed);
        int positiveGreenColor = context.getResources().getColor(R.color.percentPositiveGreen);
        this.alarm_price_format = context.getString(R.string.price_format_alarm);

        this.alarm_repeat_format = context.getResources().getString(R.string.Repeat);
        r_once = context.getResources().getString(R.string.once);
        r_always = context.getResources().getString(R.string.always);
        this.coins = new Coins(context);
        dbHelper = new DBHelper(context);
        mContext = context;
    }

    public void setRemoveAlarm(final AdapterAlarms.ViewHolder holder, final int position)
    {
        Alarm alarm = alarmList.get(position);
        holder.coin_delete_icon.setOnClickListener(v ->
        {
            String msg = mContext.getString(R.string.delete_q);

            new AlertDialog.Builder(mContext)
                    .setTitle(R.string.remove_alarm)
                    .setMessage(msg)
                    .setIcon(R.drawable.ic_baseline_warning_24)
                    .setPositiveButton(R.string.yes, (dialog, whichButton) -> {
                        dbHelper.deleteAlarm(alarm.getId());
                        alarmList.remove(alarm);
                        notifyDataSetChanged();
                    })
                    .setNegativeButton(R.string.no, null)
                    .show();
        });
    }

    @Override
    public void onBindViewHolder(final AdapterAlarms.ViewHolder holder, final int position)
    {

        Alarm item = alarmList.get(position);
        holder.alarm = item;
        Coin coin = this.coins.getCoinByMainSymbol(item.getSymbol());
        holder.coin_icon.setImageResource(coin.getIcon());
        String type_alarm =  mContext.getString(R.string.more_than2);

        String alarm_type = mContext.getString(R.string.alarm_above);
        if(item.getAlarm_type()  == DBHelper.ALARM_TYPE_LESS_THAN)
        {
            type_alarm = mContext.getString(R.string.less_than2);
        }

        if (item.getPrice() == null)
        {
            holder.coin_price.setText("N/A");
        }
        else
        {
            holder.coin_price.setText(String.format(Locale.ENGLISH, alarm_price_format, type_alarm, item.getPrice()));
        }


        String cname = coin.getName() + "(" + coin.getSymbol() + ")";

        holder.coin_name.setText(cname);

        holder.CoinSymbol = coin.getSymbol();
        holder.coinName = coin.getName();

        ColorStateList colorStateList;
        Log.e("getStatus", "onBindViewHolder: "+item.getAlarm_type() );
        if (item.getStatus() == 1)
        {
            colorStateList = ContextCompat.getColorStateList(mContext, R.color.darkGreen);
        }
        else
        {
            colorStateList = ContextCompat.getColorStateList(mContext, R.color.gray);
        }

        holder.coin_status.setBackgroundTintList(colorStateList);

        if (item.getRecorded_date() != null && !item.getRecorded_date().isEmpty())
        {
            String rdate = String.format(this.recordedStringResource, item.getRecorded_date());
            holder.record_date.setText(rdate);
        }
        else
        {
            holder.record_date.setText(R.string.Waiting);
        }

        if( DBHelper.ALARM_REPEAT_ONCE == item.getAlarm_repeat())
        {
            holder.alarm_type.setText(String.format(alarm_repeat_format, r_once));
        }
        else
        {
            holder.alarm_type.setText(String.format(alarm_repeat_format, r_always));
        }

        setRemoveAlarm(holder, position);
    }

    @Override
    public AdapterAlarms.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_alarm,
                parent, false);
        return new ViewHolder(itemLayoutView, rowListener);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private final TextView coin_name, alarm_type;
        private final TextView coin_price, record_date;
        private final ImageView coin_icon, coin_status, coin_delete_icon;
        private final CustomItemClickListener listener;
        private String CoinSymbol, coinName;
        private Alarm alarm;

        private ViewHolder(View itemLayoutView, CustomItemClickListener listener)
        {
            super(itemLayoutView);
            itemLayoutView.setOnClickListener(this);

            coin_name = itemLayoutView.findViewById(R.id.coin_name);
            coin_icon = itemLayoutView.findViewById(R.id.coin_icon);
            coin_price = itemLayoutView.findViewById(R.id.coin_price);
            coin_status = itemLayoutView.findViewById(R.id.coin_status);
            record_date = itemLayoutView.findViewById(R.id.record_date);
            coin_delete_icon = itemLayoutView.findViewById(R.id.coin_delete_icon);
            alarm_type = itemLayoutView.findViewById(R.id.alarm_type);
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
        return alarmList.size();
    }

    public void setAlarmList(ArrayList<Alarm> alarmList1)
    {
        this.alarmList = alarmList1;
        notifyDataSetChanged();
    }

    public ArrayList<Alarm> getAlarmListList()
    {
        return alarmList;
    }

}