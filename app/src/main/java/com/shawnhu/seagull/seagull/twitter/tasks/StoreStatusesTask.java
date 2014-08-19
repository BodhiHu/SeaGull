package com.shawnhu.seagull.seagull.twitter.tasks;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants;
import com.shawnhu.seagull.seagull.twitter.content.TweetStore;
import com.shawnhu.seagull.seagull.twitter.model.Response;
import com.shawnhu.seagull.seagull.twitter.model.TwitterStatusListResponse;
import com.shawnhu.seagull.seagull.twitter.utils.ContentValuesCreator;
import com.shawnhu.seagull.seagull.twitter.utils.NameValuePairImpl;
import com.shawnhu.seagull.seagull.twitter.utils.content.ContentResolverUtils;
import com.shawnhu.seagull.utils.ArrayUtils;
import com.shawnhu.seagull.utils.querybuilder.Columns;
import com.shawnhu.seagull.utils.querybuilder.RawItemArray;
import com.shawnhu.seagull.utils.querybuilder.Where;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.shawnhu.seagull.seagull.twitter.utils.Utils.appendQueryParameters;
import static com.shawnhu.seagull.seagull.twitter.utils.Utils.getAllStatusesIds;
import static com.shawnhu.seagull.seagull.twitter.utils.Utils.getStatusIdsInDatabase;

/**
 * Created by shawnhu on 8/20/14.
 */
abstract class StoreStatusesTask
        extends AsyncTask<Void, Void, Response<Bundle>> {

    private final List<TwitterStatusListResponse> responses;
    private final Uri uri;
    private final ArrayList<ContentValues> all_statuses = new ArrayList<ContentValues>();
    private final boolean notify;
    protected Context mContext;
    protected ContentResolver mResolver;

    public StoreStatusesTask(Context context, final List<TwitterStatusListResponse> result,
                             final Uri uri, final boolean notify) {
        super();
        mContext = context;
        responses = result;
        mResolver = mContext.getContentResolver();
        this.uri = uri;
        this.notify = notify;
    }

    @Override
    protected Response<Bundle> doInBackground(final Void... args) {
        boolean succeed = false;
        for (final TwitterStatusListResponse response : responses) {
            final long account_id = response.account_id;
            final List<twitter4j.Status> statuses = response.getList();
            if (statuses == null || statuses.isEmpty()) {
                continue;
            }
            final ArrayList<Long> ids_in_db = getStatusIdsInDatabase(mContext, uri, account_id);
            final boolean noItemsBefore = ids_in_db.isEmpty();
            final ContentValues[] values = new ContentValues[statuses.size()];
            final long[] statusIds = new long[statuses.size()];
            for (int i = 0, j = statuses.size(); i < j; i++) {
                final twitter4j.Status status = statuses.get(i);
                values[i] = ContentValuesCreator.makeStatusContentValues(status, account_id);
                statusIds[i] = status.getId();
            }
            // Delete all rows conflicting before new data inserted.
            final Where accountWhere = Where.equals(TweetStore.Statuses.ACCOUNT_ID, account_id);
            final Where statusWhere = Where.in(new Columns.Column(TweetStore.Statuses.STATUS_ID), new RawItemArray(statusIds));
            final String deleteWhere = Where.and(accountWhere, statusWhere).getSQL();
            final Uri deleteUri = appendQueryParameters(uri, new NameValuePairImpl(SeagullTwitterConstants.QUERY_PARAM_NOTIFY, false));
            final int rowsDeleted = mResolver.delete(deleteUri, deleteWhere, null);
            all_statuses.addAll(Arrays.asList(values));
            // Insert previously fetched items.
            final Uri insertUri = appendQueryParameters(uri, new NameValuePairImpl(SeagullTwitterConstants.QUERY_PARAM_NOTIFY, notify));
            ContentResolverUtils.bulkInsert(mResolver, insertUri, values);

            // Insert a gap.
            final long min_id = statusIds.length != 0 ? ArrayUtils.min(statusIds) : -1;
            final boolean deletedOldGap = rowsDeleted > 0 && ArrayUtils.contains(statusIds, response.max_id);
            final boolean noRowsDeleted = rowsDeleted == 0;
            final boolean insertGap = min_id > 0 && (noRowsDeleted || deletedOldGap) && !response.truncated
                    && !noItemsBefore && statuses.size() > 1;
            if (insertGap) {
                final ContentValues gap_value = new ContentValues();
                gap_value.put(TweetStore.Statuses.IS_GAP, 1);
                final StringBuilder where = new StringBuilder();
                where.append(TweetStore.Statuses.ACCOUNT_ID + " = " + account_id);
                where.append(" AND " + TweetStore.Statuses.STATUS_ID + " = " + min_id);
                final Uri update_uri = appendQueryParameters(uri, new NameValuePairImpl(SeagullTwitterConstants.QUERY_PARAM_NOTIFY, true));
                mResolver.update(update_uri, gap_value, where.toString(), null);
            }
            succeed = true;
        }
        final Bundle bundle = new Bundle();
        bundle.putBoolean(SeagullTwitterConstants.EXTRA_SUCCEED, succeed);
        getAllStatusesIds(mContext, uri);
        return new Response<Bundle>(bundle, null);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        final TwitterStatusListResponse[] array = new TwitterStatusListResponse[responses.size()];
        new CacheUsersStatusesTask(mContext, responses.toArray(array)).execute();
    }
}


