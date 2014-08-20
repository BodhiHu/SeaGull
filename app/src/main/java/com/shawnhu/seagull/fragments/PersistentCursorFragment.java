package com.shawnhu.seagull.fragments;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListAdapter;

import com.shawnhu.seagull.R;
import com.shawnhu.seagull.lang.Persistent;
import com.shawnhu.seagull.utils.PreferencesUtils;

import java.util.LinkedHashMap;

//TODO: should use ListFragmentz?
public abstract class PersistentCursorFragment extends Fragment
        implements AbsListView.OnScrollListener, Persistent, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String _ID = "_id";

    abstract protected ListAdapter getListAdapter();
    abstract protected int         getContentViewId();

    protected AbsListView mListView;
    protected ListAdapter mAdapter;
    private OnLoadMoreDataListener mOnLoadMoreDataListener;

    private int mCurrentVisibleItemId = Integer.valueOf(__DEFAULT_V);

    public PersistentCursorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
        }

        __PERSISTENT_MAP.put(__CURRENT_VISIBLE_ITEM_ID, __DEFAULT_V);
        restoreNow();
        try {
            mCurrentVisibleItemId = Integer.valueOf(getValue(__CURRENT_VISIBLE_ITEM_ID));
        } catch (NumberFormatException e) {
            e.printStackTrace();
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
        mListView.setOnScrollListener(this);
        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        setValue(__CURRENT_VISIBLE_ITEM_ID, String.valueOf(mCurrentVisibleItemId));
        saveNow();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    protected void setOnLoadMoreDataListener(OnLoadMoreDataListener l) {
        mOnLoadMoreDataListener = l;
    }

    /** OnScrollListener **************************************************************************/
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {}
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                         int totalItemCount) {
        if (firstVisibleItem <= 0) {
            Cursor c = (Cursor) mListView.getItemAtPosition(mListView.getFirstVisiblePosition());
            if (c != null) {
                mCurrentVisibleItemId = c.getInt(c.getColumnIndex(_ID));

                if (mOnLoadMoreDataListener != null) {
                    mOnLoadMoreDataListener.onLoadMoreHead();
                }
            }
        } else if (firstVisibleItem >= (mAdapter.getCount() - 1)) {
            Cursor c = (Cursor) mListView.getItemAtPosition(mListView.getLastVisiblePosition());
            if (c != null) {
                mCurrentVisibleItemId = c.getInt(c.getColumnIndex(_ID));

                if (mOnLoadMoreDataListener != null) {
                    mOnLoadMoreDataListener.onLoadMoreTail();
                }
            }
        }
    }

    /** LoaderCallbacks ***************************************************************************/
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int pos = -1;
        do {
            int tmp_pos = data.getPosition();
            if (tmp_pos == mCurrentVisibleItemId) {
                pos = tmp_pos;
                break;
            }
        } while (data.moveToNext());

        if (pos >= 0) {
            mListView.setSelection(pos);
        }
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Cursor c = (Cursor) mListView.getItemAtPosition(mListView.getFirstVisiblePosition());
        mCurrentVisibleItemId = c.getInt(c.getColumnIndex(_ID));//TODO: save

        //mAdapter.setValue(StatusesCursorAdapter.CURRENT_VISIBLE_ITEM_ID, String.valueOf(mCurrentVisibleItemId));
        //mAdapter.saveNow();
    }

    /** Persistent*********************************************************************************/
    protected LinkedHashMap<String, String> __PERSISTENT_MAP            = new LinkedHashMap<String, String>();
    static public final String              __CURRENT_VISIBLE_ITEM_ID   = "__CURRENT_VISIBLE_ITEM_ID";
    final private String                    __PREFERENCE_NAME           = "Preferences de " + ((Object) this).getClass().getName();
    static final public String              __DEFAULT_V                 = "-1";
    @Override
    public void saveNow() {
        PreferencesUtils.savePreferencesMap(getActivity(), __PREFERENCE_NAME, __PERSISTENT_MAP);
    }
    @Override
    public void restoreNow() {
        PreferencesUtils.readPreferencesToMap(getActivity(), __PREFERENCE_NAME, __PERSISTENT_MAP, __DEFAULT_V);
    }
    @Override
    public void setValue(String key, String v) {
        if (__PERSISTENT_MAP != null) {
            __PERSISTENT_MAP.put(key, v);
        }
    }
    @Override
    public String getValue(String key) {
        if (__PERSISTENT_MAP != null) {
            return __PERSISTENT_MAP.get(key);
        }

        return __DEFAULT_V;
    }
    /** */


    public interface OnLoadMoreDataListener {
        public void onLoadMoreHead();
        public void onLoadMoreTail();
    }
}

