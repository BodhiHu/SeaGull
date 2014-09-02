package com.shawnhu.seagull.activities;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import com.etsy.android.grid.StaggeredGridView;
import com.shawnhu.seagull.R;
import com.shawnhu.seagull.seagull.twitter.adapters.MixedResourcesArrayAdapter;
import com.shawnhu.seagull.widgets.SwipeRefreshLayout;

import java.util.ArrayList;

abstract public class AbsSearchActivity extends Activity
                                        implements SwipeRefreshLayout.OnRefreshListener {
    abstract protected void doSearch(String query);
    abstract protected void loadMore(String query);
    abstract protected ArrayAdapter getAdapter();
    protected void beginSearch() { mProgressBar.setVisibility(View.VISIBLE); }
    protected void setSearchDone() {
        mInitialProgressBar.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
        mSwipeRefreshLayout.setRefreshing(false);
    }


    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected AbsListView        mListView;
    protected ProgressBar        mProgressBar;
    protected ProgressBar        mInitialProgressBar;
    protected String             mQuery = "";


    @Override
    public void onCreate(Bundle savedinstancestate) {
        super.onCreate(savedinstancestate);

        setContentView(R.layout.search_swipe_to_refresh);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mInitialProgressBar = (ProgressBar) findViewById(R.id.initialProgressBar);
        mInitialProgressBar.setIndeterminate(true);

        mProgressBar.setVisibility(View.GONE);
        mInitialProgressBar.setVisibility(View.VISIBLE);


        mListView = (StaggeredGridView) findViewById(R.id.list_view);
        ((StaggeredGridView) mListView).setColumnCount(1);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setEnableScrollUpDown(false, true);

        mListView.setAdapter(getAdapter());

        handleIntent(getIntent());
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            mQuery = intent.getStringExtra(SearchManager.QUERY);
            doSearch(mQuery);
        }
    }
    @Override public void onRefreshUp() {}

    @Override
    public void onRefreshDown() {
        loadMore(mQuery);
    }
}
