package com.shawnhu.seagull.seagull.twitter.fragments;

import android.os.Bundle;

import com.shawnhu.seagull.fragments.SwipeRefreshStaggeredGridFragment;

public class UsersFragment extends SwipeRefreshStaggeredGridFragment {
    static public UsersFragment newInstance(Bundle args) {
        UsersFragment fragment = new UsersFragment();
        fragment.setArguments(args);

        return fragment;
    }

    public UsersFragment() {}
}
