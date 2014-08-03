package com.shawnhu.seagull.seagull.twitter.utils.content;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.CancellationSignal;

import com.shawnhu.seagull.utils.ArrayUtils;
import com.shawnhu.seagull.utils.StrictModeUtils;

import java.util.Collection;

import static android.text.TextUtils.isEmpty;

public class ContentResolverUtils {

	private static final int MAX_DELETE_COUNT = 128;

	public static <T> int bulkDelete(final ContentResolver resolver, final Uri uri, final String in_column,
			final Collection<T> col_values, final String extra_where, final boolean values_is_string) {
		StrictModeUtils.checkDiskIO();
		if (col_values == null) return 0;
		return bulkDelete(resolver, uri, in_column, col_values.toArray(), extra_where, values_is_string);
	}

	public static <T> int bulkDelete(final ContentResolver resolver, final Uri uri, final String in_column,
			final T[] col_values, final String extra_where, final boolean values_is_string) {
		StrictModeUtils.checkDiskIO();
		if (resolver == null || uri == null || isEmpty(in_column) || col_values == null || col_values.length == 0)
			return 0;
		final int col_values_length = col_values.length, blocks_count = col_values_length / MAX_DELETE_COUNT + 1;
		int rows_deleted = 0;
		for (int i = 0; i < blocks_count; i++) {
			final int start = i * MAX_DELETE_COUNT, end = Math.min(start + MAX_DELETE_COUNT, col_values_length);
			final String[] block = ArrayUtils.toStringArray(ArrayUtils.subArray(col_values, start, end));
			if (values_is_string) {
				final StringBuilder where = new StringBuilder(in_column + " IN(" + ArrayUtils.toStringForSQL(block)
						+ ")");
				if (!isEmpty(extra_where)) {
					where.append("AND " + extra_where);
				}
				rows_deleted += resolver.delete(uri, where.toString(), block);
			} else {
				final StringBuilder where = new StringBuilder(in_column + " IN("
						+ ArrayUtils.toString(block, ',', true) + ")");
				if (!isEmpty(extra_where)) {
					where.append("AND " + extra_where);
				}
				rows_deleted += resolver.delete(uri, where.toString(), null);
			}
		}
		return rows_deleted;
	}

	public static int bulkInsert(final ContentResolver resolver, final Uri uri, final Collection<ContentValues> values) {
		StrictModeUtils.checkDiskIO();
		if (values == null) return 0;
		return bulkInsert(resolver, uri, values.toArray(new ContentValues[values.size()]));
	}

	public static int bulkInsert(final ContentResolver resolver, final Uri uri, final ContentValues[] values) {
		StrictModeUtils.checkDiskIO();
		if (resolver == null || uri == null || values == null || values.length == 0) return 0;
		final int col_values_length = values.length, blocks_count = col_values_length / MAX_DELETE_COUNT + 1;
		int rows_inserted = 0;
		for (int i = 0; i < blocks_count; i++) {
			final int start = i * MAX_DELETE_COUNT, end = Math.min(start + MAX_DELETE_COUNT, col_values_length);
			final ContentValues[] block = new ContentValues[end - start];
			System.arraycopy(values, start, block, 0, end - start);
			rows_inserted += resolver.bulkInsert(uri, block);
		}
		return rows_inserted;
	}

	public static Cursor query(final ContentResolver resolver, final Uri uri, final String[] projection,
			final String selection, final String[] selectionArgs, final String sortOrder) {
		StrictModeUtils.checkDiskIO();
		return resolver.query(uri, projection, selection, selectionArgs, sortOrder);
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static Cursor query(final ContentResolver resolver, final Uri uri, final String[] projection,
			final String selection, final String[] selectionArgs, final String sortOrder,
			final CancellationSignal cancellationSignal) {
		StrictModeUtils.checkDiskIO();
		return resolver.query(uri, projection, selection, selectionArgs, sortOrder, cancellationSignal);
	}

}
