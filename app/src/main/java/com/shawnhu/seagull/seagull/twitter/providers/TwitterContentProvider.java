package com.shawnhu.seagull.seagull.twitter.providers;

import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.shawnhu.seagull.database.AbstractContentProvider;
import com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants;

import static com.shawnhu.seagull.seagull.twitter.providers.TweetStore.TABLE_ID_ACCOUNTS;
import static com.shawnhu.seagull.seagull.twitter.providers.TweetStore.TABLE_ID_CACHED_HASHTAGS;
import static com.shawnhu.seagull.seagull.twitter.providers.TweetStore.TABLE_ID_CACHED_STATUSES;
import static com.shawnhu.seagull.seagull.twitter.providers.TweetStore.TABLE_ID_CACHED_USERS;
import static com.shawnhu.seagull.seagull.twitter.providers.TweetStore.TABLE_ID_DIRECT_MESSAGES;
import static com.shawnhu.seagull.seagull.twitter.providers.TweetStore.TABLE_ID_DRAFTS;
import static com.shawnhu.seagull.seagull.twitter.providers.TweetStore.TABLE_ID_FILTERED_KEYWORDS;
import static com.shawnhu.seagull.seagull.twitter.providers.TweetStore.TABLE_ID_FILTERED_LINKS;
import static com.shawnhu.seagull.seagull.twitter.providers.TweetStore.TABLE_ID_FILTERED_SOURCES;
import static com.shawnhu.seagull.seagull.twitter.providers.TweetStore.TABLE_ID_FILTERED_USERS;
import static com.shawnhu.seagull.seagull.twitter.providers.TweetStore.TABLE_ID_MENTIONS;
import static com.shawnhu.seagull.seagull.twitter.providers.TweetStore.TABLE_ID_STATUSES;
import static com.shawnhu.seagull.seagull.twitter.providers.TweetStore.TABLE_ID_TRENDS_LOCAL;

public class TwitterContentProvider extends AbstractContentProvider {

    @Override
    public boolean onCreate() {
        SQLiteOpenHelper sqLiteOpenHelper =
                new TwitterSQLiteOpenHelper(getContext(),
                    SeagullTwitterConstants.DATABASES_NAME,
                    SeagullTwitterConstants.DATABASES_VERSION);
        mDatabase = sqLiteOpenHelper.getWritableDatabase();
        TAG = "TwitterDataProvider";

        return super.onCreate();
    }

    protected int      getTableId(Uri uri) {
        return TweetStore.CONTENT_PROVIDER_URI_MATCHER.match(uri);
    }
    protected String   getTableNameById(int tableId) {

        switch (tableId) {
            case TABLE_ID_ACCOUNTS:
                return TweetStore.Accounts.TABLE_NAME;
            case TABLE_ID_STATUSES:
                return TweetStore.Statuses.TABLE_NAME;
            case TABLE_ID_MENTIONS:
                return TweetStore.Mentions.TABLE_NAME;
            case TABLE_ID_DRAFTS:
                return TweetStore.Drafts.TABLE_NAME;
            case TABLE_ID_FILTERED_USERS:
                return TweetStore.Filters.Users.TABLE_NAME;
            case TABLE_ID_FILTERED_KEYWORDS:
                return TweetStore.Filters.Keywords.TABLE_NAME;
            case TABLE_ID_FILTERED_SOURCES:
                return TweetStore.Filters.Sources.TABLE_NAME;
            case TABLE_ID_FILTERED_LINKS:
                return TweetStore.Filters.Links.TABLE_NAME;
            case TABLE_ID_DIRECT_MESSAGES:
                return TweetStore.DirectMessages.TABLE_NAME;
            case TABLE_ID_TRENDS_LOCAL:
                return TweetStore.CachedTrends.Local.TABLE_NAME;
            case TABLE_ID_CACHED_STATUSES:
                return TweetStore.CachedStatuses.TABLE_NAME;
            case TABLE_ID_CACHED_USERS:
                return TweetStore.CachedUsers.TABLE_NAME;
            case TABLE_ID_CACHED_HASHTAGS:
                return TweetStore.CachedHashtags.TABLE_NAME;
            default:
                return null;
        }
    }
    protected boolean  shouldReplaceOnConflict(final int table_id) {
        switch (table_id) {
            case TABLE_ID_CACHED_HASHTAGS:
            case TABLE_ID_CACHED_STATUSES:
            case TABLE_ID_CACHED_USERS:
            case TABLE_ID_FILTERED_USERS:
            case TABLE_ID_FILTERED_KEYWORDS:
            case TABLE_ID_FILTERED_SOURCES:
            case TABLE_ID_FILTERED_LINKS:
                return true;
        }
        return false;
    }
}
