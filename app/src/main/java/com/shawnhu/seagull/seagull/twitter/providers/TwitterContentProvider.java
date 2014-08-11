package com.shawnhu.seagull.seagull.twitter.providers;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.shawnhu.seagull.seagull.twitter.TwitterManager;

import java.io.FileNotFoundException;

import static com.shawnhu.seagull.seagull.twitter.TweetStore.TABLE_ID_CACHED_HASHTAGS;
import static com.shawnhu.seagull.seagull.twitter.TweetStore.TABLE_ID_CACHED_STATUSES;
import static com.shawnhu.seagull.seagull.twitter.TweetStore.TABLE_ID_CACHED_USERS;
import static com.shawnhu.seagull.seagull.twitter.TweetStore.TABLE_ID_FILTERED_KEYWORDS;
import static com.shawnhu.seagull.seagull.twitter.TweetStore.TABLE_ID_FILTERED_LINKS;
import static com.shawnhu.seagull.seagull.twitter.TweetStore.TABLE_ID_FILTERED_SOURCES;
import static com.shawnhu.seagull.seagull.twitter.TweetStore.TABLE_ID_FILTERED_USERS;
import static com.shawnhu.seagull.seagull.twitter.utils.Utils.getTableId;
import static com.shawnhu.seagull.seagull.twitter.utils.Utils.getTableNameById;

public final class TwitterContentProvider extends ContentProvider {
    private static final String         TAG = "TwitterDataProvider";
    private SQLiteDatabase              mDatabase;

    @Override
    public boolean onCreate() {
        final Context context   = getContext();
        try {
            mDatabase = TwitterManager.getInstance(context).getWritableSQLiteDatabase();
        } catch(Exception e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public Uri insert(final Uri uri, final ContentValues values) {
        final int       tableId = getTableId(uri);
        final String    table   = getTableNameById(tableId);
        long            rowId   = -1;
        if (table != null) {
            final boolean replaceOnConflict = shouldReplaceOnConflict(tableId);
            if (replaceOnConflict) {
                rowId = mDatabase.insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            } else {
                rowId = mDatabase.insert(table, null, values);
            }
            if (rowId != -1) {
                notifyContentObserver(uri);
            } else {
                Log.e(TAG, String.format("Inserting for URI(%s) table(%s) failed", uri, table));
            }
        } else {
            Log.e(TAG, String.format(
                    "Inserting for URI(%s) failed: tableId=%d, table is null", uri, tableId));
        }
        return Uri.withAppendedPath(uri, String.valueOf(rowId));
    }

    @Override
    public int bulkInsert(final Uri uri, final ContentValues[] values) {
        final int       tableId = getTableId(uri);
        final String    table   = getTableNameById(tableId);
        int             insertedRows = 0;
        long            ret;
        if (table != null && values != null) {
            mDatabase.beginTransaction();
            final boolean replaceOnConflict = shouldReplaceOnConflict(tableId);
            for (final ContentValues contentValues : values) {
                if (replaceOnConflict) {
                    ret = mDatabase.insertWithOnConflict(table, null, contentValues,
                            SQLiteDatabase.CONFLICT_REPLACE);
                } else {
                    ret = mDatabase.insert(table, null, contentValues);
                }
                if (ret != -1) {
                    insertedRows++;
                } else {
                    Log.e(TAG, String.format("Inserting for URI(%s) failed: table=%s", uri, table));
                }
            }
            mDatabase.setTransactionSuccessful();
            mDatabase.endTransaction();
            if (insertedRows > 0) {
                notifyContentObserver(uri);
            }
        } else if (table == null) {
            Log.e(TAG, String.format("Inserting for URI(%s) failed: table is null", uri));
        }

        return insertedRows;
    }

    @Override
    public int delete(final Uri uri, final String selection, final String[] selectionArgs) {
        final int tableId = getTableId(uri);
        final String table = getTableNameById(tableId);
        int affected_rows = 0;

        if (table != null) {
            affected_rows = mDatabase.delete(table, selection, selectionArgs);
            if (affected_rows > 0) {
                notifyContentObserver(uri);
            }
        } else {
            Log.e(TAG, String.format(
                    "Deleting for URI(%s) failed: tableId=%d, table is null", uri, tableId));
        }
        return affected_rows;
    }


    @Override
    public Cursor query(final Uri uri, final String[] projection, final String selection,
                        final String[] selectionArgs, final String sortOrder) {
        final int       tableId = getTableId(uri);
        final String    table   = getTableNameById(tableId);
        Cursor          c       = null;
        if (table != null) {
            c = mDatabase.query(table, projection, selection, selectionArgs,
                                null, null, sortOrder);
            if (c != null) {
                c.setNotificationUri(getContext().getContentResolver(), uri);
            }
        } else {
            Log.e(TAG, String.format("Querying URI(%s) failed: table is null", uri));
        }
        return c;
    }
    @Override
    public int update(final Uri uri, final ContentValues values,
                      final String selection, final String[] selectionArgs) {
        final int       tableId = getTableId(uri);
        final String    table   = getTableNameById(tableId);
        int             rows    = 0;
        if (table != null) {
            rows = mDatabase.update(table, values, selection, selectionArgs);
            if (rows > 0) {
                notifyContentObserver(uri);
            }
        } else {
            Log.e(TAG, String.format("Updating for URI(%s) failed: table is null", uri));
        }

        return rows;
    }

    @Override
    public String getType(final Uri uri) {
        return null;
    }

    @Override
    public ParcelFileDescriptor openFile(final Uri uri, final String mode)
            throws FileNotFoundException {
        return super.openFile(uri, mode);
    }

    private void notifyContentObserver(final Uri uri) {
        final ContentResolver cr = getContext().getContentResolver();
        if (uri == null || cr == null) return;
        cr.notifyChange(uri, null);
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
}
