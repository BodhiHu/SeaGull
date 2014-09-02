package com.shawnhu.seagull.seagull.twitter.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.etsy.android.grid.StaggeredGridView;
import com.shawnhu.seagull.fragments.SwipeRefreshStaggeredGridFragment;
import com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants;
import com.shawnhu.seagull.seagull.twitter.adapters.StatusesArrayAdapter;
import com.shawnhu.seagull.seagull.twitter.model.TwitterStatusListResponse;
import com.shawnhu.seagull.seagull.twitter.tasks.GetUserTimelineTask;

import java.util.List;

import twitter4j.Status;

public class UserTimelineFragment extends SwipeRefreshStaggeredGridFragment {
    public static UserTimelineFragment newInstance(Bundle args) {
        UserTimelineFragment fragment = new UserTimelineFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public UserTimelineFragment() {}

    protected long                  mAccountId       = -1;
    protected long                  mUserId          = -1;
    protected StatusesArrayAdapter  mStatusesAdapter;

    public void setUserId(long userId) {
        mUserId = userId >= 0 ? userId : -1;
        getUserTimelineAsync(-1, -1, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mStatusesAdapter = new StatusesArrayAdapter(getActivity(), this);
    }
    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        Bundle args = getArguments();
        if (args != null) {
            mAccountId = args.getLong(SeagullTwitterConstants.EXTRA_ACCOUNT_ID, -1);
            mUserId = args.getLong(SeagullTwitterConstants.EXTRA_USER_ID, -1);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        ((StaggeredGridView) mListView).setColumnCount(1);
        mListView.setAdapter(mStatusesAdapter);

        //just get the latest 20 ones
        getUserTimelineAsync(-1, -1, false);

        return v;
    }

    @Override
    public void onRefreshUp() {
        long since_id = -1;
        if (mStatusesAdapter != null) {
            if (mStatusesAdapter.getCount() > 0) {
                Status status = mStatusesAdapter.getItem(0);
                since_id = status.getId();
            }
            getUserTimelineAsync(-1, since_id, true);
        } else {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
    @Override
    public void onRefreshDown() {
        long max_id = -1;
        if (mStatusesAdapter != null) {
            if (mStatusesAdapter.getCount() > 0) {
                Status status = mStatusesAdapter.getItem(mStatusesAdapter.getCount() - 1);
                max_id = status.getId();
            }
            mProgressBar.setVisibility(View.VISIBLE);
            getUserTimelineAsync(max_id-1, -1, false);
        } else {
            mSwipeRefreshLayout.setRefreshing(false);
        }

    }

    protected void getUserTimelineAsync(final long max_id, final long since_id, boolean insertAtStart) {
        if (getView() == null) {
            return;
        }

        if (mAccountId  == -1 || mUserId == -1) {
            mSwipeRefreshLayout.setRefreshing(false);
            return;
        }

        getUserTimelineAsync(new long[]{mAccountId},
                new long[]{max_id},
                new long[]{since_id},
                new long[]{mUserId}, insertAtStart);

    }
    private void getUserTimelineAsync(final long[] account_ids, final long[] max_ids, final long[] since_ids, final long[] user_ids, final boolean insertAtStart) {
        GetUserTimelineTask getUserTimelineTask =
        new GetUserTimelineTask(getActivity(), account_ids, max_ids, since_ids, user_ids) {
            @Override
            protected void onPostExecuteSafe(final List<TwitterStatusListResponse> responses) {
                if (responses != null && responses.size() > 0) {
                    for (TwitterStatusListResponse response : responses) {
                        List<twitter4j.Status> statuses = response.getList();
                        if (insertAtStart) {
                            for (int i = statuses.size()-1; i >= 0; i--) {
                                mStatusesAdapter.insert(statuses.get(i), 0);
                            }
                        } else {
                            mStatusesAdapter.addAll(statuses);
                        }
                    }
                }

                mSwipeRefreshLayout.setRefreshing(false);
                mProgressBar.setVisibility(View.GONE);
            }
        };

        getUserTimelineTask.setHost(this);
        getUserTimelineTask.execute();
    }

}
