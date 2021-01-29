package com.ham3da.cryptofreind;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ActivityWeb extends AppCompatActivity
{

    WebView webView2;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String title1 = getIntent().getStringExtra("title");

        setContentView(R.layout.activity_web_view);
        mToolbar = findViewById(R.id.toolbar_webview);
        setSupportActionBar(mToolbar);

        //getSupportActionBar().setTitle(title1);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        webView2 = findViewById(R.id.webView);


        String text = getIntent().getStringExtra("text");
        setTitle(title1);
       // mToolbar.setTitle(title1);
        webView2.loadDataWithBaseURL("file:///android_res/", text, "text/html", "UTF-8", null);

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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


}
