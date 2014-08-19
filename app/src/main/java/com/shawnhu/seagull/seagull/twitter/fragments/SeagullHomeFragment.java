package com.shawnhu.seagull.seagull.twitter.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import com.shawnhu.seagull.R;
import com.shawnhu.seagull.fragments.HomeFragment;
import com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants;
import com.shawnhu.seagull.seagull.twitter.adapters.StatusesCursorAdapter;
import com.shawnhu.seagull.seagull.twitter.model.TwitterStatusListResponse;
import com.shawnhu.seagull.seagull.twitter.providers.TweetStore;
import com.shawnhu.seagull.seagull.twitter.tasks.GetHomeTimelineTask;

import java.security.InvalidParameterException;
import java.util.List;

/**
 * Created by shawnhu on 8/16/14.
 */
public class SeagullHomeFragment extends HomeFragment
        implements HomeFragment.OnLoadMoreDataListener, LoaderManager.LoaderCallbacks<Cursor> {

    static public SeagullHomeFragment newInstance(Bundle args) {
        SeagullHomeFragment fragment =  new SeagullHomeFragment();
        fragment.setArguments(args);
        return fragment;
    }



    public SeagullHomeFragment() {

    }

    private int mFirstVisibleItemId = -1;
    private long mAccountId = -1;

    private StatusesCursorAdapter mAdapter = new StatusesCursorAdapter(getActivity(), null, 0);

    @Override
    public void onCreate(Bundle savedBundleInstance) {
        super.onCreate(savedBundleInstance);

        setOnLoadMoreDataListener(this);
        Bundle args = getArguments();
        if (args != null) {
            mAccountId = args.getLong(SeagullTwitterConstants.EXTRA_ACCOUNT_ID, -1);
        }

        if (mAccountId < 0) {
            throw new InvalidParameterException("account id is negative, this is a bug");
        }

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = super.onCreateView(inflater, container, savedInstanceState);
        getLoaderManager().initLoader(0, null, this);

        return v;
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        //this will call onLoaderReset
        getLoaderManager().destroyLoader(0);
    }

    protected ListAdapter getListAdapter() {
        Integer c;
        try {
            c = Integer.valueOf(mAdapter.getValue(StatusesCursorAdapter.CURRENT_VISIBLE_ITEM_ID));
        } catch (NumberFormatException ne) {
            ne.printStackTrace();
            c = -1;
        }

        mFirstVisibleItemId = c;
        return mAdapter;
    }
    protected int getContentViewId() {
        return R.layout.fragment_home;
    }
    protected int getFirstVisibleItemId() {
        return mFirstVisibleItemId;
    }

    @Override
    public void onLoadMoreHead() {
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
    @Override
    public void onLoadMoreTail() {
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

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        CursorLoader cursorLoader =
                new CursorLoader(getActivity(),
                        StatusesCursorAdapter.CONTENT_URI,
                        StatusesCursorAdapter.PROJECTION,
                        StatusesCursorAdapter.SELECTION,
                        new String[] {
                        },
                        StatusesCursorAdapter.SORT_ORDER
                );

        return cursorLoader;
    }
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() == 0) {
            getHomeTimelineAsync(
                    new long[]{mAccountId},
                    new long[]{-1},
                    new long[]{-1}
            );
            return;
        }

        Cursor c = (Cursor) mListView.getItemAtPosition(mListView.getFirstVisiblePosition());
        mFirstVisibleItemId = c.getInt(c.getColumnIndex(TweetStore.Statuses._ID));
        mAdapter.swapCursor(data);

        int pos = -1;
        do {
            int tmp_pos = data.getPosition();
            if (tmp_pos == mFirstVisibleItemId) {
                pos = tmp_pos;
                break;
            }
        } while (data.moveToNext());

        if (pos >= 0) {
            mListView.setSelection(pos);
        }
    }
    public void onLoaderReset(Loader<Cursor> loader) {
        Cursor c = (Cursor) mListView.getItemAtPosition(mListView.getFirstVisiblePosition());
        mFirstVisibleItemId = c.getInt(c.getColumnIndex(TweetStore.Statuses._ID));

        mAdapter.setValue(StatusesCursorAdapter.CURRENT_VISIBLE_ITEM_ID, String.valueOf(mFirstVisibleItemId));
        mAdapter.saveNow();

        mAdapter.swapCursor(null);
    }

    protected void getHomeTimelineAsync(final long[] account_ids, final long[] max_ids, final long[] since_ids) {
        new GetHomeTimelineTask(getActivity(), account_ids, max_ids, since_ids) {
            @Override
            protected void onPostExecute(List<TwitterStatusListResponse> result) {
                getLoaderManager().restartLoader(0, null, SeagullHomeFragment.this);
            }
        }.execute();
    }
}
