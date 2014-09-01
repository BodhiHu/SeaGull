package com.shawnhu.seagull.seagull.twitter.adapters;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;

import com.shawnhu.seagull.R;
import com.shawnhu.seagull.seagull.twitter.content.TweetStore;

final public class StatusesCursorAdapter extends SimpleCursorAdapter {

    static final public String[] PROJECTION  = TweetStore.Statuses.COLUMNS;
    static final public String   SELECTION   =
            "("                                                        +
                    "(" + TweetStore.Statuses.ACCOUNT_ID       + "=?)" +
            ")";
    static final public String   SORT_ORDER  = TweetStore.Statuses.STATUS_TIMESTAMP + " DESC";
    static final public Uri      CONTENT_URI = TweetStore.Statuses.CONTENT_URI;

    protected Fragment mHostFragment;
    protected Activity mHostActivity;

    public StatusesCursorAdapter(Context context, Cursor c, int flags, Fragment fragment) {
        super(context,
                R.layout.status_item,
                c,
                PROJECTION,
                null,
                flags);

        if (fragment == null) {
            throw new NullPointerException("Hosting Fragment can not be null");
        }

        mHostFragment = fragment;
    }
    public StatusesCursorAdapter(Context context, Cursor c, int flags, Activity activity) {
        super(context,
                R.layout.status_item,
                c,
                PROJECTION,
                null,
                flags);

        if (activity == null) {
            throw new NullPointerException("Hosting Activity can not be null");
        }

        mHostActivity = activity;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        StatusViewBuilder viewBuilder;
        if (mHostFragment != null) {
            viewBuilder = new StatusViewBuilder(mHostFragment);
        } else {
            viewBuilder = new StatusViewBuilder(mHostActivity);
        }

        if (cursor != null) {
            int account_id_idx = cursor.getColumnIndex(TweetStore.Statuses.ACCOUNT_ID);
            if (account_id_idx >= 0) {
                viewBuilder.buildStatusView(view, cursor.getLong(account_id_idx), cursor);
            }
        }
    }
}
