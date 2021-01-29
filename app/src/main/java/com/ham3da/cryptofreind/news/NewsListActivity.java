package com.ham3da.cryptofreind.news;

import android.os.Bundle;
import android.os.Parcelable;

import androidx.appcompat.app.ActionBar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.ham3da.cryptofreind.R;
import com.ham3da.cryptofreind.models.rest.News;
import com.ham3da.cryptofreind.rest.NewsService;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.ham3da.cryptofreind.R.color.colorAccent;


/**
 * Created by Ryan on 12/28/2017.
 */

public class NewsListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener
{

    private NewsListAdapter adapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Observable<News[]> newsObservable;


    public void getNewsObservable(int whatToDo, boolean cache)
    {

        //Example of framework isolation by using observables
        //An standard Rx Action.
        Action1<News[]> subscriber = newsRestResults -> {


            List<NewsItem> myNews = new ArrayList<>();
            if (newsRestResults != null && newsRestResults.length > 0)
            {

                Parcelable recyclerViewState;
                recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
                for (News news : newsRestResults)
                {
                    NewsItem newsItem = new NewsItem(news.getTitle(),
                            news.getUrl(), news.getBody(),
                            news.getImageurl(), news.getSource(),
                            news.getPublishedOn());
                    if (!myNews.contains(newsItem)) myNews.add(newsItem);
                }
                adapter.setData(myNews);
                recyclerView.setAdapter(adapter);
                swipeRefreshLayout.setRefreshing(false);
                recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
                Log.e("News", "call successful");
            }
            else
            {
                swipeRefreshLayout.setRefreshing(false);
                assert newsRestResults != null;
                Log.e("News", "call failed "+newsRestResults.toString());
            }
        };

        switch (whatToDo)
        {
            case 1:
            default:
                //Wrapped observable call
                getNews();
                break;
            case 2:
                //Observable instance from EasyRest
                if (newsObservable == null)
                {
                    newsObservable = NewsService.getPlainObservableNews(this, cache).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread());
                }
                newsObservable.subscribe(subscriber);
                break;
        }

    }

    public void getNews()
    {
        NewsService.getObservableNews(this, true, newsRestResults -> {

            Parcelable recyclerViewState;
            recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
            if (newsRestResults.isSuccessful())
            {
                List<NewsItem> myNews = new ArrayList<>();
                for (News news : newsRestResults.getResultEntity())
                {
                    NewsItem newsItem = new NewsItem(news.getTitle(),
                            news.getUrl(), news.getBody(),
                            news.getImageurl(), news.getSource(),
                            news.getPublishedOn());
                    if (!myNews.contains(newsItem)) myNews.add(newsItem);
                }
                adapter.setData(myNews);
                recyclerView.setAdapter(adapter);
                swipeRefreshLayout.setRefreshing(false);
                recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
            }
            else
            {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);
        Toolbar mToolbar = findViewById(R.id.toolbar_news_list);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setTitle(getResources().getString(R.string.News));
        }

        AppCompatActivity mActivity = this;
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout_recycler);
        recyclerView = findViewById(R.id.newsListRecyclerView);
        HorizontalDividerItemDecoration divider = new HorizontalDividerItemDecoration.Builder(this).build();
        recyclerView.addItemDecoration(divider);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);
        swipeRefreshLayout.setColorSchemeResources(colorAccent);
        adapter = new NewsListAdapter(new ArrayList<>());
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh()
    {
        getNewsObservable(2, false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.news_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.news_refresh_button:
                swipeRefreshLayout.setRefreshing(true);
                onRefresh();
                return true;
            default:
                finish();
                return true;
        }
    }

    //TODO: An advantage about using observables is how easily they allow to avoid lifecycle crashes.
    //If we were using a listener, there's a chance the callback runs at a moment when accesing some
    // elements (like widgets) is illegal/impossible. While we can void listeners to avoid that,
    // depending on the framework, that may not be possible. Also, Rx observers aren't part of a
    // particular library, so they allow for greater isolation between your app's layers.
    @Override
    public void onPause()
    {
        super.onPause();
        //Here, we essentially tell RX to ignore any subscriptions done in the UI thread
        if (newsObservable != null) newsObservable.unsubscribeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public void onResume()
    {
        super.onResume();
        //Here, we make the activity call the news service everytime it cames back from being paused
        if (swipeRefreshLayout != null)
        {
            swipeRefreshLayout.post(() -> {
                        swipeRefreshLayout.setRefreshing(true);
                        getNewsObservable(2, true);
                    }
            );
        }
        else
        {
            onRefresh();
        }
    }
}
