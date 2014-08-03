package com.shawnhu.seagull.seagull.twitter.utils.content;

import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants;
import com.shawnhu.seagull.seagull.twitter.TwitterQueryBuilder;
import com.shawnhu.seagull.utils.StringUtils;
import com.shawnhu.seagull.seagull.twitter.TweetStore;
import com.shawnhu.seagull.utils.querybuilder.NewColumn;
import com.shawnhu.seagull.utils.querybuilder.SQLQueryBuilder;
import com.shawnhu.seagull.utils.querybuilder.query.SQLCreateTableQuery;
import com.shawnhu.seagull.utils.querybuilder.query.SQLCreateViewQuery;


public final class TwitterSQLiteOpenHelper extends SQLiteOpenHelper {

	private final Context mContext;

	public TwitterSQLiteOpenHelper(final Context context, final String name, final int version) {
		super(context, name, null, version);
		mContext = context;
	}

	@Override
	public void onCreate(final SQLiteDatabase db) {
		db.beginTransaction();
		db.execSQL(createTable(TweetStore.Accounts.TABLE_NAME, TweetStore.Accounts.COLUMNS, TweetStore.Accounts.TYPES, true));
		db.execSQL(createTable(TweetStore.Statuses.TABLE_NAME, TweetStore.Statuses.COLUMNS, TweetStore.Statuses.TYPES, true));
		db.execSQL(createTable(TweetStore.Mentions.TABLE_NAME, TweetStore.Mentions.COLUMNS, TweetStore.Mentions.TYPES, true));
		db.execSQL(createTable(TweetStore.Drafts.TABLE_NAME, TweetStore.Drafts.COLUMNS, TweetStore.Drafts.TYPES, true));
		db.execSQL(createTable(TweetStore.CachedUsers.TABLE_NAME, TweetStore.CachedUsers.COLUMNS, TweetStore.CachedUsers.TYPES, true));
		db.execSQL(createTable(TweetStore.CachedStatuses.TABLE_NAME, TweetStore.CachedStatuses.COLUMNS, TweetStore.CachedStatuses.TYPES, true));
		db.execSQL(createTable(TweetStore.CachedHashtags.TABLE_NAME, TweetStore.CachedHashtags.COLUMNS, TweetStore.CachedHashtags.TYPES, true));
		db.execSQL(createTable(TweetStore.Filters.Users.TABLE_NAME, TweetStore.Filters.Users.COLUMNS, TweetStore.Filters.Users.TYPES, true));
		db.execSQL(createTable(TweetStore.Filters.Keywords.TABLE_NAME, TweetStore.Filters.Keywords.COLUMNS, TweetStore.Filters.Keywords.TYPES, true));
		db.execSQL(createTable(TweetStore.Filters.Sources.TABLE_NAME, TweetStore.Filters.Sources.COLUMNS, TweetStore.Filters.Sources.TYPES, true));
		db.execSQL(createTable(TweetStore.Filters.Links.TABLE_NAME, TweetStore.Filters.Links.COLUMNS, TweetStore.Filters.Links.TYPES, true));
		db.execSQL(createTable(TweetStore.DirectMessages.Inbox.TABLE_NAME, TweetStore.DirectMessages.Inbox.COLUMNS,
				TweetStore.DirectMessages.Inbox.TYPES, true));
		db.execSQL(createTable(TweetStore.DirectMessages.Outbox.TABLE_NAME, TweetStore.DirectMessages.Outbox.COLUMNS,
				TweetStore.DirectMessages.Outbox.TYPES, true));
		db.execSQL(createTable(TweetStore.CachedTrends.Local.TABLE_NAME, TweetStore.CachedTrends.Local.COLUMNS, TweetStore.CachedTrends.Local.TYPES,
				true));
		db.execSQL(createTable(TweetStore.Tabs.TABLE_NAME, TweetStore.Tabs.COLUMNS, TweetStore.Tabs.TYPES, true));
		db.execSQL(createDirectMessagesView().getSQL());
		db.execSQL(createDirectMessageConversationEntriesView().getSQL());
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	@Override
	public void onDowngrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
		handleVersionChange(db, oldVersion, newVersion);
	}

	@Override
	public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
		handleVersionChange(db, oldVersion, newVersion);
		if (oldVersion <= 43 && newVersion >= 44) {
			final ContentValues values = new ContentValues();
			final SharedPreferences prefs = mContext
					.getSharedPreferences(SeagullTwitterConstants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
			// Here I use old consumer key/secret because it's default key for
			// older versions
			final String pref_consumer_key = prefs.getString(SeagullTwitterConstants.KEY_CONSUMER_KEY, SeagullTwitterConstants.TWITTER_CONSUMER_KEY);
			final String pref_consumer_secret = prefs.getString(SeagullTwitterConstants.KEY_CONSUMER_SECRET, SeagullTwitterConstants.TWITTER_CONSUMER_SECRET);
			values.put(TweetStore.Accounts.CONSUMER_KEY, StringUtils.trim(pref_consumer_key));
			values.put(TweetStore.Accounts.CONSUMER_SECRET, StringUtils.trim(pref_consumer_secret));
			db.update(TweetStore.Accounts.TABLE_NAME, values, null, null);
		}
	}

	private SQLCreateViewQuery createDirectMessageConversationEntriesView() {
		final SQLCreateViewQuery.Builder qb = SQLQueryBuilder.createView(true,
                TweetStore.DirectMessages.ConversationEntries.TABLE_NAME);
		qb.as(TwitterQueryBuilder.ConversationsEntryQueryBuilder.build());
		return qb.build();
	}

	private SQLCreateViewQuery createDirectMessagesView() {
		final SQLCreateViewQuery.Builder qb = SQLQueryBuilder.createView(true, TweetStore.DirectMessages.TABLE_NAME);
		qb.as(TwitterQueryBuilder.DirectMessagesQueryBuilder.build());
		return qb.build();
	}

	private void handleVersionChange(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
		final HashMap<String, String> accountsAlias = new HashMap<String, String>();
		final HashMap<String, String> filtersAlias = new HashMap<String, String>();
		accountsAlias.put(TweetStore.Accounts.SCREEN_NAME, "username");
		accountsAlias.put(TweetStore.Accounts.NAME, "username");
		accountsAlias.put(TweetStore.Accounts.ACCOUNT_ID, "user_id");
		accountsAlias.put(TweetStore.Accounts.COLOR, "user_color");
		accountsAlias.put(TweetStore.Accounts.OAUTH_TOKEN_SECRET, "token_secret");
		DatabaseUpgradeHelper.safeUpgrade(db, TweetStore.Accounts.TABLE_NAME, TweetStore.Accounts.COLUMNS, TweetStore.Accounts.TYPES, false, accountsAlias);
		DatabaseUpgradeHelper.safeUpgrade(db, TweetStore.Statuses.TABLE_NAME, TweetStore.Statuses.COLUMNS, TweetStore.Statuses.TYPES, true, null);
		DatabaseUpgradeHelper.safeUpgrade(db, TweetStore.Mentions.TABLE_NAME, TweetStore.Mentions.COLUMNS, TweetStore.Mentions.TYPES, true, null);
		DatabaseUpgradeHelper.safeUpgrade(db, TweetStore.Drafts.TABLE_NAME, TweetStore.Drafts.COLUMNS, TweetStore.Drafts.TYPES, false, null);
		DatabaseUpgradeHelper.safeUpgrade(db, TweetStore.CachedUsers.TABLE_NAME, TweetStore.CachedUsers.COLUMNS, TweetStore.CachedUsers.TYPES, true, null);
		DatabaseUpgradeHelper.safeUpgrade(db, TweetStore.CachedStatuses.TABLE_NAME, TweetStore.CachedStatuses.COLUMNS, TweetStore.CachedStatuses.TYPES, false, null);
		DatabaseUpgradeHelper.safeUpgrade(db, TweetStore.CachedHashtags.TABLE_NAME, TweetStore.CachedHashtags.COLUMNS, TweetStore.CachedHashtags.TYPES, false, null);
		DatabaseUpgradeHelper.safeUpgrade(db, TweetStore.Filters.Users.TABLE_NAME, TweetStore.Filters.Users.COLUMNS, TweetStore.Filters.Users.TYPES, oldVersion < 49, null);
		DatabaseUpgradeHelper.safeUpgrade(db, TweetStore.Filters.Keywords.TABLE_NAME, TweetStore.Filters.Keywords.COLUMNS, TweetStore.Filters.Keywords.TYPES, oldVersion < 49,
                filtersAlias);
		DatabaseUpgradeHelper.safeUpgrade(db, TweetStore.Filters.Sources.TABLE_NAME, TweetStore.Filters.Sources.COLUMNS, TweetStore.Filters.Sources.TYPES, oldVersion < 49,
                filtersAlias);
		DatabaseUpgradeHelper.safeUpgrade(db, TweetStore.Filters.Links.TABLE_NAME, TweetStore.Filters.Links.COLUMNS, TweetStore.Filters.Links.TYPES, oldVersion < 49,
                filtersAlias);
		DatabaseUpgradeHelper.safeUpgrade(db, TweetStore.DirectMessages.Inbox.TABLE_NAME, TweetStore.DirectMessages.Inbox.COLUMNS, TweetStore.DirectMessages.Inbox.TYPES,
                true, null);
		DatabaseUpgradeHelper.safeUpgrade(db, TweetStore.DirectMessages.Outbox.TABLE_NAME, TweetStore.DirectMessages.Outbox.COLUMNS, TweetStore.DirectMessages.Outbox.TYPES,
                true, null);
		DatabaseUpgradeHelper.safeUpgrade(db, TweetStore.CachedTrends.Local.TABLE_NAME, TweetStore.CachedTrends.Local.COLUMNS, TweetStore.CachedTrends.Local.TYPES, true, null);
		DatabaseUpgradeHelper.safeUpgrade(db, TweetStore.Tabs.TABLE_NAME, TweetStore.Tabs.COLUMNS, TweetStore.Tabs.TYPES, false, null);
		db.execSQL(createDirectMessagesView().getSQL());
		db.execSQL(createDirectMessageConversationEntriesView().getSQL());
	}

	private static String createTable(final String tableName, final String[] columns, final String[] types,
			final boolean createIfNotExists) {
		final SQLCreateTableQuery.Builder qb = SQLQueryBuilder.createTable(createIfNotExists, tableName);
		qb.columns(NewColumn.createNewColumns(columns, types));
		return qb.buildSQL();
	}

}
