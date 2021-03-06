package com.shawnhu.seagull.seagull.twitter.fragments;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ProgressBar;

import com.shawnhu.seagull.R;
import com.shawnhu.seagull.fragments.PersistentCursorFragment;
import com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants;
import com.shawnhu.seagull.seagull.twitter.adapters.StatusesCursorAdapter;
import com.shawnhu.seagull.seagull.twitter.content.TweetStore;
import com.shawnhu.seagull.seagull.twitter.model.TwitterStatusListResponse;
import com.shawnhu.seagull.seagull.twitter.tasks.GetHomeTimelineTask;
import com.shawnhu.seagull.widgets.SwipeRefreshLayout;

import java.security.InvalidParameterException;
import java.util.List;

public class SeagullHomeFragment extends PersistentCursorFragment
        implements PersistentCursorFragment.OnLoadMoreDataListener,
                   SwipeRefreshLayout.OnRefreshListener {

    static public SeagullHomeFragment newInstance(Bundle args) {
        SeagullHomeFragment fragment = new SeagullHomeFragment();
        fragment.setArguments(args);
        return fragment;
    }



    public SeagullHomeFragment() {
    }

    private long mAccountId = -1;

    private StatusesCursorAdapter mCursorAdapter = null;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar        mProgressBar;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //Till this moment, fragment has activity
    }
    @Override
    public void onCreate(Bundle savedBundleInstance) {
        super.onCreate(savedBundleInstance);

        setOnLoadMoreDataListener(this);
        Bundle args = getArguments();
        if (args != null) {
            mAccountId = args.getLong(SeagullTwitterConstants.EXTRA_ACCOUNT_ID, -1);
        }

        if (mAccountId < 0) {
            throw new InvalidParameterException("account id is negative");
        }

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mCursorAdapter == null) {
            mCursorAdapter = new StatusesCursorAdapter(getActivity(), null, 0, this);
        }
        if (mCursorAdapter != null && mCursorAdapter.getCount() == 0) {
            getLoaderManager().initLoader(0, null, this);
        }

        View v = super.onCreateView(inflater, container, savedInstanceState);
        v.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setOnRefreshListener(this);
        }
        mProgressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);
        return v;
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        getLoaderManager().destroyLoader(0);
    }

    protected ListAdapter getListAdapter() {
        return mCursorAdapter;
    }
    protected int getContentViewId() {
        return R.layout.fragment_home;
    }

    public void loadMoreHead() {
        Cursor c = (Cursor) mListView.getItemAtPosition(0);
        long since_id   = -1;
        if (c != null) {
            since_id = c.getLong(c.getColumnIndex(TweetStore.Statuses.STATUS_ID));
        }

        getHomeTimelineAsync(
                new long[]{mAccountId},
                new long[]{-1},
                new long[]{since_id}
        );

    }
    public void loadMoreTail() {
        Cursor c = (Cursor) mListView.getItemAtPosition(mListView.getCount() - 1);
        long max_id = -1;
        if (c != null) {
            max_id = c.getLong(c.getColumnIndex(TweetStore.Statuses.STATUS_ID));
        }
        getHomeTimelineAsync(
                new long[]{mAccountId},
                new long[]{max_id},
                new long[]{-1}
        );
    }

    @Override
    public void onRefreshUp() {
        loadMoreHead();
    }
    @Override
    public void onRefreshDown() {
        mProgressBar.setVisibility(View.VISIBLE);
        loadMoreTail();
    }

    @Override
    public void onLoadMoreHead() {}
    @Override
    public void onLoadMoreTail() {}

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        super.onCreateLoader(id, args);

        CursorLoader cursorLoader =
                new CursorLoader(getActivity(),
                        StatusesCursorAdapter.CONTENT_URI,
                        StatusesCursorAdapter.PROJECTION,
                        StatusesCursorAdapter.SELECTION,
                        new String[] {
                                String.valueOf(mAccountId),
                        },
                        StatusesCursorAdapter.SORT_ORDER
                );

        return cursorLoader;
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() == 0) {
            getHomeTimelineAsync(
                    new long[]{mAccountId},
                    new long[]{-1},
                    new long[]{-1}
            );
            return;
        }
        mCursorAdapter.swapCursor(data);

        super.onLoadFinished(loader, data);
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        super.onLoaderReset(loader);

        mCursorAdapter.swapCursor(null);
    }

    protected void getHomeTimelineAsync(final long[] account_ids, final long[] max_ids, final long[] since_ids) {
        GetHomeTimelineTask getHomeTimelineTask =
        new GetHomeTimelineTask(getActivity(), account_ids, max_ids, since_ids) {
            @Override
            protected void onPostExecuteSafe(final List<TwitterStatusListResponse> responses) {
                super.onPostExecuteSafe(responses);
                mProgressBar.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        };

        getHomeTimelineTask.setHost(this);
        getHomeTimelineTask.execute();
    }

}
