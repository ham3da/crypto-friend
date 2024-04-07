package com.ham3da.cryptofreind.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.ham3da.cryptofreind.App;
import com.ham3da.cryptofreind.AppSettings;
import com.ham3da.cryptofreind.R;
import com.ham3da.cryptofreind.currencylist.CurrencyListTabsActivity;
import com.ham3da.cryptofreind.rest.ReadBitfinexTicker;

import java.util.Timer;
import java.util.TimerTask;

public class TickerService extends Service
{

    Timer timer = new Timer();
    ReadBitfinexTicker readBitfinexTicker;
    int CHANGE_PERIOD = 10; //In Second
    private static final String CHANNEL_ID = "crypto_channel_foreground";
    private static final int NOTIFICATION_ID = 19861986;
    private NotificationManager notificationManager;
    AppSettings appSettings;
    String TAG = "TickerService";
    int firstStart = 0;
    IntentFilter theFilter;

    BroadcastReceiver broadcastReceiverPM;

    // Binder given to clients
    private final IBinder binder = new LocalBinder();

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder
    {
        TickerService getService()
        {
            // Return this instance of LocalService so clients can call public methods
            return TickerService.this;
        }
    }

    public TickerService()
    {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.i(TAG, "onStartCommand");

        appSettings = new AppSettings(getApplicationContext());
        CHANGE_PERIOD = appSettings.getUpdateTime();
        int period = 1000 * CHANGE_PERIOD;

        timer.schedule(new TimerTask()
                       {
                           @Override
                           public void run()
                           {
                               readBitfinexTicker.updateCurrencyList(true);
                               Log.i(TAG, "schedule: 1");
                           }
                       },0, period);

        return START_STICKY_COMPATIBILITY;
    }

    @Override
    public void onCreate()
    {

        Log.i(TAG, "onCreate");
        super.onCreate();
        if(readBitfinexTicker == null)
        {
            readBitfinexTicker = new ReadBitfinexTicker(getBaseContext().getApplicationContext());
        }

        theFilter = new IntentFilter();
        theFilter.addAction("com.ham3da.cryptofreind.broadcast.UPDATE_PRICE_MANUALLY");
        theFilter.addAction("com.ham3da.cryptofreind.broadcast.RESET_TIMER");

        this.broadcastReceiverPM = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                if ("com.ham3da.cryptofreind.broadcast.UPDATE_PRICE_MANUALLY".equals(intent.getAction()))
                {
                    readBitfinexTicker.updateCurrencyList(false);
                }
                else if("com.ham3da.cryptofreind.broadcast.RESET_TIMER".equals(intent.getAction()))
                {
                    appSettings = new AppSettings(getApplicationContext());
                    CHANGE_PERIOD = appSettings.getUpdateTime();
                    Log.e(TAG, "CHANGE_PERIOD: "+CHANGE_PERIOD );


                    timer.cancel();
                    int period = 1000 * CHANGE_PERIOD;
                    timer = new Timer();
                    timer.schedule(new TimerTask()
                    {
                        @Override
                        public void run()
                        {
                            readBitfinexTicker.updateCurrencyList(true);
                            Log.i(TAG, "schedule: 2");
                        }
                    },0, period);
                }
            }
        };


        App app = (App) getApplicationContext();
        app.setTickerServiceRunning(true);
        fireMessage();

        this.registerReceiver(this.broadcastReceiverPM, theFilter);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        timer.cancel();
        this.unregisterReceiver(this.broadcastReceiverPM);

        App app = (App) getApplicationContext();
        app.setTickerServiceRunning(false);
        Log.e(TAG, "onDestroy: TickerService");
    }

    @Override
    public boolean stopService(Intent name)
    {
        return super.stopService(name);
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO: Return the communication channel to the service.
        return binder;

    }

    private NotificationManager getNotificationManager()
    {
        if (notificationManager == null)
        {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    public void fireMessage()
    {
        Intent intent = new Intent(getApplicationContext(), CurrencyListTabsActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);


        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);


        CharSequence channelName = this.getString(R.string.app_name);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_DEFAULT);

            notificationChannel.setSound(null, null);
            getNotificationManager().createNotificationChannel(notificationChannel);
        }

        String content = getString(R.string.app_foreground_des);
        String content2 = String.format(content, getString(R.string.app_name));
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(channelName)
                .setContentText(content2)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        Notification notification = builder.build();

        if (this.firstStart == 0)
        {
            this.firstStart = 1;
            startForeground(NOTIFICATION_ID, notification);
            Log.i(TAG, "fireMessage: startForeground");
        }
        else
        {
            getNotificationManager().notify(NOTIFICATION_ID, builder.build());
            Log.i(TAG, "fireMessage: notify");
        }
    }


}