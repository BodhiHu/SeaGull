package com.shawnhu.seagull.seagull.activities;

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

import twitter4j.User;

public class SeagullSearchActivity extends AbsSearchActivity
                                   implements SwipeRefreshLayout.OnRefreshListener {
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected AbsListView        mListView;
    protected ProgressBar        mProgressBar;
    protected MixedResourcesArrayAdapter mAdapter;
    protected String             mQuery = "";
    protected int                mCurrentUserPage = 0;
    protected int                mCurrentStatusPage = 0;

    protected int getContentViewId() {
        return R.layout.swipe_to_refresh_list;
    }

    protected void doSearch(String query) {
        mQuery = query;
        mCurrentStatusPage = 0;
        mCurrentUserPage   = 0;
        mAdapter.clear();
        searchTweetsAsync(query, 0);
        searchUsersAsync(query, 0);
    }

    @Override
    public void onCreate(Bundle saveInstance) {
        super.onCreate(saveInstance);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mListView = (StaggeredGridView) findViewById(R.id.list_view);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mAdapter = new MixedResourcesArrayAdapter(this, null);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onRefreshUp() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRefreshDown() {
        mProgressBar.setVisibility(View.VISIBLE);
        searchUsersAsync(mQuery, mCurrentUserPage);
        searchTweetsAsync(mQuery, mCurrentStatusPage);
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
