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

    private int mHeadItemId = -1, mTailItemId = -1, mCurrentPosition = -1;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = super.onCreateView(inflater, container, savedInstanceState);
        getLoaderManager().initLoader(0, null, this);

        return v;
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        mCurrentPosition = mListView.getSelectedItemPosition();
        Cursor c = mAdapter.getCursor();
        if (c != null) {
            c.moveToFirst();
            mHeadItemId = c.getInt(c.getColumnIndex(TweetStore.Statuses._ID));
            c.moveToLast();
            mTailItemId = c.getInt(c.getColumnIndex(TweetStore.Statuses._ID));

        }
        mAdapter.setValue(StatusesAdapter._ID_OF_HEAD_ITEM, String.valueOf(mHeadItemId));
        mAdapter.setValue(StatusesAdapter._ID_OF_TAIL_ITEM, String.valueOf(mTailItemId));
        mAdapter.setValue(StatusesAdapter.CURRENT_POSITION, String.valueOf(mCurrentPosition));
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mAdapter.saveNow();
        getLoaderManager().destroyLoader(0);
    }

    protected ListAdapter getListAdapter() {
        Integer s, e, c;
        try {
            s = Integer.valueOf(mAdapter.getValue(StatusesAdapter._ID_OF_HEAD_ITEM));
            e = Integer.valueOf(mAdapter.getValue(StatusesAdapter._ID_OF_TAIL_ITEM));
            c = Integer.valueOf(mAdapter.getValue(StatusesAdapter.CURRENT_POSITION));
        } catch (NumberFormatException ne) {
            ne.printStackTrace();
            s = e = c = -1;
        }

        mHeadItemId = s;
        mTailItemId = e;
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
    public void onLoadMoreHead() {
        long maxId;
        AsyncTwitterWrapper.GetHomeTimelineTask task =
                new AsyncTwitterWrapper.GetHomeTimelineTask(
                        new long[] {mAccountId},
                        new long[] {-1},
                        new long[] {-1}
                );
        TwitterManager.getInstance()
                .getAsyncTaskManager()
                .add(task, true);
    }
    @Override
    public void onLoadMoreTail() {
        AsyncTwitterWrapper.GetHomeTimelineTask task =
                new AsyncTwitterWrapper.GetHomeTimelineTask(
                        new long[] {mAccountId},
                        new long[] {-1},
                        new long[] {-1}
                );
        TwitterManager.getInstance()
                .getAsyncTaskManager()
                .add(task, true);
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mHeadItemId < 0 || mTailItemId < 0) {
            return null;
        }

        CursorLoader cursorLoader =
                new CursorLoader(getActivity(),
                        StatusesAdapter.CONTENT_URI,
                        StatusesAdapter.PROJECTION,
                        StatusesAdapter.SELECTION,
                        new String[] {
                                String.valueOf(mTailItemId),
                                String.valueOf(mHeadItemId),
                        },
                        StatusesAdapter.SORT_ORDER
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
