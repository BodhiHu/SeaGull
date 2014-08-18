package com.shawnhu.seagull.seagull.twitter.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants;
import com.shawnhu.seagull.seagull.twitter.model.TwitterStatusListResponse;
import com.shawnhu.seagull.seagull.twitter.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Created by shawn on 14-8-18.
 */
abstract public class GetStatusesTask extends AsyncTask<Void, Void, List<TwitterStatusListResponse>> {

    private final long[] mAccountIds, mMaxIds, mSinceIds;
    protected Context mContext;

    public GetStatusesTask(Context context, final long[] account_ids, final long[] max_ids, final long[] since_ids) {
        super();
        mAccountIds = account_ids;
        mMaxIds = max_ids;
        mSinceIds = since_ids;
        mContext = context;
    }

    public abstract ResponseList<twitter4j.Status> getStatuses(Twitter twitter, Paging paging)
            throws TwitterException;

    @Override
    protected List<TwitterStatusListResponse> doInBackground(final Void... params) {

        final List<TwitterStatusListResponse> result = new ArrayList<TwitterStatusListResponse>();

        if (mAccountIds == null) return result;

        int idx = 0;
        final int load_item_limit = SeagullTwitterConstants.DEFAULT_LOAD_ITEM_LIMIT;
        for (final long account_id : mAccountIds) {
            final Twitter twitter = Utils.getTwitterInstance(mContext, account_id, true);
            if (twitter != null) {
                try {
                    final Paging paging = new Paging();
                    paging.setCount(SeagullTwitterConstants.DEFAULT_LOAD_ITEM_LIMIT);
                    final long maxId, sinceId;
                    if (isMaxIdsValid() && mMaxIds[idx] > 0) {
                        maxId = mMaxIds[idx];
                        paging.setMaxId(maxId);
                    } else {
                        maxId = -1;
                    }
                    if (isSinceIdsValid() && mSinceIds[idx] > 0) {
                        sinceId = mSinceIds[idx];
                        paging.setSinceId(sinceId - 1);
                    } else {
                        sinceId = -1;
                    }
                    final List<twitter4j.Status> statuses = new ArrayList<twitter4j.Status>();
                    final boolean truncated = Utils.truncateStatuses(getStatuses(twitter, paging), statuses, sinceId);
                    result.add(new TwitterStatusListResponse(account_id, maxId, sinceId, load_item_limit, statuses,
                            truncated));
                } catch (final TwitterException e) {
                    result.add(new TwitterStatusListResponse(account_id, e));
                }
            }
            idx++;
        }
        return result;
    }

    final boolean isMaxIdsValid() {
        return mMaxIds != null && mMaxIds.length == mAccountIds.length;
    }

    final boolean isSinceIdsValid() {
        return mSinceIds != null && mSinceIds.length == mAccountIds.length;
    }
}

