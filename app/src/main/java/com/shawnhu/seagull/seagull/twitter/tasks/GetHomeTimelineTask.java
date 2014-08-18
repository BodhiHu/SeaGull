package com.shawnhu.seagull.seagull.twitter.tasks;

import android.content.Context;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Created by shawn on 14-8-18.
 */
public class GetHomeTimelineTask extends GetStatusesTask {

    static final public String TAG = "GetHomeTimelineTask";
    public GetHomeTimelineTask(Context context, final long[] account_ids, final long[] max_ids, final long[] since_ids) {
        super(context, account_ids, max_ids, since_ids);
    }

    @Override
    public ResponseList<twitter4j.Status> getStatuses(final Twitter twitter, final Paging paging)
            throws TwitterException {
        return twitter.getHomeTimeline(paging);
    }
}
