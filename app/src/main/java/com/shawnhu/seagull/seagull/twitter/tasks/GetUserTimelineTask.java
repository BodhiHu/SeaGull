package com.shawnhu.seagull.seagull.twitter.tasks;

import android.content.Context;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class GetUserTimelineTask extends GetStatusesTask {
    private long mUserId = -1;
    public GetUserTimelineTask(Context context, final long[] account_ids, final long[] max_ids, final long[] since_ids, final long userId) {
        super(context, account_ids, max_ids, since_ids);

        mUserId = userId;
    }

    @Override
    public ResponseList<twitter4j.Status> getStatuses(Twitter twitter, Paging paging)
            throws TwitterException
    {
        if (mUserId == -1) {
            return twitter.getUserTimeline(paging);
        } else {
            return twitter.getUserTimeline(mUserId, paging);
        }
    }

}
