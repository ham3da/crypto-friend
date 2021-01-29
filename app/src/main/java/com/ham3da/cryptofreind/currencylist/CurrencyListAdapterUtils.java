package com.ham3da.cryptofreind.currencylist;

import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;


public class CurrencyListAdapterUtils
{

    public static void setPercentChangeTextView(TextView textView, Double pctChange, String time,
                                                String negativePercentStringResource, String positivePercentStringResource,
                                                int negativeRedColor, int positiveGreenColor, String pctChangeNotAvailableStringResource)
    {
        if (pctChange == null)
        {
            textView.setText(String.format(pctChangeNotAvailableStringResource, time));
        }
        else
        {
            if (pctChange < 0)
            {
                textView.setText(String.format(Locale.ENGLISH, negativePercentStringResource, time, pctChange));
                textView.setTextColor(negativeRedColor);
            }
            else
            {
                textView.setText(String.format(Locale.ENGLISH, positivePercentStringResource, time, pctChange));
                textView.setTextColor(positiveGreenColor);
            }
        }
    }

    public static void setPercentChangeImageView(ImageView imageView, Double pctChange, int negativeImage, int positiveImage)
    {
        if (pctChange == null)
        {
            imageView.setImageResource(0);
        }
        else
        {
            if (pctChange < 0)
            {
                imageView.setImageResource(negativeImage);
            }
            else
            {
                imageView.setImageResource(positiveImage);
            }
        }
    }

}
