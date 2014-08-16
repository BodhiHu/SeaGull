package com.shawnhu.seagull.seagull.twitter.fragments;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;

import com.shawnhu.seagull.R;
import com.shawnhu.seagull.fragments.HomeFragment;
import com.shawnhu.seagull.seagull.twitter.adapters.StatusesAdapter;

import java.util.List;

import twitter4j.Status;

/**
 * Created by shawnhu on 8/16/14.
 */
public class SeagullHomeFragment extends HomeFragment implements HomeFragment.OnLoadMoreDataListener {
    private int mStartId = -1, mEndId = -1, mCurrentPosition = -1;
    private StatusesAdapter mAdapter  =
            new StatusesAdapter(getActivity(),
                    R.layout.status_item,
                    new Cursor(),
                    new String[] {},
                    new int[] {},
                    0);

    @Override
    public void onCreate(Bundle savedBundleInstance) {
        super.onCreate(savedBundleInstance);

        setOnLoadMoreDataListener(this);
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
            s = e = c = 0;
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

    private List<Status> getLocalStatues(int mStartId, int mEndId) {
        if (mStartId < 0 || mEndId < 0 || mEndId <= mStartId) {
            return null;
        }

        ContentResolver cr = getActivity().getApplicationContext().getContentResolver();
        CursorAdapter

        return null;
    }
}
