package com.ham3da.cryptofreind;

import android.content.Context;

import com.ham3da.cryptofreind.models.rest.CMCCoin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class SortUtil
{
    public static void sortList(ArrayList<CMCCoin> currencyList, int number, Context context)
    {
        switch (number)
        {
            // Price
            case 0:
                Collections.sort(currencyList, (lhs, rhs) -> {
                    if (lhs.getLAST_PRICE() == 0 && rhs.getLAST_PRICE() == 0)
                    {
                        return 0;
                    }
                    if (lhs.getLAST_PRICE() == 0)
                    {
                        return 1;
                    }
                    if (rhs.getLAST_PRICE() == 0)
                    {
                        return -1;
                    }
                    double comp = rhs.getLAST_PRICE() - lhs.getLAST_PRICE();
                    return floatComp((float) comp);
                });
                break;
            // Name A-Z
            case 1:
                Collections.sort(currencyList, (lhs, rhs) -> lhs.getName(context).compareTo(rhs.getName(context)));
                break;
            // Name Z-A
            case 2:
                Collections.sort(currencyList, (lhs, rhs) -> rhs.getName(context).compareTo(lhs.getName(context)));
                break;
            // Change 24h
            case 3:
                Collections.sort(currencyList, (lhs, rhs) -> {
                    if (lhs.getDAILY_CHANGE() == 0 && rhs.getDAILY_CHANGE() == 0)
                    {
                        return 0;
                    }
                    if (lhs.getDAILY_CHANGE() == 0)
                    {
                        return 1;
                    }
                    if (rhs.getDAILY_CHANGE() == 0)
                    {
                        return -1;
                    }
                    double comp = rhs.getDAILY_CHANGE() - lhs.getDAILY_CHANGE();
                    return floatComp((float) comp);
                });
                break;

            // Volume 24h
            case 4:
                Collections.sort(currencyList, (lhs, rhs) -> {
                    if (lhs.getVOLUME() == 0 && rhs.getVOLUME() == 0)
                    {
                        return 0;
                    }
                    if (lhs.getVOLUME() == 0)
                    {
                        return 1;
                    }
                    if (rhs.getVOLUME() == 0)
                    {
                        return -1;
                    }
                    double comp = rhs.getVOLUME() - lhs.getVOLUME();
                    return floatComp((float) comp);
                });
                break;

            case 5:
                Collections.sort(currencyList, (lhs, rhs) -> {
                    if (lhs.getDAILY_CHANGE() == 0 && rhs.getDAILY_CHANGE() == 0)
                    {
                        return 0;
                    }
                    if (lhs.getDAILY_CHANGE() == 0)
                    {
                        return 1;
                    }
                    if (rhs.getDAILY_CHANGE() == 0)
                    {
                        return -1;
                    }
                    double comp = (rhs.getDAILY_CHANGE()) - (lhs.getDAILY_CHANGE());
                    return floatCompLH((float) comp);
                });
                break;
        }
    }

    private static int floatComp(float f)
    {
        if (f == 0)
        {
            return 0;
        }
        else if (f < 0)
        {
            return -1;
        }
        else
        {
            return 1;
        }
    }

    private static int floatCompLH(float f)
    {
        if (f == 0)
        {
            return 0;
        }
        else if (f < 0)
        {
            return 1;
        }
        else
        {
            return -1;
        }
    }
}
