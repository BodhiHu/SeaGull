package com.shawnhu.seagull.seagull.twitter.tasks;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

import com.shawnhu.seagull.seagull.twitter.content.TweetStore;
import com.shawnhu.seagull.seagull.twitter.model.TwitterListResponse;
import com.shawnhu.seagull.tasks.ContextAsyncTask;
import com.shawnhu.seagull.utils.querybuilder.Where;
import com.twitter.Extractor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import twitter4j.User;

import static com.shawnhu.seagull.seagull.twitter.utils.ContentValuesCreator.makeCachedUserContentValues;
import static com.shawnhu.seagull.seagull.twitter.utils.ContentValuesCreator.makeStatusContentValues;
import static com.shawnhu.seagull.seagull.twitter.utils.content.ContentResolverUtils.bulkDelete;
import static com.shawnhu.seagull.seagull.twitter.utils.content.ContentResolverUtils.bulkInsert;

public class CacheUsersStatusesTask
        extends AsyncTask<Void, Void, Void> {

    private final TwitterListResponse<twitter4j.Status>[] all_statuses;
    private final ContentResolver resolver;

    public CacheUsersStatusesTask(final Context context, final TwitterListResponse<twitter4j.Status>... all_statuses) {
        resolver = context.getContentResolver();
        this.all_statuses = all_statuses;
    }

    @Override
    protected Void doInBackground(final Void... args) {
        if (all_statuses == null || all_statuses.length == 0) return null;
        final Extractor extractor = new Extractor();
        final Set<ContentValues> cachedUsersValues = new HashSet<ContentValues>();
        final Set<ContentValues> cached_statuses_values = new HashSet<ContentValues>();
        final Set<ContentValues> hashtag_values = new HashSet<ContentValues>();
        final Set<Long> userIds = new HashSet<Long>();
        final Set<Long> status_ids = new HashSet<Long>();
        final Set<String> hashtags = new HashSet<String>();
        final Set<User> users = new HashSet<User>();

        for (final TwitterListResponse<twitter4j.Status> values : all_statuses) {
            if (values == null || values.getList() == null) {
                continue;
            }
            final List<twitter4j.Status> list = values.getList();
            for (final twitter4j.Status status : list) {
                if (status == null || status.getId() <= 0) {
                    continue;
                }
                status_ids.add(status.getId());
                cached_statuses_values.add(makeStatusContentValues(status, values.account_id));
                hashtags.addAll(extractor.extractHashtags(status.getText()));
                final User user = status.getUser();
                if (user != null && user.getId() > 0) {
                    users.add(user);
                    final ContentValues filtered_users_values = new ContentValues();
                    filtered_users_values.put(TweetStore.Filters.Users.NAME, user.getName());
                    filtered_users_values.put(TweetStore.Filters.Users.SCREEN_NAME, user.getScreenName());
                    final String filtered_users_where = Where.equals(TweetStore.Filters.Users.USER_ID, user.getId()).getSQL();
                    resolver.update(TweetStore.Filters.Users.CONTENT_URI, filtered_users_values, filtered_users_where, null);
                }
            }
        }

        bulkDelete(resolver, TweetStore.CachedStatuses.CONTENT_URI, TweetStore.CachedStatuses.STATUS_ID, status_ids, null, false);
        bulkInsert(resolver, TweetStore.CachedStatuses.CONTENT_URI, cached_statuses_values);

        for (final String hashtag : hashtags) {
            final ContentValues hashtag_value = new ContentValues();
            hashtag_value.put(TweetStore.CachedHashtags.NAME, hashtag);
            hashtag_values.add(hashtag_value);
        }
        bulkDelete(resolver, TweetStore.CachedHashtags.CONTENT_URI, TweetStore.CachedHashtags.NAME, hashtags, null, true);
        bulkInsert(resolver, TweetStore.CachedHashtags.CONTENT_URI, hashtag_values);

        for (final User user : users) {
            userIds.add(user.getId());
            cachedUsersValues.add(makeCachedUserContentValues(user));
        }
        bulkDelete(resolver, TweetStore.CachedUsers.CONTENT_URI, TweetStore.CachedUsers.USER_ID, userIds, null, false);
        bulkInsert(resolver, TweetStore.CachedUsers.CONTENT_URI, cachedUsersValues);
        return null;
    }
}
