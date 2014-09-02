package com.shawnhu.seagull.seagull.twitter.fragments;

import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.etsy.android.grid.StaggeredGridView;
import com.shawnhu.seagull.fragments.SwipeRefreshStaggeredGridFragment;
import com.shawnhu.seagull.seagull.activities.ShowUserActivity;
import com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants;
import com.shawnhu.seagull.seagull.twitter.adapters.OnShowUser;
import com.shawnhu.seagull.seagull.twitter.adapters.UsersArrayAdapter;
import com.shawnhu.seagull.seagull.twitter.model.ListResponse;
import com.shawnhu.seagull.seagull.twitter.tasks.GetUsersTask;

import java.util.ArrayList;
import java.util.List;

import twitter4j.CursorPaging;
import twitter4j.PagableResponseList;
import twitter4j.User;

public class UsersFragment extends SwipeRefreshStaggeredGridFragment {
    static public UsersFragment newInstance(Bundle args) {
        UsersFragment fragment = new UsersFragment();
        fragment.setArguments(args);

        return fragment;
    }

    public UsersFragment() {}

    public static final String EXTRA_FOLLOWINGS_FRAG = "EXTRA_FOLLOWINGS_FRAG";
    protected long                  mAccountId       = -1;
    protected long                  mUserId          = -1;
    protected boolean               mIsFriendsFragm  = true;
    protected UsersArrayAdapter     mUsersAdapter;
    protected long                  mPrevCursor = -1;
    protected long                  mNextCursor = -1;
    final static protected int      PAGING_COUNT = 20;

    public void setUpFragment(long account_id, long user_id) {
        mAccountId = account_id;
        mUserId = user_id;
        if (mUserId >= 0 && mAccountId >= 0) {
            loadUsersAsync(PAGING_COUNT, -1, true);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mUsersAdapter = new UsersArrayAdapter(getActivity(), new ArrayList<User>(0), this);
    }

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        Bundle args = getArguments();
        if (args != null) {
            mIsFriendsFragm = args.getBoolean(EXTRA_FOLLOWINGS_FRAG, true);
            mAccountId      = args.getLong(SeagullTwitterConstants.EXTRA_ACCOUNT_ID, -1);
            mUserId         = args.getLong(SeagullTwitterConstants.EXTRA_USER_ID, -1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        if (mIsFriendsFragm) {
            v.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
        } else {
            v.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark));
        }

        ((StaggeredGridView) mListView).setColumnCount(1);
        mListView.setAdapter(mUsersAdapter);

        mUsersAdapter.registerShowUserListener(new OnShowUser() {
            @Override
            public void onShowUser(User user) {
                if (user != null && getActivity() != null) {
                    Intent i = new Intent(getActivity(), ShowUserActivity.class);
                    i.putExtra(SeagullTwitterConstants.EXTRA_ACCOUNT_ID, mAccountId);
                    i.putExtra(SeagullTwitterConstants.EXTRA_USER_ID,    user.getId());
                    getActivity().startActivity(i);
                }
            }
        });

        loadUsersAsync(PAGING_COUNT, -1, true);

        return v;
    }

    @Override
    public void onRefreshUp() {
        loadUsersAsync(PAGING_COUNT, mPrevCursor, false);
    }

    @Override
    public void onRefreshDown() {
        mProgressBar.setVisibility(View.VISIBLE);
        loadUsersAsync(PAGING_COUNT, mNextCursor, true);
    }

    private void loadUsersAsync(int count, long cursor, final boolean insertAtEnd) {
        if (count < 1 || (cursor != -1 && cursor < 1)) {
            mSwipeRefreshLayout.setRefreshing(false);
            return;
        }

        if (mAccountId == -1 || mUserId == -1 || getActivity() == null) {
            mSwipeRefreshLayout.setRefreshing(false);
            return;
        }

        CursorPaging paging = new CursorPaging();
        count = count > PAGING_COUNT ? PAGING_COUNT : count;
        if (!insertAtEnd) {
            cursor -= PAGING_COUNT;
            cursor = cursor >= 1 ? cursor : 1;
            count = cursor >= count ? count : (int) cursor;
        }

        GetUsersTask getUsersTask =
        new GetUsersTask(getActivity(),
                mAccountId,
                mUserId,
                mIsFriendsFragm,
                new CursorPaging(cursor, count))
        {
            @Override
            protected void onPostExecuteSafe(final ListResponse<User> result) {
                if (result != null) {
                    List<User> usersList = result.getList();

                    if (usersList instanceof PagableResponseList
                        && mUsersAdapter != null
                        && usersList.size() > 0) {

                        long pre_cursor = ((PagableResponseList) usersList).getPreviousCursor();
                        long nxt_cursor = ((PagableResponseList) usersList).getNextCursor();
                        if (insertAtEnd) {
                            mUsersAdapter.addAll(usersList);

                            mNextCursor = nxt_cursor >= 1 ? nxt_cursor : 1;
                            mPrevCursor = nxt_cursor - mUsersAdapter.getCount() -1;
                            mPrevCursor = mPrevCursor >= 1 ? mPrevCursor : 1;
                        } else {
                            for (int i = usersList.size()-1; i >= 0; i--) {
                                mUsersAdapter.insert(usersList.get(i), 0);
                            }

                            mPrevCursor = pre_cursor >= 1? pre_cursor : 1;
                            mNextCursor = pre_cursor + mUsersAdapter.getCount() + 1;
                        }
                    }
                }

                mSwipeRefreshLayout.setRefreshing(false);
                mProgressBar.setVisibility(View.GONE);
            }
        };

        getUsersTask.setHost(this);
        getUsersTask.execute();
    }
}
