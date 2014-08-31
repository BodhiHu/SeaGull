package com.shawnhu.seagull.seagull.twitter.tasks;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.shawnhu.seagull.seagull.twitter.content.TweetStore;
import com.shawnhu.seagull.seagull.twitter.model.Response;
import com.shawnhu.seagull.tasks.ContextAsyncTask;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import static com.shawnhu.seagull.seagull.twitter.utils.Utils.getTwitterInstance;

public class RetweetStatusTask extends ContextAsyncTask<Void, Void, Response<Status>> {

    private final long account_id;

    private final long status_id;

    protected Context mContext;

    public RetweetStatusTask(Context context, final long account_id, final long status_id) {
        super(context);
        this.account_id = account_id;
        this.status_id = status_id;
        mContext = context;
    }

    @Override
    protected Response<twitter4j.Status> doInBackground(final Void... params) {

        if (account_id < 0) return new Response<twitter4j.Status>(null, null);

        final Twitter twitter = getTwitterInstance(mContext, account_id, false);
        if (twitter != null) {
            try {
                final twitter4j.Status status = twitter.retweetStatus(status_id);
                return new Response<twitter4j.Status>(status, null);
            } catch (final TwitterException e) {
                return new Response<twitter4j.Status>(null, e);
            }
        }
        return new Response<twitter4j.Status>(null, null);
    }

    @Override
    protected void onPostExecuteSafe(final Response<twitter4j.Status> result) {
        if (result.hasData() && result.getData().getId() > 0) {
            final ContentValues values = new ContentValues();
            values.put(TweetStore.Statuses.MY_RETWEET_ID, result.getData().getId());
            final String where = TweetStore.Statuses.STATUS_ID + " = " + status_id + " OR " + TweetStore.Statuses.RETWEET_ID + " = "
                    + status_id;
            for (final Uri uri : TweetStore.STATUSES_URIS) {
                mContext.getContentResolver().update(uri, values, where, null);
            }
        }
    }
}

