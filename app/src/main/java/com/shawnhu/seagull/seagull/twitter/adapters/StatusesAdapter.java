package com.shawnhu.seagull.seagull.twitter.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.SimpleCursorAdapter;

import com.shawnhu.seagull.R;
import com.shawnhu.seagull.adapters.PersistentCursorAdapter;
import com.shawnhu.seagull.seagull.twitter.providers.TweetStore;

public class StatusesAdapter extends PersistentCursorAdapter implements SimpleCursorAdapter.ViewBinder {
    static final public String[] PROJECTION = {
            TweetStore.Statuses._ID,
            TweetStore.Statuses.TEXT_PLAIN,
            TweetStore.Statuses.STATUS_TIMESTAMP,
            TweetStore.Statuses.USER_NAME,
            TweetStore.Statuses.USER_SCREEN_NAME,
            TweetStore.Statuses.USER_PROFILE_IMAGE_URL,
            TweetStore.Statuses.LOCATION,
    };

    public StatusesAdapter(Context context, Cursor c, int flags) {
        super(context,
                R.layout.status_item,
                c,
                PROJECTION,
                new int[] {
                        0,
                        R.id.status_text_plain,
                        R.id.status_timestamp,
                        R.id.status_username,
                        R.id.status_user_screenname,
                        R.id.status_user_profile_image,
                        R.id.status_location,
                },
                flags);

        setViewBinder(this);
    }

    @Override
    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
        return true;
    }

}
