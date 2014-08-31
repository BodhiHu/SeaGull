package com.shawnhu.seagull.seagull.twitter.tasks;

import android.content.Context;

import com.shawnhu.seagull.seagull.twitter.model.Response;
import com.shawnhu.seagull.tasks.ContextAsyncTask;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

import static com.shawnhu.seagull.seagull.twitter.utils.Utils.getTwitterInstance;

/**
 * Created by shawn on 14-8-25.
 */
public class GetUserProfileTask extends ContextAsyncTask<Void, Void, Response<User>> {
    protected Context   mContext;
    protected long      mAccountId;
    protected long      mUserId;
    public GetUserProfileTask(Context context, long accountId, long userId) {
        super(context);
        mContext = context;
        mAccountId = accountId;
        mUserId = userId;
    }

    @Override
    protected Response<twitter4j.User> doInBackground(final Void... params) {
        if (mUserId < 0) return new Response<twitter4j.User>(null, null);

        final Twitter twitter = getTwitterInstance(mContext, mAccountId, false);
        if (twitter != null) {
            try {
                final twitter4j.User user = twitter.showUser(mUserId);
                return new Response<twitter4j.User>(user, null);
            } catch (final TwitterException e) {
                return new Response<twitter4j.User>(null, e);
            }
        }
        return new Response<twitter4j.User>(null, null);
    }

}
