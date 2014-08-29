package com.shawnhu.seagull.seagull.twitter.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.shawnhu.seagull.seagull.twitter.model.ListResponse;

import java.util.Arrays;
import java.util.List;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import static com.shawnhu.seagull.seagull.twitter.utils.Utils.getTwitterInstance;

public class SearchTweetsTask extends AsyncTask<Void, Void, ListResponse<twitter4j.Status>>{
    protected Context       mContext;
    protected long          mAccountId;
    protected String        mQuery;
    protected int           mPage;
    public SearchTweetsTask(Context context, long accountId, String query, int page) {
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
                List<twitter4j.Status> statusesList;
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

    @Override
    protected void onPostExecute(final ListResponse<twitter4j.Status> result) {}
}
