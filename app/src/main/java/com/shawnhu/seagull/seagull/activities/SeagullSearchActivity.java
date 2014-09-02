package com.shawnhu.seagull.seagull.activities;

import android.widget.ArrayAdapter;

import com.shawnhu.seagull.activities.AbsSearchActivity;
import com.shawnhu.seagull.seagull.Seagull;
import com.shawnhu.seagull.seagull.twitter.adapters.MixedResourcesArrayAdapter;
import com.shawnhu.seagull.seagull.twitter.model.ListResponse;
import com.shawnhu.seagull.seagull.twitter.tasks.SearchTweetsTask;
import com.shawnhu.seagull.seagull.twitter.tasks.SearchUsersTask;

import java.util.ArrayList;

import twitter4j.User;

public class SeagullSearchActivity extends AbsSearchActivity {
    MixedResourcesArrayAdapter mAdapter;

    protected ArrayAdapter getAdapter() {
        mAdapter = new MixedResourcesArrayAdapter(this, new ArrayList<Object>(0), this);
        return mAdapter;
    }

    protected void doSearch(String query) {
        searchUsersAsync(query, 1);
        searchTweetsAsync(query, -1);
    }
    protected void loadMore(String query) {
        beginSearch();
        searchUsersAsync(query, 1);
        searchTweetsAsync(query, -1);
    }

    protected void searchUsersAsync(String query, int page) {
        SearchUsersTask searchUsersTask =
        new SearchUsersTask(this, Seagull.sCurrentAccount.sAccountId, query, page) {
            @Override
            protected void onPostExecuteSafe(final ListResponse<User> result) {
                if (result != null && result.getList() != null) {
                    mAdapter.addAll(result.getList());
                    setSearchDone();
                }
            }
        };

        searchUsersTask.setHost(this);
        searchUsersTask.execute();
    }
    protected void searchTweetsAsync(String query, int page) {
        SearchTweetsTask searchTweetsTask =
        new SearchTweetsTask(this, Seagull.sCurrentAccount.sAccountId, query, page) {
            @Override
            protected void onPostExecuteSafe(final ListResponse<twitter4j.Status> result) {
                if (result != null && result.getList() != null) {
                    mAdapter.addAll(result.getList());
                    setSearchDone();
                }
            }
        };

        searchTweetsTask.setHost(this);
        searchTweetsTask.execute();
    }
}
