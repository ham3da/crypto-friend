package com.ham3da.cryptofreind.currencylist;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapterCurrencyList extends FragmentStateAdapter
{

    Context mContext;

    List<Fragment> fragmentList;

    public SectionsPagerAdapterCurrencyList(FragmentActivity fragmentActivity, FragmentManager fm)
    {
        super(fragmentActivity);
        mContext = fragmentActivity.getBaseContext();
        fragmentList = new ArrayList<>();
        fragmentList.add(FragmentAllCurrencyList.newInstance());
        fragmentList.add(FragmentFavoriteCurrencyList.newInstance());
        fragmentList.add(FragmentAlarmList.newInstance());
        fragmentList.add(FragmentPortfolio.newInstance());
    }


    @NonNull
    @Override
    public Fragment createFragment(int position)
    {
        return fragmentList.get(position);
    }

    @Override
    public int getItemCount()
    {
        return 4;
    }


    @Override
    public long getItemId(int position)
    {
        return super.getItemId(position);
    }

    public Fragment getFragment(int position)
    {
        Fragment fragment = null;
        fragment = fragmentList.get(position);
        return fragment;
    }


}
