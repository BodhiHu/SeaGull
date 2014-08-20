package com.shawnhu.seagull.seagull.twitter.fragments;

import android.app.Activity;
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
import com.shawnhu.seagull.fragments.PersistentCursorFragment;
import com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants;
import com.shawnhu.seagull.seagull.twitter.adapters.StatusesCursorAdapter;
import com.shawnhu.seagull.seagull.twitter.content.TweetStore;
import com.shawnhu.seagull.seagull.twitter.tasks.GetHomeTimelineTask;

import java.security.InvalidParameterException;

/**
 * Created by shawnhu on 8/16/14.
 */
public class SeagullHomeFragment extends PersistentCursorFragment
        implements PersistentCursorFragment.OnLoadMoreDataListener {

    static public SeagullHomeFragment newInstance(Bundle args) {
        SeagullHomeFragment fragment = new SeagullHomeFragment();
        fragment.setArguments(args);
        return fragment;
    }



    public SeagullHomeFragment() {
    }

    private long mAccountId = -1;

    private StatusesCursorAdapter mAdapter = null;

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
            throw new InvalidParameterException("account id is negative, this is a bug");
        }

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (mAdapter == null) {
            mAdapter = new StatusesCursorAdapter(getActivity(), null, 0);
        }
        if (mAdapter != null && mAdapter.getCount() == 0) {
            getLoaderManager().initLoader(0, null, this);
        }

        View v = super.onCreateView(inflater, container, savedInstanceState);
        return v;
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        getLoaderManager().destroyLoader(0);
    }

    protected ListAdapter getListAdapter() {
        return mAdapter;
    }
    protected int getContentViewId() {
        return R.layout.fragment_home;
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
        mAdapter.swapCursor(data);

        super.onLoadFinished(loader, data);
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        super.onLoaderReset(loader);

        mAdapter.swapCursor(null);
    }

    protected void getHomeTimelineAsync(final long[] account_ids, final long[] max_ids, final long[] since_ids) {
        (new GetHomeTimelineTask(getActivity(), account_ids, max_ids, since_ids))
                .execute();
    }
}
