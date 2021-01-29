package com.ham3da.cryptofreind;

import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null)
        {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat
    {

        AppSettings appSettings;
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
        {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            String Per_second = getString(R.string.Per_second);
            appSettings = new AppSettings(getContext());

            App app = (App) getContext().getApplicationContext();

            EditTextPreference pref = (EditTextPreference)findPreference("price_updates_frequency");
            pref.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            });

            pref.setSummary(Per_second+" ("+appSettings.getUpdateTime()+")");
            pref.setOnPreferenceChangeListener((preference, newValue) -> {


                if(Integer.parseInt(newValue.toString()) < 5 )
                {
                    Toast.makeText(getContext(), R.string.must_above_five, Toast.LENGTH_SHORT).show();
                    return false;
                }

                pref.setSummary(Per_second+" ("+newValue.toString()+")");
                app.resetTickerServiceTimer(Integer.parseInt(newValue.toString()));

                return true;
            });




        }
    }
}