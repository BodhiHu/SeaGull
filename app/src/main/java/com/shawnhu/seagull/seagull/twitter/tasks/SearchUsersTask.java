package com.shawnhu.seagull.seagull.twitter.tasks;

import android.content.Context;
import android.os.*;

import com.shawnhu.seagull.seagull.twitter.model.ListResponse;

import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

import static com.shawnhu.seagull.seagull.twitter.utils.Utils.getTwitterInstance;

/**
 * Created by shawn on 14-8-29.
 */
public class SearchUsersTask extends android.os.AsyncTask<Void, Void, ListResponse<User>> {

    protected Context       mContext;
    protected long          mAccountId;
    protected String        mQuery;
    protected int           mPage;
    public SearchUsersTask(Context context, long accountId, String query, int page) {
        mContext = context;
        mAccountId = accountId;
        mQuery = query;
        mPage = page;
    }

    @Override
    protected ListResponse<User> doInBackground(final Void... params) {
        final Twitter twitter = getTwitterInstance(mContext, mAccountId, false);
        if (twitter != null) {
            try {
                List<User> usersList = twitter.searchUsers(mQuery, mPage);
                return new ListResponse<twitter4j.User>(usersList, null);
            } catch (final TwitterException e) {
                return new ListResponse<twitter4j.User>(null, e);
            }
        }
        return new ListResponse<twitter4j.User>(null, null);
    }

    @Override
    protected void onPostExecute(final ListResponse<twitter4j.User> result) {}
}
