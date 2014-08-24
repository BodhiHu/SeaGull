package com.shawnhu.seagull.seagull.twitter.tasks;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.shawnhu.seagull.seagull.twitter.content.TweetStore;
import com.shawnhu.seagull.seagull.twitter.model.Response;
import com.shawnhu.seagull.seagull.twitter.model.TwitterStatus;
import com.shawnhu.seagull.seagull.twitter.utils.Utils;

import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Created by shawnhu on 8/24/14.
 */

public class CreateFavoriteTask
            extends AsyncTask<Void, Void, Response<TwitterStatus>> {

    private final long account_id, status_id;
    protected Context mContext;

    public CreateFavoriteTask(Context context, final long account_id, final long status_id) {
        mContext = context;
        this.account_id = account_id;
        this.status_id = status_id;
    }

    @Override
    protected Response<TwitterStatus> doInBackground(final Void... params) {
        if (account_id < 0) return new Response<TwitterStatus>(null, null);
        final Twitter twitter = Utils.getTwitterInstance(mContext, account_id, false);
        if (twitter == null) return new Response<TwitterStatus>(null, null);
        try {
            final twitter4j.Status status = twitter.createFavorite(status_id);
            final ContentValues values = new ContentValues();
            values.put(TweetStore.Statuses.IS_FAVORITE, true);
            final StringBuilder where = new StringBuilder();
            where.append(TweetStore.Statuses.ACCOUNT_ID + " = " + account_id);
            where.append(" AND ");
            where.append("(");
            where.append(TweetStore.Statuses.STATUS_ID + " = " + status_id);
            where.append(" OR ");
            where.append(TweetStore.Statuses.RETWEET_ID + " = " + status_id);
            where.append(")");
            for (final Uri uri : TweetStore.STATUSES_URIS) {
                mContext.getContentResolver().update(uri, values, where.toString(), null);
            }
            return new Response<TwitterStatus>(new TwitterStatus(status, account_id, false), null);
        } catch (final TwitterException e) {
            return new Response<TwitterStatus>(null, e);
        }
    }

    @Override
    protected void onPostExecute(final Response<TwitterStatus> result) {
    }

}
