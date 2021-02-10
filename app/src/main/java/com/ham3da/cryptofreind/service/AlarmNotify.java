package com.ham3da.cryptofreind.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.annotation.ColorInt;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.crashlytics.internal.model.CrashlyticsReport;
import com.ham3da.cryptofreind.App;
import com.ham3da.cryptofreind.DBHelper;
import com.ham3da.cryptofreind.R;
import com.ham3da.cryptofreind.alarm.Alarm;
import com.ham3da.cryptofreind.currencydetails.Coin;
import com.ham3da.cryptofreind.currencydetails.Coins;
import com.ham3da.cryptofreind.currencylist.CurrencyListTabsActivity;
import com.ham3da.cryptofreind.models.rest.CMCCoin;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class AlarmNotify
{
    String Tag = "AlarmNotify";
    Context mContext;
    Coins coins;
    App app;
    private NotificationManager notificationManager;

    public AlarmNotify(Context context)
    {
        mContext = context;
        coins = new Coins(context);
        app = (App) context.getApplicationContext();
    }

    private NotificationManager getNotificationManager()
    {
        if (notificationManager == null)
        {
            notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    private void showNotify(String title, String content, String big_content, int icon, int notify_id)
    {

        Log.e("showNotify", "showNotify: " + content);
        try
        {
            String CHANNEL_ID = "crypto_channel_alarm";

            String app_name = mContext.getString(R.string.app_name);
            String channelName = String.format(mContext.getString(R.string.alarm_notify_title), app_name);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_DEFAULT);
                getNotificationManager().createNotificationChannel(notificationChannel);
            }

            Log.e("AlarmNotify3", "showNotify: 1");
            Intent notificationIntent = new Intent(mContext, CurrencyListTabsActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);

            NotificationCompat.Builder mBuilder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                mBuilder = new NotificationCompat.Builder(mContext, CHANNEL_ID);
            }
            else
            {
                mBuilder = new NotificationCompat.Builder(mContext);
            }

            mBuilder.setSmallIcon(icon)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(big_content))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            mBuilder.setContentIntent(pendingIntent);

            getNotificationManager().notify(notify_id, mBuilder.build());

        } catch (Exception execution)
        {
            execution.printStackTrace();
        }
    }


    public void checkAlarms(ArrayList<CMCCoin> cmcCoinArrayList)
    {

        DBHelper dbHelper = new DBHelper(mContext);
        String app_name = mContext.getString(R.string.app_name);
        String title = String.format(mContext.getString(R.string.alarm_notify_title), app_name);

        String alarm_notify_short = mContext.getString(R.string.alarm_notify_short);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        String now_date = dateFormat.format(Calendar.getInstance().getTime());


        boolean needAlarmListUpdate = false;
        for (CMCCoin cmcCoin : cmcCoinArrayList)
        {
            Coin coin = coins.getCoin(cmcCoin.getSymbol());

            String alarm_coin = String.format(mContext.getString(R.string.alarm_coin), coin.getName());
            String content = String.format(alarm_notify_short, coin.getSymbol(), cmcCoin.getLAST_PRICE().toString());


            ArrayList<Alarm> alarmArrayList = dbHelper.getAlarms(1, coin.getSymbol());
            for (Alarm alarm : alarmArrayList)
            {
                StringBuilder big_content = new StringBuilder();
                String your_desired_price = String.format(mContext.getString(R.string.your_desired_price), alarm.getPrice().toString());
                String alarm_price = String.format(mContext.getString(R.string.alarm_price), cmcCoin.getLAST_PRICE().toString());

                boolean notified = false;
                int chanel_id;

                //Log.e("getAlarm_type", "getAlarm_type: "+alarm.getAlarm_type() );

                if (alarm.getAlarm_type() == DBHelper.ALARM_TYPE_MORE_THAN && cmcCoin.getLAST_PRICE() >= alarm.getPrice())
                {
                    //Log.e("AlarmNotify2", "showNotify: _MORE");
                    chanel_id = alarm.getId();
                    big_content.append(alarm_coin).append(System.lineSeparator());
                    big_content.append(your_desired_price).append(System.lineSeparator());

                    big_content.append(alarm_price).append(System.lineSeparator());
                    String alarm_type = mContext.getString(R.string.alarm_above);
                    big_content.append(String.format(mContext.getString(R.string.alarm_type), alarm_type)).append(System.lineSeparator());

                    big_content.append(String.format(mContext.getString(R.string.recorded_s), now_date));

                    showNotify(title, content, big_content.toString(), coin.getIcon(), chanel_id);
                    //do any
                    notified = true;

                }
                if (alarm.getAlarm_type() == DBHelper.ALARM_TYPE_LESS_THAN && cmcCoin.getLAST_PRICE() <= alarm.getPrice())
                {
                    //Log.e("AlarmNotify2", "showNotify: _LESS");
                    chanel_id = alarm.getId();
                    big_content.append(alarm_coin).append(System.lineSeparator());
                    big_content.append(your_desired_price).append(System.lineSeparator());
                    big_content.append(alarm_price).append(System.lineSeparator());
                    String alarm_type = mContext.getString(R.string.alarm_below);
                    big_content.append(String.format(mContext.getString(R.string.alarm_type), alarm_type)).append(System.lineSeparator());
                    big_content.append(String.format(mContext.getString(R.string.recorded_s), now_date));

                    showNotify(title, content, big_content.toString(), coin.getIcon(), chanel_id);
                    //do any
                    notified = true;
                }

                if (notified)//once : update status to inactive
                {
                    needAlarmListUpdate = true;
                    if (alarm.getAlarm_repeat() == DBHelper.ALARM_REPEAT_ONCE)
                    {
                        dbHelper.updateAlarmDateStatus(now_date, 0, alarm.getId());
                    }
                    else
                    {
                        dbHelper.updateAlarmRDate(now_date, alarm.getId());
                    }

                }
            }

        }

        if (needAlarmListUpdate)
        {
           app.updateAlarmList();
        }


    }

}
