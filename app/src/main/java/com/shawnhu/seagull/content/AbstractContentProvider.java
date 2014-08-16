package com.shawnhu.seagull.content;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.FileNotFoundException;

public abstract class AbstractContentProvider extends ContentProvider {
    protected SQLiteDatabase    mDatabase;
    abstract protected int      getTableId(Uri uri);
    abstract protected String   getTableNameById(int tableId);
    abstract protected boolean  shouldReplaceOnConflict(final int table_id);


    protected String            TAG = "AbstractContentProvider";

    @Override
    public boolean onCreate() {
        if (mDatabase == null) {
            throw new NullPointerException("You must set the target SQLiteDatabase object" +
                    " which must not be null.");
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
                Utils.notifyContentObserver(getContext(), uri);
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
                Utils.notifyContentObserver(getContext(), uri);
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
                Utils.notifyContentObserver(getContext(), uri);
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
                Utils.notifyContentObserver(getContext(), uri);
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

    static public class Utils {
        static public void notifyContentObserver(final Context context, final Uri uri) {
            final ContentResolver cr = context.getContentResolver();
            if (uri == null || cr == null) {
                throw new NullPointerException();
            }
            cr.notifyChange(uri, null);
        }
    }
}
