package com.shawnhu.seagull.seagull.twitter.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;

import com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants;
import com.shawnhu.seagull.seagull.twitter.fragments.SeagullHomeFragment;
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

    public GraphPagerAdapter(FragmentActivity activity, long accountId, User user) {
        super(activity.getSupportFragmentManager());
        mContext = activity;
        mAccountId = accountId;
        mUser = user;
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
                Bundle args = new Bundle();
                args.putLong(SeagullTwitterConstants.EXTRA_ACCOUNT_ID, mAccountId);
                args.putLong(SeagullTwitterConstants.EXTRA_USER_ID, mUser.getId());
                return UserTimelineFragment.newInstance(args);
            }
            case TAB_POS_FOLLOWINGS: {
                return UsersFragment.newInstance(null);
            }
            case TAB_POS_FOLLOWERS: {
                return UsersFragment.newInstance(null);
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
