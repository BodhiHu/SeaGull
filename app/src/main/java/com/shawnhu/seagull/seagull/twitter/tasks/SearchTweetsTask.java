package com.shawnhu.seagull.seagull.twitter.tasks;

import android.content.Context;

import com.shawnhu.seagull.seagull.twitter.model.ListResponse;
import com.shawnhu.seagull.tasks.ContextAsyncTask;

import java.util.Arrays;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import static com.shawnhu.seagull.seagull.twitter.utils.Utils.getTwitterInstance;

public class SearchTweetsTask extends ContextAsyncTask<Void, Void, ListResponse<Status>> {
    protected Context       mContext;
    protected long          mAccountId;
    protected String        mQuery;
    protected int           mPage;
    public SearchTweetsTask(Context context, long accountId, String query, int page) {
        super(context);
        mContext = context;
        mAccountId = accountId;
        mQuery = query;
        mPage = page;
    }

    @Override
    protected ListResponse<twitter4j.Status> doInBackground(final Void... params) {
        final Twitter twitter = getTwitterInstance(mContext, mAccountId, false);
        if (twitter != null) {
            try {
                Query query = new Query(mQuery);
                query.setPage(mPage);
                QueryResult queryResult = twitter.search(query);
                if (queryResult != null) {
                    return new ListResponse<twitter4j.Status>(Arrays.asList(queryResult.getStatuses()), null);
                }
            } catch (final TwitterException e) {
                return new ListResponse<twitter4j.Status>(null, e);
            }
        }
        return new ListResponse<twitter4j.Status>(null, null);
    }
}
