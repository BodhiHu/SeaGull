package com.shawnhu.seagull.seagull.activities;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ProgressBar;

import com.etsy.android.grid.StaggeredGridView;
import com.shawnhu.seagull.R;
import com.shawnhu.seagull.activities.AbsSearchActivity;
import com.shawnhu.seagull.seagull.Seagull;
import com.shawnhu.seagull.seagull.twitter.adapters.MixedResourcesArrayAdapter;
import com.shawnhu.seagull.seagull.twitter.model.ListResponse;
import com.shawnhu.seagull.seagull.twitter.tasks.SearchTweetsTask;
import com.shawnhu.seagull.seagull.twitter.tasks.SearchUsersTask;
import com.shawnhu.seagull.widgets.SwipeRefreshLayout;

import java.util.ArrayList;

import twitter4j.User;

public class SeagullSearchActivity extends Activity
                                   implements SwipeRefreshLayout.OnRefreshListener {
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected AbsListView        mListView;
    protected ProgressBar        mProgressBar;
    protected MixedResourcesArrayAdapter mAdapter;
    protected String             mQuery = "";
    protected int                mCurrentUserPage = 0;
    protected int                mCurrentStatusPage = 0;


    @Override
    public void onCreate(Bundle savedinstancestate) {
        super.onCreate(savedinstancestate);

        setContentView(R.layout.swipe_to_refresh_list);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mListView = (StaggeredGridView) findViewById(R.id.list_view);
        ((StaggeredGridView) mListView).setColumnCount(1);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setEnableScrollUpDown(false, true);

        mAdapter = new MixedResourcesArrayAdapter(this, new ArrayList<Object>(0));
        mListView.setAdapter(mAdapter);

        handleIntent(getIntent());
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doSearch(query);
        }
    }

    protected void doSearch(String query) {
        mQuery = query;
        mCurrentStatusPage = 0;
        mCurrentUserPage   = 0;
        mAdapter.clear();
        searchTweetsAsync(query, 0);
        searchUsersAsync(query, 0);
    }


    @Override public void onRefreshUp() { }

    @Override
    public void onRefreshDown() {
        mProgressBar.setVisibility(View.VISIBLE);
        searchUsersAsync(mQuery, mCurrentUserPage);
        mCurrentUserPage++;
        searchTweetsAsync(mQuery, mCurrentStatusPage);
        mCurrentStatusPage++;
    }

    protected void searchUsersAsync(String query, int page) {
        new SearchUsersTask(this, Seagull.sCurrentAccount.sAccountId, query, page) {
            @Override
            protected void onPostExecute(final ListResponse<User> result) {
                if (result != null && result.getList() != null) {
                    mAdapter.addAll(result.getList());
                    mSwipeRefreshLayout.setRefreshing(false);
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        }.execute();
    }
    protected void searchTweetsAsync(String query, int page) {
        new SearchTweetsTask(this, Seagull.sCurrentAccount.sAccountId, query, page) {
            @Override
            protected void onPostExecute(final ListResponse<twitter4j.Status> result) {
                if (result != null && result.getList() != null) {
                    mAdapter.addAll(result.getList());
                    mSwipeRefreshLayout.setRefreshing(false);
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        }.execute();
    }
}
