package com.ham3da.cryptofreind;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.IntentCompat;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.ham3da.cryptofreind.currencylist.CurrencyListTabsActivity;

import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by javad ehteshami on 2021-01-20
 */

public class CFUtility
{
    /**
     * google play = 0, cafebazaar = 1, myket = 2
     */
    private final int Store = 1;
    public static int GOOGLE_PLAY_VER = 0;
    public static int CAFEBAZAAR_VER = 1;
    public static int MYKET_VER = 2;
    private final Context mContext;
    final int MAX_ACTION = 4;
    PreferenceHelper preferenceHelper;

    String TAG = "CFUtility";

    public CFUtility(Context context)
    {
        mContext = context;
        preferenceHelper = new PreferenceHelper(mContext);
    }


    public int getStore()
    {
        return Store;
    }

    public void openEmail()
    {
        try
        {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:ham3da.j@gmail.com"));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.contact_us);
            emailIntent.putExtra(Intent.EXTRA_TEXT, mContext.getString(R.string.contact_text));
            mContext.startActivity(Intent.createChooser(emailIntent, mContext.getString(R.string.choose_email_app)));
        } catch (Exception e)
        {
            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    public void openMyTelegram()
    {
        try
        {

            Intent telegramIntent = new Intent(Intent.ACTION_VIEW);
            telegramIntent.setData(Uri.parse("https://telegram.me/ham3da_ir"));
            telegramIntent.setPackage("org.telegram.messenger");
            mContext.startActivity(telegramIntent);

        } catch (Exception e)
        {
            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public String getAppLink()
    {
        String packageName = mContext.getPackageName();
        String store = "https://play.google.com/store/apps/details?id=" + packageName;//google play
        switch (this.Store)
        {
            case 0:
                store = "https://play.google.com/store/apps/details?id=" + packageName;//google play
                break;
            case 1:
                store = "https://cafebazaar.ir/app/" + packageName + "/";//cafebazaar
                break;
            case 2:
                store = "https://myket.ir/app/" + packageName + "/";//myket
                break;
        }
        return store;
    }

    public void ShowContactUs()
    {
        CharSequence[] options = new CharSequence[]{
                this.mContext.getString(R.string.telegram),
                this.mContext.getString(R.string.email)};

        AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext, R.style.MyAlertDialogTheme);
        builder.setCancelable(true);
        builder.setTitle(mContext.getString(R.string.contact_us));
        builder.setIcon(R.drawable.ic_baseline_mail_24);
        builder.setItems(options, (dialog, which) -> {
            switch (which)
            {
                case 0:
                    openMyTelegram();
                    break;
                case 1:
                    openEmail();
                    break;
            }

        });
        builder.setNegativeButton(this.mContext.getString(R.string.cancel), (dialog, which) -> {
            //the user clicked on Cancel
        });
        builder.show();
    }

    public void shareApp()
    {
        String link = getAppLink();
        String app_name = mContext.getString(R.string.app_name);
        shareText(app_name + " " + System.lineSeparator() + link);
    }

    public void shareText(String subject)
    {
        Intent txtIntent = new Intent(Intent.ACTION_SEND);
        txtIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        txtIntent.setType("text/plain");
        txtIntent.putExtra(Intent.EXTRA_TEXT, subject);
        mContext.startActivity(Intent.createChooser(txtIntent, mContext.getString(R.string.share)));
    }

    public void shareText(String subject, String body)
    {
        Intent txtIntent = new Intent(Intent.ACTION_SEND);
        txtIntent.setType("text/plain");
        txtIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        txtIntent.putExtra(Intent.EXTRA_TEXT, body);
        txtIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mContext.startActivity(Intent.createChooser(txtIntent, mContext.getString(R.string.share)));
    }

    public void gotoAppPage()
    {
        try
        {
            String packageName = mContext.getPackageName();
            Intent intent = new Intent(Intent.ACTION_VIEW);

            switch (this.Store)
            {
                case 0:
                    Uri.Builder uriBuilder = Uri.parse("https://play.google.com/store/apps/details")
                            .buildUpon()
                            .appendQueryParameter("id", packageName)
                            .appendQueryParameter("launch", "false");
                    intent.setData(uriBuilder.build());
                    intent.setPackage("com.android.vending");
                    break;

                case 1:
                    intent.setData(Uri.parse("https://cafebazaar.ir/app/" + packageName + "/"));
                    intent.setPackage("com.farsitel.bazaar");
                    break;
                case 2:
                    intent.setData(Uri.parse("https://myket.ir/app/" + packageName));
                    intent.setPackage("ir.mservices.market");
                    break;
            }
            mContext.startActivity(intent);

        } catch (Exception e)
        {
            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("gotoAppPage", e.getMessage());
        }

    }

    public void gotoRating()
    {
        try
        {
            String packageName = mContext.getPackageName();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            switch (this.Store)
            {
                case 0:
                    Uri.Builder uriBuilder = Uri.parse("https://play.google.com/store/apps/details")
                            .buildUpon()
                            .appendQueryParameter("id", packageName)
                            .appendQueryParameter("launch", "false");
                    intent.setData(uriBuilder.build());
                    intent.setPackage("com.android.vending");
                    break;
                case 1:
                    intent = new Intent(Intent.ACTION_EDIT);
                    intent.setData(Uri.parse("bazaar://details?id=" + packageName));
                    intent.setPackage("com.farsitel.bazaar");

                    break;

                case 2:
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("myket://comment/#Intent;scheme=comment;package=" + packageName + ";end"));
                    intent.setPackage("ir.mservices.market");
                    break;

            }
            mContext.startActivity(intent);

        } catch (Exception e)
        {
            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("gotoRateing", e.getMessage());
        }

    }

    public void openUrl(String url)
    {
        Intent intent1 = new Intent(Intent.ACTION_VIEW);
        intent1.setData(Uri.parse(url));
        mContext.startActivity(intent1);
    }


    public void setActionNum(int num)
    {
        preferenceHelper.setKey("AdAction", num);
    }

    public int getActionNum()
    {
        return preferenceHelper.getKey("AdAction", 0);
    }

    public void increaseActionNum()
    {
        int new_action_num = getActionNum();
        new_action_num++;
        setActionNum(new_action_num);
    }

    public int getMaxActionNum()
    {
        return MAX_ACTION;
    }


    public boolean isExpiredReward()
    {
        long startDateValue = preferenceHelper.getKey("ExpiredDate", (long) 0);
        if (startDateValue <= 0)
        {
            return true;
        }

        long endDateValue = new Date().getTime();

        long diff = endDateValue - startDateValue;


        long hours = TimeUnit.MILLISECONDS.toHours(diff);
        ;


        Log.e(TAG, "isExpiredReward: " + hours);
        if (hours < 24)
        {
            return false;
        }
        else
        {
            preferenceHelper.removeKey("ExpiredDate");
            return true;
        }
    }

    public void updateRewardDate()
    {
        long endDateValue = new Date().getTime();
        preferenceHelper.setKey("ExpiredDate", endDateValue);
    }

    public static boolean isNetworkConnected(Context context)
    {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;

    }


    public static void restartApp(Context context){
        Intent intent = new Intent(context, CurrencyListTabsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

    }



}
