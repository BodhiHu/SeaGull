package com.shawnhu.seagull.seagull.twitter.providers;

import android.app.NotificationManager;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.shawnhu.seagull.R;
import com.shawnhu.seagull.seagull.twitter.TweetStore;
import com.shawnhu.seagull.seagull.twitter.TwitterManager;
import com.shawnhu.seagull.seagull.twitter.model.TwitterDirectMessage;
import com.shawnhu.seagull.seagull.twitter.model.TwitterStatus;
import com.shawnhu.seagull.seagull.twitter.model.TwitterUnreadItem;
import com.shawnhu.seagull.seagull.twitter.utils.ImagePreloader;
import com.shawnhu.seagull.seagull.twitter.utils.MediaPreviewUtils;
import com.shawnhu.seagull.seagull.twitter.utils.SQLiteDatabaseWrapper;
import com.shawnhu.seagull.seagull.twitter.utils.SharedPreferencesWrapper;
import com.shawnhu.seagull.seagull.twitter.utils.TwitterQueryBuilder;
import com.shawnhu.seagull.seagull.twitter.utils.Utils;
import com.shawnhu.seagull.utils.ArrayUtils;
import com.shawnhu.seagull.utils.JSON.JSONFileIO;
import com.shawnhu.seagull.utils.ParseUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import twitter4j.http.HostAddressResolver;

import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.BROADCAST_DATABASE_READY;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.BROADCAST_HOME_ACTIVITY_ONSTART;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.BROADCAST_HOME_ACTIVITY_ONSTOP;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.KEY_PRELOAD_PREVIEW_IMAGES;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.KEY_PRELOAD_PROFILE_IMAGES;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.QUERY_PARAM_URL;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.SHARED_PREFERENCES_NAME;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.TABLE_ID_CACHED_HASHTAGS;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.TABLE_ID_CACHED_STATUSES;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.TABLE_ID_CACHED_USERS;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.TABLE_ID_DIRECT_MESSAGES;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.TABLE_ID_DIRECT_MESSAGES_CONVERSATION;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.TABLE_ID_DIRECT_MESSAGES_CONVERSATIONS_ENTRIES;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.TABLE_ID_DIRECT_MESSAGES_CONVERSATION_SCREEN_NAME;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.TABLE_ID_FILTERED_KEYWORDS;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.TABLE_ID_FILTERED_LINKS;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.TABLE_ID_FILTERED_SOURCES;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.TABLE_ID_FILTERED_USERS;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.VIRTUAL_TABLE_ID_CACHED_IMAGES;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.VIRTUAL_TABLE_ID_CACHE_FILES;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.VIRTUAL_TABLE_ID_DATABASE_READY;
import static com.shawnhu.seagull.seagull.twitter.SeagullTwitterConstants.VIRTUAL_TABLE_ID_DNS;
import static com.shawnhu.seagull.seagull.twitter.TweetStore.DirectMessages;
import static com.shawnhu.seagull.seagull.twitter.TweetStore.Preferences;
import static com.shawnhu.seagull.seagull.twitter.TweetStore.Statuses;
import static com.shawnhu.seagull.seagull.twitter.utils.Utils.getNotificationUri;
import static com.shawnhu.seagull.seagull.twitter.utils.Utils.getTableId;
import static com.shawnhu.seagull.seagull.twitter.utils.Utils.getTableNameById;

public final class TwitterDataProvider extends      ContentProvider
                                       implements   SQLiteDatabaseWrapper.LazyLoadCallback {

    private static final String         TAG = "TwitterDataProvider";

    private ContentResolver             mContentResolver;
    private SQLiteDatabaseWrapper       mDatabaseWrapper;
    private NotificationManager         mNotificationManager;
    private SharedPreferencesWrapper    mPreferences;
    private ImagePreloader              mImagePreloader;
    private HostAddressResolver         mHostAddressResolver;
    private TwitterManager              mTwitterManager;

    @Override
    public int bulkInsert(final Uri uri, final ContentValues[] values) {
        try {
            final int tableId = getTableId(uri);
            final String table = getTableNameById(tableId);
            int result = 0;
            if (table != null && values != null) {
                mDatabaseWrapper.beginTransaction();
                final boolean replaceOnConflict = shouldReplaceOnConflict(tableId);
                for (final ContentValues contentValues : values) {
                    if (replaceOnConflict) {
                        mDatabaseWrapper.insertWithOnConflict(table, null, contentValues,
                                SQLiteDatabase.CONFLICT_REPLACE);
                    } else {
                        mDatabaseWrapper.insert(table, null, contentValues);
                    }
                    result++;
                }
                mDatabaseWrapper.setTransactionSuccessful();
                mDatabaseWrapper.endTransaction();
            }
            if (result > 0) {
                onDatabaseUpdated(tableId, uri);
            }
            return result;
        } catch (final SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public int delete(final Uri uri, final String selection, final String[] selectionArgs) {
        try {
            final int tableId = getTableId(uri);
            final String table = getTableNameById(tableId);
            switch (tableId) {
                case TABLE_ID_DIRECT_MESSAGES_CONVERSATION:
                case TABLE_ID_DIRECT_MESSAGES:
                case TABLE_ID_DIRECT_MESSAGES_CONVERSATIONS_ENTRIES:
                    return 0;
            }
            if (table == null) return 0;
            final int result = mDatabaseWrapper.delete(table, selection, selectionArgs);
            if (result > 0) {
                onDatabaseUpdated(tableId, uri);
            }
            return result;
        } catch (final SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String getType(final Uri uri) {
        return null;
    }

    @Override
    public Uri insert(final Uri uri, final ContentValues values) {
        try {
            final int tableId = getTableId(uri);
            final String table = getTableNameById(tableId);
            switch (tableId) {
                case TABLE_ID_DIRECT_MESSAGES_CONVERSATION:
                case TABLE_ID_DIRECT_MESSAGES:
                case TABLE_ID_DIRECT_MESSAGES_CONVERSATIONS_ENTRIES:
                    return null;
            }
            if (table == null) return null;
            final boolean replaceOnConflict = shouldReplaceOnConflict(tableId);
            final long rowId;
            if (replaceOnConflict) {
                rowId = mDatabaseWrapper.insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            } else {
                rowId = mDatabaseWrapper.insert(table, null, values);
            }
            onDatabaseUpdated(tableId, uri);
            return Uri.withAppendedPath(uri, String.valueOf(rowId));
        } catch (final SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public boolean onCreate() {
        final Context context = getContext();
        mDatabaseWrapper = new SQLiteDatabaseWrapper(this);
        mTwitterManager  = TwitterManager.getInstance(getContext());
        mHostAddressResolver = mTwitterManager.getHostAddressResolver();
        mPreferences = SharedPreferencesWrapper.getInstance(context, SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        mImagePreloader = new ImagePreloader(context, mTwitterManager.getImageLoader());
        final IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_HOME_ACTIVITY_ONSTART);
        filter.addAction(BROADCAST_HOME_ACTIVITY_ONSTOP);
        return true;
    }

    @Override
    public SQLiteDatabase onCreateSQLiteDatabase() {
        final SQLiteOpenHelper helper = mTwitterManager.getSQLiteOpenHelper();
        return helper.getWritableDatabase();
    }

    @Override
    public ParcelFileDescriptor openFile(final Uri uri, final String mode) throws FileNotFoundException {
        if (uri == null || mode == null) throw new IllegalArgumentException();
        final int table_id = getTableId(uri);

        switch (table_id) {
            case VIRTUAL_TABLE_ID_CACHED_IMAGES: {
                return getCachedImageFd(uri.getQueryParameter(QUERY_PARAM_URL));
            }
            case VIRTUAL_TABLE_ID_CACHE_FILES: {
                return getCacheFileFd(uri.getLastPathSegment());
            }
        }
        return null;
    }

    @Override
    public Cursor query(final Uri uri, final String[] projection, final String selection, final String[] selectionArgs,
            final String sortOrder) {
        try {
            final int tableId = getTableId(uri);
            final String table = getTableNameById(tableId);
            switch (tableId) {
                case VIRTUAL_TABLE_ID_DATABASE_READY: {
                    if (mDatabaseWrapper.isReady())
                        return new MatrixCursor(projection != null ? projection : new String[0]);
                    return null;
                }
                case VIRTUAL_TABLE_ID_DNS: {
                    return getDNSCursor(uri.getLastPathSegment());
                }
                case VIRTUAL_TABLE_ID_CACHED_IMAGES: {
                    return getCachedImageCursor(uri.getQueryParameter(QUERY_PARAM_URL));
                }
                case TABLE_ID_DIRECT_MESSAGES_CONVERSATION: {
                    final List<String> segments = uri.getPathSegments();
                    if (segments.size() != 4) return null;
                    final long accountId = ParseUtils.parseLong(segments.get(2));
                    final long conversationId = ParseUtils.parseLong(segments.get(3));
                    final String query = TwitterQueryBuilder.ConversationQueryBuilder.buildByConversationId(projection,
                            accountId, conversationId, selection, sortOrder);
                    final Cursor c = mDatabaseWrapper.rawQuery(query, selectionArgs);
                    setNotificationUri(c, DirectMessages.CONTENT_URI);
                    return c;
                }
                case TABLE_ID_DIRECT_MESSAGES_CONVERSATION_SCREEN_NAME: {
                    final List<String> segments = uri.getPathSegments();
                    if (segments.size() != 4) return null;
                    final long accountId = ParseUtils.parseLong(segments.get(2));
                    final String screenName = segments.get(3);
                    final String query = TwitterQueryBuilder.ConversationQueryBuilder.buildByScreenName(projection,
                            accountId, screenName, selection, sortOrder);
                    final Cursor c = mDatabaseWrapper.rawQuery(query, selectionArgs);
                    setNotificationUri(c, DirectMessages.CONTENT_URI);
                    return c;
                }
            }
            if (table == null) return null;
            final Cursor c = mDatabaseWrapper.query(table, projection, selection, selectionArgs, null, null, sortOrder);
            setNotificationUri(c, getNotificationUri(tableId, uri));
            return c;
        } catch (final SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public int update(final Uri uri, final ContentValues values, final String selection, final String[] selectionArgs) {
        try {
            final int tableId = getTableId(uri);
            final String table = getTableNameById(tableId);
            int result = 0;
            if (table != null) {
                switch (tableId) {
                    case TABLE_ID_DIRECT_MESSAGES_CONVERSATION:
                    case TABLE_ID_DIRECT_MESSAGES:
                    case TABLE_ID_DIRECT_MESSAGES_CONVERSATIONS_ENTRIES:
                        return 0;
                }
                result = mDatabaseWrapper.update(table, values, selection, selectionArgs);
            }
            if (result > 0) {
                onDatabaseUpdated(tableId, uri);
            }
            return result;
        } catch (final SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    private Cursor getCachedImageCursor(final String url) {
        if (Utils.isDebugBuild()) {
            Log.d(TAG, String.format("getCachedImageCursor(%s)", url));
        }
        final MatrixCursor c = new MatrixCursor(TweetStore.CachedImages.MATRIX_COLUMNS);
        final File file = mImagePreloader.getCachedImageFile(url);
        if (url != null && file != null) {
            c.addRow(new String[] { url, file.getPath() });
        }
        return c;
    }

    private ParcelFileDescriptor getCachedImageFd(final String url) throws FileNotFoundException {
        if (Utils.isDebugBuild()) {
            Log.d(TAG, String.format("getCachedImageFd(%s)", url));
        }
        final File file = mImagePreloader.getCachedImageFile(url);
        if (file == null) return null;
        return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
    }

    private ParcelFileDescriptor getCacheFileFd(final String name) throws FileNotFoundException {
        if (name == null) return null;
        final Context mContext = getContext();
        final File cacheDir = mContext.getCacheDir();
        final File file = new File(cacheDir, name);
        if (!file.exists()) return null;
        return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
    }

    private ContentResolver getContentResolver() {
        if (mContentResolver != null) return mContentResolver;
        final Context context = getContext();
        return mContentResolver = context.getContentResolver();
    }

    private Cursor getDNSCursor(final String host) {
        final MatrixCursor c = new MatrixCursor(TweetStore.DNS.MATRIX_COLUMNS);
        try {
            final String address = mHostAddressResolver.resolve(host);
            if (host != null && address != null) {
                c.addRow(new String[] { host, address });
            }
        } catch (final IOException e) {

        }
        return c;
    }

    private NotificationManager getNotificationManager() {
        if (mNotificationManager != null) return mNotificationManager;
        final Context context = getContext();
        return mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private Bitmap getProfileImageForNotification(final String profile_image_url) {
        final Context context = getContext();
        final Resources res = context.getResources();
        final int w = res.getDimensionPixelSize(android.R.dimen.notification_large_icon_width);
        final int h = res.getDimensionPixelSize(android.R.dimen.notification_large_icon_height);
        final File profile_image_file = mImagePreloader.getCachedImageFile(profile_image_url);
        final Bitmap profile_image = profile_image_file != null && profile_image_file.isFile() ? BitmapFactory
                .decodeFile(profile_image_file.getPath()) : null;
        if (profile_image != null) return Bitmap.createScaledBitmap(profile_image, w, h, true);
        return Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable.ic_profile_image_default), w, h,
                true);
    }

    private int getSendersCount(final List<TwitterDirectMessage> items) {
        if (items == null || items.isEmpty()) return 0;
        final Set<Long> ids = new HashSet<Long>();
        for (final TwitterDirectMessage item : items.toArray(new TwitterDirectMessage[items.size()])) {
            ids.add(item.sender_id);
        }
        return ids.size();
    }

    private Cursor getUnreadCountsCursor() {
        final MatrixCursor c = new MatrixCursor(TweetStore.UnreadCounts.MATRIX_COLUMNS);
        return c;
    }

    private int getUsersCount(final List<TwitterStatus> items) {
        if (items == null || items.isEmpty()) return 0;
        final Set<Long> ids = new HashSet<Long>();
        for (final TwitterStatus item : items.toArray(new TwitterStatus[items.size()])) {
            ids.add(item.user_id);
        }
        return ids.size();
    }

    private void notifyContentObserver(final Uri uri) {
        final ContentResolver cr = getContentResolver();
        if (uri == null || cr == null) return;
        cr.notifyChange(uri, null);
    }

    private void onDatabaseUpdated(final int tableId, final Uri uri) {
        if (uri == null) return;
        notifyContentObserver(getNotificationUri(tableId, uri));
    }

    private void preloadImages(final ContentValues... values) {
        if (values == null) return;
        for (final ContentValues v : values) {
            if (mPreferences.getBoolean(KEY_PRELOAD_PROFILE_IMAGES, false)) {
                mImagePreloader.preloadImage(v.getAsString(Statuses.USER_PROFILE_IMAGE_URL));
                mImagePreloader.preloadImage(v.getAsString(DirectMessages.SENDER_PROFILE_IMAGE_URL));
                mImagePreloader.preloadImage(v.getAsString(DirectMessages.RECIPIENT_PROFILE_IMAGE_URL));
            }
            if (mPreferences.getBoolean(KEY_PRELOAD_PREVIEW_IMAGES, false)) {
                final String textHtml = v.getAsString(Statuses.TEXT_HTML);
                for (final String link : MediaPreviewUtils.getSupportedLinksInStatus(textHtml)) {
                    mImagePreloader.preloadImage(link);
                }
            }
        }
    }

    private void restoreUnreadItemsFile(final Collection<TwitterUnreadItem> items, final String name) {
        if (items == null || name == null) return;
        try {
            final File file = JSONFileIO.getSerializationFile(getContext(), name);
            final List<TwitterUnreadItem> restored = JSONFileIO.readArrayList(file);
            if (restored != null) {
                items.addAll(restored);
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    private void saveUnreadItemsFile(final Collection<TwitterUnreadItem> items, final String name) {
        if (items == null || name == null) return;
        try {
            final File file = JSONFileIO.getSerializationFile(getContext(), name);
            JSONFileIO.writeArray(file, items.toArray(new TwitterUnreadItem[items.size()]));
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    private void setNotificationUri(final Cursor c, final Uri uri) {
        final ContentResolver cr = getContentResolver();
        if (cr == null || c == null || uri == null) return;
        c.setNotificationUri(cr, uri);
    }

    private static int clearUnreadCount(final List<TwitterUnreadItem> set, final long[] accountIds) {
        if (accountIds == null) return 0;
        int count = 0;
        for (final TwitterUnreadItem item : set.toArray(new TwitterUnreadItem[set.size()])) {
            if (item != null && ArrayUtils.contains(accountIds, item.account_id) && set.remove(item)) {
                count++;
            }
        }
        return count;
    }

    private static List<TwitterDirectMessage> getMessagesForAccounts(final List<TwitterDirectMessage> items,
            final long accountId) {
        if (items == null) return Collections.emptyList();
        final List<TwitterDirectMessage> result = new ArrayList<TwitterDirectMessage>();
        for (final TwitterDirectMessage item : items.toArray(new TwitterDirectMessage[items.size()])) {
            if (item.account_id == accountId) {
                result.add(item);
            }
        }
        return result;
    }

    private static Cursor getPreferencesCursor(final SharedPreferencesWrapper preferences, final String key) {
        final MatrixCursor c = new MatrixCursor(TweetStore.Preferences.MATRIX_COLUMNS);
        final Map<String, Object> map = new HashMap<String, Object>();
        final Map<String, ?> all = preferences.getAll();
        if (key == null) {
            map.putAll(all);
        } else {
            map.put(key, all.get(key));
        }
        for (final Map.Entry<String, ?> item : map.entrySet()) {
            final Object value = item.getValue();
            final int type = getPreferenceType(value);
            c.addRow(new Object[] { item.getKey(), ParseUtils.parseString(value), type });
        }
        return c;
    }

    private static int getPreferenceType(final Object object) {
        if (object == null)
            return Preferences.TYPE_NULL;
        else if (object instanceof Boolean)
            return Preferences.TYPE_BOOLEAN;
        else if (object instanceof Integer)
            return Preferences.TYPE_INTEGER;
        else if (object instanceof Long)
            return Preferences.TYPE_LONG;
        else if (object instanceof Float)
            return Preferences.TYPE_FLOAT;
        else if (object instanceof String) return Preferences.TYPE_STRING;
        return Preferences.TYPE_INVALID;
    }

    private static List<TwitterStatus> getStatusesForAccounts(final List<TwitterStatus> items,
            final long accountId) {
        if (items == null) return Collections.emptyList();
        final List<TwitterStatus> result = new ArrayList<TwitterStatus>();
        for (final TwitterStatus item : items.toArray(new TwitterStatus[items.size()])) {
            if (item.account_id == accountId) {
                result.add(item);
            }
        }
        return result;
    }

    private static int getUnreadCount(final List<TwitterUnreadItem> set, final long... accountIds) {
        if (set == null || set.isEmpty()) return 0;
        int count = 0;
        for (final TwitterUnreadItem item : set.toArray(new TwitterUnreadItem[set.size()])) {
            if (item != null && ArrayUtils.contains(accountIds, item.account_id)) {
                count++;
            }
        }
        return count;
    }

    private static boolean shouldReplaceOnConflict(final int table_id) {
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

    private static String stripMentionText(final String text, final String my_screen_name) {
        if (text == null || my_screen_name == null) return text;
        final String temp = "@" + my_screen_name + " ";
        if (text.startsWith(temp)) return text.substring(temp.length());
        return text;
    }

    @SuppressWarnings("unused")
    private static class GetWritableDatabaseTask extends AsyncTask<Void, Void, SQLiteDatabase> {
        private final Context mContext;
        private final SQLiteOpenHelper mHelper;
        private final SQLiteDatabaseWrapper mWrapper;

        GetWritableDatabaseTask(final Context context, final SQLiteOpenHelper helper,
                final SQLiteDatabaseWrapper wrapper) {
            mContext = context;
            mHelper = helper;
            mWrapper = wrapper;
        }

        @Override
        protected SQLiteDatabase doInBackground(final Void... params) {
            return mHelper.getWritableDatabase();
        }

        @Override
        protected void onPostExecute(final SQLiteDatabase result) {
            mWrapper.setSQLiteDatabase(result);
            if (result != null) {
                mContext.sendBroadcast(new Intent(BROADCAST_DATABASE_READY));
            }
        }
    }

}
