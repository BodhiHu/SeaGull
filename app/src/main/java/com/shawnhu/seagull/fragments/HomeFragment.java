package com.shawnhu.seagull.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListAdapter;

import com.shawnhu.seagull.R;

//TODO: should use ListFragment
public abstract class HomeFragment extends Fragment implements AbsListView.OnScrollListener {

    abstract protected ListAdapter getListAdapter();
    abstract protected int         getContentViewId();
    abstract protected int getFirstVisibleItemId();

    protected AbsListView mListView;
    protected ListAdapter mAdapter;
    protected OnLoadMoreDataListener mOnLoadMoreDataListener;

    protected void setOnLoadMoreDataListener(OnLoadMoreDataListener l) {
        mOnLoadMoreDataListener = l;
    }

    /*
    public HomeFragment() {
        // Required empty public constructor
    }
    */

    @Override
    public void onAttach(Activity activity) {
        mListView.setAdapter(mAdapter);
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(getContentViewId(), container, false);
        if (v == null) {
            throw new NullPointerException("can not inflate view");
        }

        mListView = (AbsListView) v.findViewById(R.id.list_view);
        if (mListView == null) {
            throw new NullPointerException("can not find AbsListView");
        }

        mAdapter = getListAdapter();
        if (mAdapter == null) {
            throw new NullPointerException("ListAdapter is null");
        }

        mListView.setAdapter(mAdapter);
        mListView.setSelection(getFirstVisibleItemId());
        mListView.setOnScrollListener(this);
        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /** BEGIN OnScrollListener ********************************************************************/
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {}
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                         int totalItemCount) {
        if (mOnLoadMoreDataListener != null) {
            if (firstVisibleItem <= 0) {
                mOnLoadMoreDataListener.onLoadMoreHead();
            } else if (firstVisibleItem >= (mAdapter.getCount() - 1)) {
                mOnLoadMoreDataListener.onLoadMoreTail();
            }
        }
    }
    /** END   OnScrollListener ********************************************************************/

    public interface OnLoadMoreDataListener {
        public void onLoadMoreHead();
        public void onLoadMoreTail();
    }
}
