package com.shawnhu.seagull.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;

import com.etsy.android.grid.StaggeredGridView;
import com.shawnhu.seagull.R;
import com.shawnhu.seagull.widgets.SwipeRefreshLayout;

public abstract class SwipeRefreshStaggeredGridFragment extends Fragment
                                  implements SwipeRefreshLayout.OnRefreshListener {
    protected AbsListView mListView;
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected ProgressBar        mProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.swipe_to_refresh_list, container, false);

        mListView = (StaggeredGridView) v.findViewById(R.id.list_view);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mProgressBar = (ProgressBar) v.findViewById(R.id.progressBar);

        return v;
    }

    @Override
    public void onRefreshUp() {

    }
    @Override
    public void onRefreshDown() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

}
