package com.shawnhu.seagull.seagull.twitter.adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;

import com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants;
import com.shawnhu.seagull.seagull.twitter.fragments.UserTimelineFragment;
import com.shawnhu.seagull.seagull.twitter.fragments.UsersFragment;
import com.shawnhu.seagull.utils.NumberUtils;

import twitter4j.User;

public class GraphPagerAdapter extends FragmentPagerAdapter {
    static public final String TAB_TITLES[]         = {"Tweets", "Followings", "Followers"};
    static public final int    TAB_POS_TWEETS       = 0;
    static public final int    TAB_POS_FOLLOWINGS   = 1;
    static public final int    TAB_POS_FOLLOWERS    = 2;

    protected Context mContext;
    protected long    mAccountId = -1;
    protected User    mUser;
    protected UserTimelineFragment mUserTimelineFragment;
    protected UsersFragment mFollowingsFragment;
    protected UsersFragment mFollowersFragment;

    public GraphPagerAdapter(FragmentActivity activity, long accountId, User user) {
        super(activity.getSupportFragmentManager());
        mContext = activity;
        mAccountId = accountId;
        mUser = user;
        registerDataSetObserver(new DataSetObserver() {
            @Override public void onChanged() {
                if (mUser != null) {
                    if (mUserTimelineFragment != null) {
                        mUserTimelineFragment.setUserId(mUser.getId());
                    }
                    if (mFollowingsFragment != null) {
                        mFollowingsFragment.setUserIds(mAccountId, mUser.getId());
                    }
                    if (mFollowersFragment != null) {
                        mFollowersFragment.setUserIds(mAccountId, mUser.getId());
                    }
                }
            }
            @Override public void onInvalidated() {}
        });
    }

    public void setUser(User user) {
        mUser = user;

        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int pos) {
        pos %= TAB_TITLES.length;
        switch (pos) {
            case TAB_POS_TWEETS: {
                if (mUserTimelineFragment == null) {
                    Bundle args = new Bundle();
                    args.putLong(SeagullTwitterConstants.EXTRA_ACCOUNT_ID, mAccountId);
                    if (mUser != null) {
                        args.putLong(SeagullTwitterConstants.EXTRA_USER_ID, mUser.getId());
                    }
                    mUserTimelineFragment = UserTimelineFragment.newInstance(args);
                }
                return mUserTimelineFragment;
            }
            case TAB_POS_FOLLOWINGS: {
                if (mFollowingsFragment == null) {
                    Bundle args = new Bundle();
                    args.putBoolean(UsersFragment.EXTRA_FOLLOWINGS_FRAG, true);
                    args.putLong(SeagullTwitterConstants.EXTRA_ACCOUNT_ID, mAccountId);
                    if (mUser != null) {
                        args.putLong(SeagullTwitterConstants.EXTRA_USER_ID, mUser.getId());
                    }
                    mFollowingsFragment = UsersFragment.newInstance(args);
                }
                return mFollowingsFragment;
            }
            case TAB_POS_FOLLOWERS: {
                if (mFollowersFragment == null) {
                    Bundle args = new Bundle();
                    args.putBoolean(UsersFragment.EXTRA_FOLLOWINGS_FRAG, false);
                    args.putLong(SeagullTwitterConstants.EXTRA_ACCOUNT_ID, mAccountId);
                    if (mUser != null) {
                        args.putLong(SeagullTwitterConstants.EXTRA_USER_ID, mUser.getId());
                    }
                    mFollowersFragment = UsersFragment.newInstance(args);
                }
                return mFollowersFragment;
            }
        }
        return null;
    }

    @Override
    public int getCount() {
        return TAB_TITLES.length;
    }

    @Override
    public String getPageTitle(int pos) {
        pos %= TAB_TITLES.length;
        int prefix_num = 0;
        if (mUser != null) {
            switch (pos) {
                case TAB_POS_TWEETS:
                    prefix_num = mUser.getStatusesCount();
                    break;
                case TAB_POS_FOLLOWINGS:
                    prefix_num = mUser.getFriendsCount();
                    break;
                case TAB_POS_FOLLOWERS:
                    prefix_num = mUser.getFollowersCount();
                    break;
            }
        }

        prefix_num = prefix_num >= 0 ? prefix_num : 0;

        return NumberUtils.formatIntToRought(prefix_num) + " " + TAB_TITLES[pos % TAB_TITLES.length];
    }
}
