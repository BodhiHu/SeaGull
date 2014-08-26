package com.shawnhu.seagull.seagull.twitter.tasks;

import android.content.Context;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class GetUserTimelineTask extends GetStatusesTask {
    private long[] mUserIds;
    public GetUserTimelineTask(Context context, final long[] account_ids, final long[] max_ids, final long[] since_ids, final long[] userIds) {
        super(context, account_ids, max_ids, since_ids);

        mUserIds = userIds;
    }

    @Override
    public ResponseList<twitter4j.Status> getStatuses(Twitter twitter, Paging paging)
            throws TwitterException
    {
        //we only care the first account
        if (mUserIds[0] == -1) {
            return twitter.getUserTimeline(paging);
        } else {
            return twitter.getUserTimeline(mUserIds[0], paging);
        }
    }

}
