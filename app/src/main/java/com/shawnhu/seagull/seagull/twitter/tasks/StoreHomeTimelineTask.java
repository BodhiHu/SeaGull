package com.shawnhu.seagull.seagull.twitter.tasks;

import android.content.Context;
import android.os.Bundle;

import com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants;
import com.shawnhu.seagull.seagull.twitter.content.TweetStore;
import com.shawnhu.seagull.seagull.twitter.model.Response;
import com.shawnhu.seagull.seagull.twitter.model.TwitterStatusListResponse;

import java.util.List;

/**
 * Created by shawnhu on 8/20/14.
 */

class StoreHomeTimelineTask
        extends StoreStatusesTask {

    public StoreHomeTimelineTask(Context context, final List<TwitterStatusListResponse> result, final boolean notify) {
        super(context, result, TweetStore.Statuses.CONTENT_URI, notify);
    }

    @Override
    protected void onPostExecute(final Response<Bundle> response) {
        final boolean succeed =
                response != null    &&
                response.hasData()  &&
                response.getData().getBoolean(SeagullTwitterConstants.EXTRA_SUCCEED);
        final Bundle extras = new Bundle();
        extras.putBoolean(SeagullTwitterConstants.EXTRA_SUCCEED, succeed);
        super.onPostExecute(response);
    }
}
