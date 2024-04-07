package com.ham3da.cryptofreind.currencylist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;

import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;


import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.MenuItem;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ham3da.cryptofreind.ActivityWeb;
import com.ham3da.cryptofreind.App;
import com.ham3da.cryptofreind.AppSettings;
import com.ham3da.cryptofreind.CFUtility;
import com.ham3da.cryptofreind.R;
import com.ham3da.cryptofreind.SettingsActivity;
import com.ham3da.cryptofreind.models.rest.CMCCoin;
import com.ham3da.cryptofreind.news.NewsListActivity;
import com.ham3da.cryptofreind.service.TickerService;

import java.io.InputStream;
import java.util.Locale;
import java.util.Objects;


public class CurrencyListTabsActivity extends AppCompatActivity implements
        FragmentFavoriteCurrencyList.AllCoinsListUpdater,
        FragmentAllCurrencyList.FavoritesListUpdater,
        FragmentAlarmList.AlarmListFragmentInterface,
        FragmentPortfolio.PortfolioListFragmentInterface,
        NavigationView.OnNavigationItemSelectedListener {


    private SectionsPagerAdapterCurrencyList mSectionsPagerAdapter;
    public ViewPager2 mViewPager;

    public final static String DAY = "24h";
    public final static String SORT_SETTING = "sort_setting";
    public AppCompatActivity mContext;
    AppSettings appSettings;
    String TAG = "CurrencyListTabsActivity";
    String version = com.ham3da.cryptofreind.BuildConfig.VERSION_NAME;

    Intent serviceIntent;

    Boolean broadCastReceiver = false;
    TabLayout tabLayout;
    App app;
    Boolean updateRequired = false;

    boolean is_created = false;
    InterstitialAd mInterstitialAd;
    AdRequest adRequest;

    @StringRes
    private static final int[] TAB_TITLES = new int[]{
            R.string.all_coins,
            R.string.favorites,
            R.string.alarms,
            R.string.portfolio,
    };
    @DrawableRes
    private static final int[] TAB_ICONS = new int[]{R.drawable.ic_btc_l,
            R.drawable.ic_baseline_bookmark_24,
            R.drawable.ic_baseline_notifications_24,
            R.drawable.ic_baseline_account_balance_wallet_24,
    };

    private final BroadcastReceiver updatePriceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (!is_created) {
                return;
            }
            switch (action) {
                case "com.ham3da.cryptofreind.broadcast.UPDATE_PRICE":
                    updateAllCoinCurrencyList();
                    updateFavCurrencyList();
                    updatePortfolio();
                    break;
                case "com.ham3da.cryptofreind.broadcast.UPDATE_ALARM":
                    updateAlarmList();
                    break;
                case "com.ham3da.cryptofreind.broadcast.UPDATE_PRICE_FAIL":

                    String error = intent.getStringExtra("error");
                    Toast.makeText(getBaseContext(), error, Toast.LENGTH_SHORT).show();
                    showServerError(error);
                    stopLoader();
                    break;

                case "android.net.conn.CONNECTIVITY_CHANGE":

                    if (!CFUtility.isNetworkConnected(getBaseContext())) {
                        showConnErrorCoinsAndFavorites();
                    }

                    break;

            }
        }
    };


    public void registerBroadcast() {
        IntentFilter intentFilters = new IntentFilter();

        intentFilters.addAction("com.ham3da.cryptofreind.broadcast.UPDATE_PRICE");
        intentFilters.addAction("com.ham3da.cryptofreind.broadcast.UPDATE_PRICE_FAIL");
        intentFilters.addAction("com.ham3da.cryptofreind.broadcast.UPDATE_ALARM");
        intentFilters.addAction("android.net.conn.CONNECTIVITY_CHANGE");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(updatePriceReceiver, intentFilters, Context.RECEIVER_EXPORTED);

        } else {

            registerReceiver(updatePriceReceiver, intentFilters);
        }

    }

    @Override
    public void applyOverrideConfiguration(Configuration overrideConfiguration) {
        Log.e(TAG, "applyOverrideConfiguration: ");
        if (overrideConfiguration != null) {
            int uiMode = overrideConfiguration.uiMode;
            overrideConfiguration.setTo(getBaseContext().getResources().getConfiguration());
            overrideConfiguration.uiMode = uiMode;
        }
        super.applyOverrideConfiguration(overrideConfiguration);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        Log.e(TAG, "attachBaseContext: ");
        super.attachBaseContext(newBase);
    }

    @Override
    protected void onPause() {
        if (broadCastReceiver) {
            unregisterReceiver(updatePriceReceiver);
            broadCastReceiver = false;
        }
        super.onPause();
    }


    @Override
    protected void onResume() {
        super.onResume();

        Log.e(TAG, "onResume: Main");
        registerBroadcast();
        broadCastReceiver = true;
        if (updateRequired) {
            updateAll();
        }

    }

    private void updateAll() {
        broadCastReceiver = false;
        updateAllCoinCurrencyList();
        updateFavCurrencyList();
        updateAlarmList();
        updatePortfolio();

    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy: Main Ac");

        if (broadCastReceiver) {
            unregisterReceiver(updatePriceReceiver);
            broadCastReceiver = false;
        }
        super.onDestroy();
        appSettings = new AppSettings(getApplicationContext());

        if (!appSettings.getServiceUpdate()) {
            stopService(serviceIntent);
        }
        is_created = false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.e(TAG, "onCreate: 2");
        super.onCreate(savedInstanceState);
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        setContentView(R.layout.activity_currency_list_tabs);
        FirebaseCrashlytics.getInstance();

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();

                    // Log and toast

                    Log.d("token", token);

                });

        app = (App) getApplicationContext();

        progress_bar_dlg = findViewById(R.id.progressBar_loader);

        MobileAds.initialize(this, initializationStatus -> {
        });


        serviceIntent = new Intent(CurrencyListTabsActivity.this, TickerService.class);
        mContext = this;
        Toolbar mToolbar = findViewById(R.id.toolbar_currency_list);
        setSupportActionBar(mToolbar);
        tabLayout = findViewById(R.id.currency_list_tabs);
        mViewPager = findViewById(R.id.currency_list_tabs_container);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, mToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        appSettings = new AppSettings(getApplicationContext());
        initPager();


        if (!app.getTickerServiceRunning()) {
            int delayTime = appSettings.getUpdateTime();
            serviceIntent.putExtra("delay", 0);
            ContextCompat.startForegroundService(CurrencyListTabsActivity.this, serviceIntent);
        }

        registerBroadcast();
        broadCastReceiver = true;
        is_created = true;
        adAsk = false;
    }


    private void initPager() {
        mSectionsPagerAdapter = new SectionsPagerAdapterCurrencyList(this, getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.registerOnPageChangeCallback(new ViewPager2OnPageChangeCallback());
        new TabLayoutMediator(tabLayout, mViewPager,
                (tab, position) -> tab.setText(getString(TAB_TITLES[position]))).attach();
        tabLayout.setSelectedTabIndicatorColor(Color.WHITE);
        setupTabIcons();
    }

    private void setPagerAdapter() {
        mSectionsPagerAdapter = new SectionsPagerAdapterCurrencyList(this, getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }

    boolean requireCloseApp;

    public void displayInterstitial() {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(this);
        } else {
            AdmobInterstitialInit(true);
        }
    }

    private void AdmobInterstitialInit(boolean requestShow) {
        FullScreenContentCallback fullScreenContentCallback = new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                mInterstitialAd = null;

            }


            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                Log.e(TAG, "onAdFailedToShowFullScreenContent: " + adError.getMessage());
                super.onAdFailedToShowFullScreenContent(adError);

            }
        };

        String mAdunitID = getString(R.string.admob_Interstitial_ad_unit);

        InterstitialAd.load(this, mAdunitID, new AdRequest.Builder().build(),
                new InterstitialAdLoadCallback() {

                    @Override
                    public void onAdLoaded(@NonNull com.google.android.gms.ads.interstitial.InterstitialAd ad) {
                        mInterstitialAd = ad;
                        mInterstitialAd.setFullScreenContentCallback(fullScreenContentCallback);
                        if (requestShow) {

                            progress_bar_dlg.setVisibility(View.GONE);
                            mInterstitialAd.show(CurrencyListTabsActivity.this);
                        }
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                        progress_bar_dlg.setVisibility(View.GONE);
                        Log.e(TAG, "onAdFailedToLoad: " + adError.getMessage());

                    }
                });

    }

    private void askExitAd(boolean closeApp1) {
        requireCloseApp = closeApp1;
        adAsk = true;
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setIcon(R.drawable.ic_baseline_favorite_border_24);

        dialog.setCancelable(true);
        dialog.setTitle(R.string.easy_donating);
        dialog.setMessage(R.string.admob_des);
        dialog.setPositiveButton(R.string.view_admob, (dialog1, id) -> {
            progress_bar_dlg.setVisibility(View.VISIBLE);

            displayInterstitial();


        }).setNegativeButton(R.string.close, (dialog12, which) -> finish());

        final AlertDialog alert = dialog.create();
        alert.show();
    }

    ProgressBar progress_bar_dlg;

    private void setupTabIcons() {
        if (tabLayout != null) {
            tabLayout.getTabAt(0).setIcon(TAB_ICONS[0]);
            tabLayout.getTabAt(1).setIcon(TAB_ICONS[1]);
            tabLayout.getTabAt(2).setIcon(TAB_ICONS[2]);
            tabLayout.getTabAt(3).setIcon(TAB_ICONS[3]);
            tabLayout.setInlineLabel(true);
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        CFUtility cfUtility = new CFUtility(this);
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_action_upgrade:
                askExitAd(false);
                break;
            case R.id.nav_action_news:
                updateRequired = true;
                startActivity(new Intent(CurrencyListTabsActivity.this, NewsListActivity.class));
                break;

            case R.id.nav_action_settings:
                updateRequired = true;
                startActivity(new Intent(CurrencyListTabsActivity.this, SettingsActivity.class));
                break;
            case R.id.nav_about:
                updateRequired = true;
                showAbout();
                break;
            case R.id.nav_rating:
                updateRequired = true;
                cfUtility.gotoRating();
                break;
            case R.id.nav_policy:
                updateRequired = true;
                showPrivacyPolicy();
                break;
            case R.id.nav_contact_us:
                cfUtility.ShowContactUs();
                break;
            case R.id.nav_share:
                cfUtility.shareApp();
                break;
            default:
                Log.e(TAG, "onNavigationItemSelected: " + id);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int ItemId = item.getItemId();
        //if (ItemId == R.id.action_upgrade)
        // {
        //    showAlertReward();
        //}
        return super.onOptionsItemSelected(item);
    }

    boolean adAsk = false;

    @Override
    public void onBackPressed() {

        if (adAsk) {
            super.onBackPressed();
        } else {
            askExitAd(true);
        }

    }

    public void removeFavorite(CMCCoin coin) {
        FragmentFavoriteCurrencyList frag = (FragmentFavoriteCurrencyList) mSectionsPagerAdapter.getFragment(1);
        if (frag != null && frag.getActivity() != null) {
            frag.removeFavorite(coin);
        }
    }

    public void addFavorite(CMCCoin coin) {
        FragmentFavoriteCurrencyList frag = (FragmentFavoriteCurrencyList) mSectionsPagerAdapter.getFragment(1);
        if (frag != null && frag.getActivity() != null) {
            frag.addFavorite(coin);
        }
    }

    public void allCoinsModifyFavorites(CMCCoin coin) {
        FragmentAllCurrencyList frag = (FragmentAllCurrencyList) mSectionsPagerAdapter.getFragment(0);
        if (frag != null && frag.getActivity() != null) {
            frag.getAdapter().notifyDataSetChanged();
        } else {
            setPagerAdapter();
        }
    }

    public void performFavsSort() {
        FragmentFavoriteCurrencyList frag = (FragmentFavoriteCurrencyList) mSectionsPagerAdapter.getFragment(1);
        if (frag != null && frag.getActivity() != null) {
            frag.performFavsSort();
        }
    }


    protected void stopLoader() {
        FragmentAllCurrencyList frag = (FragmentAllCurrencyList) mSectionsPagerAdapter.getFragment(0);
        if (frag != null && frag.getActivity() != null) {
            frag.stopLoading();
        } else {
            setPagerAdapter();
        }

        FragmentFavoriteCurrencyList frag_fav = (FragmentFavoriteCurrencyList) mSectionsPagerAdapter.getFragment(1);
        if (frag_fav != null && frag_fav.getActivity() != null) {
            frag_fav.stopLoading();
        }
    }


    protected void showServerError(String msg) {
        FragmentAllCurrencyList frag = (FragmentAllCurrencyList) mSectionsPagerAdapter.getFragment(0);
        if (frag != null && frag.getActivity() != null) {
            frag.showItError(msg);
        }

        FragmentFavoriteCurrencyList frag_fav = (FragmentFavoriteCurrencyList) mSectionsPagerAdapter.getFragment(1);
        if (frag_fav != null && frag_fav.getActivity() != null) {
            frag_fav.showItError(msg);
        }

        FragmentPortfolio frag_portf = (FragmentPortfolio) mSectionsPagerAdapter.getFragment(3);
        if (frag_portf != null && frag_portf.getActivity() != null) {
            frag_portf.showItError(msg);
        }

    }


    protected void showConnErrorCoinsAndFavorites() {
        FragmentAllCurrencyList frag = (FragmentAllCurrencyList) mSectionsPagerAdapter.getFragment(0);
        if (frag != null && frag.getActivity() != null) {
            frag.showItError(null);
        } else {
            setPagerAdapter();
        }

        FragmentFavoriteCurrencyList frag_fav = (FragmentFavoriteCurrencyList) mSectionsPagerAdapter.getFragment(1);
        if (frag_fav != null && frag_fav.getActivity() != null) {
            frag_fav.showItError(null);
        }

        FragmentPortfolio frag_portf = (FragmentPortfolio) mSectionsPagerAdapter.getFragment(3);
        if (frag_fav != null && frag_fav.getActivity() != null) {
            frag_portf.showItError(null);
        }

    }


    protected void refreshCoinsAndFavorites() {
        FragmentAllCurrencyList frag = (FragmentAllCurrencyList) mSectionsPagerAdapter.getFragment(0);
        if (frag != null && frag.getActivity() != null) {

            frag.hideItError();
            frag.onRefresh();
        } else {
            setPagerAdapter();
        }

        FragmentFavoriteCurrencyList frag_fav = (FragmentFavoriteCurrencyList) mSectionsPagerAdapter.getFragment(1);
        if (frag_fav != null && frag_fav.getActivity() != null) {

            frag_fav.hideItError();
            frag_fav.onRefresh();

        }
    }

    @Override
    public void portfolioRefreshAllCoins() {
        refreshAllCoins();
    }

    @Override
    public void refreshAllCoins() {
        FragmentAllCurrencyList frag = (FragmentAllCurrencyList) mSectionsPagerAdapter.getFragment(0);
        if (frag != null && frag.getActivity() != null) {
            frag.checkConn();
        } else {
            setPagerAdapter();
        }
    }

    @Override
    public void updateFavCurrencyList() {
        FragmentFavoriteCurrencyList frag = (FragmentFavoriteCurrencyList) mSectionsPagerAdapter.getFragment(1);
        if (frag != null && frag.getActivity() != null) {
            frag.updateFavCurrencyList();
        } else {
            setPagerAdapter();
        }
    }

    public void performAllCoinsSort() {
        FragmentAllCurrencyList frag = (FragmentAllCurrencyList) mSectionsPagerAdapter.getFragment(0);
        if (frag != null && frag.getActivity() != null) {

            frag.performAllCoinsSort();

        } else {
            setPagerAdapter();
        }
    }

    @Override
    public void updateAllCoinCurrencyList() {
        FragmentAllCurrencyList frag = (FragmentAllCurrencyList) mSectionsPagerAdapter.getFragment(0);
        if (frag != null && frag.getActivity() != null) {
            frag.updateAllCoinCurrencyList();
        } else {
            setPagerAdapter();
        }
    }


    @Override
    public void updatePortfolio() {
        FragmentPortfolio frag = (FragmentPortfolio) mSectionsPagerAdapter.getFragment(3);
        if (frag != null && frag.getActivity() != null) {

            frag.updatePortfolioList();

        }
    }

    @Override
    public void updateAlarmList() {
        FragmentAlarmList frag = (FragmentAlarmList) mSectionsPagerAdapter.getFragment(2);
        if (frag != null && frag.getActivity() != null) {

            frag.updateAlarmList();

        }
    }

    public void showAbout() {

        try {

            String title = getString(R.string.about);
            String background_color = "#ffffff";
            String text_color = "#000";
            String link_color = "blue";


            String CurrentLang = Locale.getDefault().getLanguage();

            String file_name = "about_" + CurrentLang + ".htm";
            InputStream stream = getAssets().open(file_name);

            int size = stream.available();
            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.close();

            String text = new String(buffer);

            text = text.replace("$version", version);
            text = text.replace("$name", getString(R.string.app_name));

            text = text.replace("$fontSize", 17 + "px");
            text = text.replace("$background_color", background_color);
            text = text.replace("$text_color", text_color);
            text = text.replace("$link_color", link_color);

            Intent intent = new Intent(CurrencyListTabsActivity.this, ActivityWeb.class);
            intent.putExtra("title", title);
            intent.putExtra("text", text);
            startActivity(intent);

        } catch (Exception ex) {
            Log.e("about", "showAbout: " + ex.getMessage());
        }

    }

    public void showPrivacyPolicy() {
        try {


            String title = getString(R.string.privacy_policy);
            String background_color = "#ffffff";
            String text_color = "#000";
            String link_color = "blue";


            String CurrentLang = Locale.getDefault().getLanguage();
            String file_name = "pr_po_" + CurrentLang + ".htm";
            InputStream stream = getAssets().open(file_name);

            int size = stream.available();
            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.close();

            String text = new String(buffer);

            text = text.replace("$version", version);
            text = text.replace("$name", getString(R.string.app_name));

            text = text.replace("$fontSize", "17px");
            text = text.replace("$background_color", background_color);
            text = text.replace("$text_color", text_color);
            text = text.replace("$link_color", link_color);

            Intent intent = new Intent(CurrencyListTabsActivity.this, ActivityWeb.class);
            intent.putExtra("title", title);
            intent.putExtra("text", text);
            startActivity(intent);

        } catch (Exception ex) {
            Log.e("about", "showAbout: " + ex.getMessage());
        }
    }

    class ViewPager2OnPageChangeCallback extends ViewPager2.OnPageChangeCallback {


        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }

        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            Fragment fragment = mSectionsPagerAdapter.getFragment(position);
            if (fragment != null) {
                //fragment.onResume();
                Log.e(TAG, "onPageSelected: 0");
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            super.onPageScrollStateChanged(state);
        }
    }
}
