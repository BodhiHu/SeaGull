package com.shawnhu.seagull.seagull.twitter.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.widget.ListAdapter;

import com.shawnhu.seagull.R;
import com.shawnhu.seagull.fragments.HomeFragment;
import com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants;
import com.shawnhu.seagull.seagull.twitter.TwitterManager;
import com.shawnhu.seagull.seagull.twitter.adapters.StatusesAdapter;
import com.shawnhu.seagull.seagull.twitter.providers.TweetStore;
import com.shawnhu.seagull.seagull.twitter.utils.AsyncTwitterWrapper;

import java.security.InvalidParameterException;

/**
 * Created by shawnhu on 8/16/14.
 */
public class SeagullHomeFragment extends HomeFragment
        implements HomeFragment.OnLoadMoreDataListener, LoaderManager.LoaderCallbacks<Cursor> {

    static public SeagullHomeFragment getInstance(Bundle args) {
        SeagullHomeFragment fragment =  new SeagullHomeFragment();
        fragment.setArguments(args);
        return fragment;
    }



    public SeagullHomeFragment() {

    }

    private int mStartId = -1, mEndId = -1, mCurrentPosition = -1;
    private long mAccountId = -1;

    private StatusesAdapter mAdapter = new StatusesAdapter(getActivity(), null, 0);

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
    public void onSaveInstanceState(Bundle outState) {
        mAdapter.saveNow();
    }

    protected ListAdapter getListAdapter() {
        Integer s, e, c;
        try {
            s = Integer.valueOf(mAdapter.getSavedValue(StatusesAdapter.START_ID));
            e = Integer.valueOf(mAdapter.getSavedValue(StatusesAdapter.END_ID));
            c = Integer.valueOf(mAdapter.getSavedValue(StatusesAdapter.CURRENT_POSITION));
        } catch (NumberFormatException ne) {
            ne.printStackTrace();
            s = e = c = -1;
        }

        mStartId = s;
        mEndId = e;
        mCurrentPosition = c;
        return mAdapter;
    }
    protected int getContentViewId() {
        return R.layout.fragment_home;
    }
    protected int getCurrentPosition() {
        return mCurrentPosition;
    }

    @Override
    public void onLoadMoreStart() {

    }
    @Override
    public void onLoadMoreEnd() {

    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = TweetStore.Statuses.CONTENT_URI;
        String SELECTION = "((" + TweetStore.Statuses._ID + ">=" + mEndId + ") AND (" +
                                  TweetStore.Statuses._ID + "<=" + mStartId +
                           "))";

        CursorLoader cursorLoader =
                new CursorLoader(getActivity(),
                        TweetStore.Statuses.CONTENT_URI,
                        StatusesAdapter.PROJECTION,
                        SELECTION,
                        null,
                        TweetStore.Statuses._ID + " DESC"
                        );

        return cursorLoader;
    }
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() == 0) {
            AsyncTwitterWrapper.GetHomeTimelineTask task =
                    new AsyncTwitterWrapper.GetHomeTimelineTask(
                            new long[] {mAccountId},
                            new long[] {-1},
                            new long[] {-1}
                    );
            TwitterManager.getInstance()
                    .getAsyncTaskManager()
                    .add(task, true);
            return;
        }

        mAdapter.swapCursor(data);
    }
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
