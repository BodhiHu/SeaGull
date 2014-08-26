package com.shawnhu.seagull.seagull.twitter.fragments;

import android.os.Bundle;

public class SeagullHomeFragment extends TwitterStatusesFragment {

    static public SeagullHomeFragment newInstance(Bundle args) {
        SeagullHomeFragment fragment = new SeagullHomeFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
