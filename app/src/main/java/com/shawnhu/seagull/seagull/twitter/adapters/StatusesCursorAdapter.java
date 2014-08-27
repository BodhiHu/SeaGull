package com.shawnhu.seagull.seagull.twitter.adapters;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
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

    public StatusesCursorAdapter(Context context, Cursor c, int flags) {
        super(context,
                R.layout.status_item,
                c,
                PROJECTION,
                null,
                flags);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        StatusViewBuilder.buildStatusView(view, context, cursor);
    }
}
