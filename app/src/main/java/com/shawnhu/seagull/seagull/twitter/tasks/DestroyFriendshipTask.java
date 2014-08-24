package com.shawnhu.seagull.seagull.twitter.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.shawnhu.seagull.seagull.twitter.content.TweetStore;
import com.shawnhu.seagull.seagull.twitter.model.Response;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

import static com.shawnhu.seagull.seagull.twitter.utils.Utils.getTwitterInstance;

public class DestroyFriendshipTask extends AsyncTask<Void, Void, Response<User>> {

    private final long account_id;
    private final long user_id;
    protected Context mContext;

    public DestroyFriendshipTask(Context context, final long account_id, final long user_id) {
        this.account_id = account_id;
        this.user_id = user_id;
        mContext = context;
    }

    public long getAccountId() {
        return account_id;
    }

    public long getUserId() {
        return user_id;
    }

    @Override
    protected Response<User> doInBackground(final Void... params) {

        final Twitter twitter = getTwitterInstance(mContext, account_id, false);
        if (twitter != null) {
            try {
                final twitter4j.User user = twitter.destroyFriendship(user_id);
                final String where = TweetStore.Statuses.ACCOUNT_ID + " = " + account_id + " AND " + TweetStore.Statuses.USER_ID + " = "
                        + user_id;
                mContext.getContentResolver().delete(TweetStore.Statuses.CONTENT_URI, where, null);
                return new Response<User>(user, null);
            } catch (final TwitterException e) {
                return new Response<User>(null, e);
            }
        }
        return new Response<User>(null, null);
    }

    @Override
    protected void onPostExecute(final Response<User> result) {}

}

