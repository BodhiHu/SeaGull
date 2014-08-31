package com.shawnhu.seagull.seagull.twitter.tasks;

import android.content.Context;

import com.shawnhu.seagull.seagull.twitter.model.ListResponse;
import com.shawnhu.seagull.tasks.ContextAsyncTask;

import java.util.List;

import twitter4j.CursorPaging;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

import static com.shawnhu.seagull.seagull.twitter.utils.Utils.getTwitterInstance;

public class GetUsersTask  extends ContextAsyncTask<Void, Void, ListResponse<User>> {

    protected Context       mContext;
    protected long          mAccountId;
    protected long          mUserId;
    protected boolean       mGetFriendsList;
    protected CursorPaging  mPaging;
    public GetUsersTask(Context context, long accountId, long userId, boolean shouldGetFriends, CursorPaging paging) {
        super(context);
        mContext = context;
        mAccountId = accountId;
        mUserId = userId;
        mGetFriendsList = shouldGetFriends;
        mPaging = paging;
    }

    @Override
    protected ListResponse<twitter4j.User> doInBackground(final Void... params) {
        if (mUserId < 0) return new ListResponse<twitter4j.User>(null, null);

        final Twitter twitter = getTwitterInstance(mContext, mAccountId, false);
        if (twitter != null) {
            try {
                List<User> usersList;
                if (mGetFriendsList) {
                    usersList = twitter.getFriendsList(mUserId, mPaging);
                } else {
                    usersList = twitter.getFollowersList(mUserId, mPaging);
                }
                return new ListResponse<twitter4j.User>(usersList, null);
            } catch (final TwitterException e) {
                return new ListResponse<twitter4j.User>(null, e);
            }
        }
        return new ListResponse<twitter4j.User>(null, null);
    }

}
