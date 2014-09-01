package com.shawnhu.seagull.seagull.twitter.tasks;

import android.content.Context;

import com.shawnhu.seagull.seagull.twitter.model.Response;
import com.shawnhu.seagull.tasks.ContextAsyncTask;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

import static com.shawnhu.seagull.seagull.twitter.utils.Utils.getTwitterInstance;

public class CreateFriendshipTask extends ContextAsyncTask<Void, Void, Response<User>> {

    protected final long account_id;
    protected final long user_id;
    protected Context mContext;

    public CreateFriendshipTask(Context context, final long account_id, final long user_id) {
        super(context);
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
        if (twitter == null) return new Response<User>(null, null);
        try {
            final twitter4j.User user = twitter.createFriendship(user_id);
            return new Response<User>(user, null);
        } catch (final TwitterException e) {
            return new Response<User>(null, e);
        }
    }
}


