package com.ham3da.cryptofreind;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.Objects;

public class MyFirebaseMessagingService extends FirebaseMessagingService
{

    @Override
    public void onNewToken(@NonNull String s)
    {
        super.onNewToken(s);
        Log.e("NEW_TOKEN_2", s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage)
    {
        super.onMessageReceived(remoteMessage);

        String url = "";
        if (remoteMessage.getData().containsKey("Myurl"))
        {
            url = remoteMessage.getData().get("Myurl");
        }
        JSONObject json = new JSONObject(remoteMessage.getData());
        //Log.e("getData", json.toString());

        this.sendNotification(Objects.requireNonNull(remoteMessage.getNotification()).getBody(), remoteMessage.getNotification().getTitle(), url);

    }

    @Override
    public void onSendError(@NonNull String var1, Exception var2)
    {
        Log.e("onSendError", var2.getMessage());
    }

    private void sendNotification(String messageBody, String messageTitle, String url)
    {
        try
        {
            String CHANNEL_ID = "crypto_channel_fmg";
            String app_name = getString(R.string.app_name);
            NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationCompat.Builder mBuilder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, app_name, NotificationManager.IMPORTANCE_DEFAULT);
                manager.createNotificationChannel(notificationChannel);

            }

            mBuilder = new NotificationCompat.Builder(this, "crypto_channel_fmg")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(messageTitle)
                    .setContentText(messageBody);


            if (url.length() > 0)
            {
                Intent notificationIntent = new Intent(Intent.ACTION_VIEW);
                notificationIntent.setData(Uri.parse(url));
                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pending = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_ONE_SHOT
                );
                mBuilder.setContentIntent(pending);
            }
            mBuilder.setAutoCancel(true);

            manager.notify(0, mBuilder.build());


        } catch (Exception ex)
        {
            Log.e("FirebaseMessagingLog", ex.getMessage());
        }


    }
}