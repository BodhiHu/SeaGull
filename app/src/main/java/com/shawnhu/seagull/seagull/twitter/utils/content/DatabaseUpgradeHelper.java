package com.shawnhu.seagull.seagull.twitter.utils.content;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.shawnhu.seagull.utils.ArrayUtils;
import com.shawnhu.seagull.utils.querybuilder.Columns;
import com.shawnhu.seagull.utils.querybuilder.NewColumn;
import com.shawnhu.seagull.utils.querybuilder.Tables;
import com.shawnhu.seagull.utils.querybuilder.Where;
import com.shawnhu.seagull.utils.querybuilder.query.SQLInsertIntoQuery;
import com.shawnhu.seagull.utils.querybuilder.query.SQLSelectQuery;
import com.shawnhu.seagull.utils.querybuilder.SQLQueryBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public final class DatabaseUpgradeHelper {
	public static void safeUpgrade(final SQLiteDatabase db, final String table, final String[] newColNames,
			final String[] newColTypes, final boolean dropDirectly, final boolean strictMode,
			final Map<String, String> colAliases) {
		safeUpgrade(db, table, newColNames, newColTypes, dropDirectly, strictMode, colAliases, SQLInsertIntoQuery.OnConflict.REPLACE);
	}

	public static void safeUpgrade(final SQLiteDatabase db, final String table, final String[] newColNames,
			final String[] newColTypes, final boolean dropDirectly, final boolean strictMode,
			final Map<String, String> colAliases, final SQLInsertIntoQuery.OnConflict onConflict) {

		if (newColNames == null || newColTypes == null || newColNames.length != newColTypes.length)
			throw new IllegalArgumentException("Invalid parameters for upgrading table " + table
					+ ", length of columns and types not match.");

		// First, create the table if not exists.
		final NewColumn[] newCols = NewColumn.createNewColumns(newColNames, newColTypes);
		final String createQuery = SQLQueryBuilder.createTable(true, table).columns(newCols).buildSQL();
		db.execSQL(createQuery);

		// We need to get all data from old table.
		final String[] oldCols = getColumnNames(db, table);
		if (strictMode) {
			final String oldCreate = getCreateSQL(db, table);
			final Map<String, String> map = getTypeMapByCreateQuery(oldCreate);
			boolean differenct = false;
			for (final NewColumn newCol : newCols) {
				if (!newCol.getType().equalsIgnoreCase(map.get(newCol.getName()))) {
					differenct = true;
				}
			}
			if (!differenct) return;
		} else if (oldCols == null || ArrayUtils.contentMatch(newColNames, oldCols)) return;
		if (dropDirectly) {
			db.beginTransaction();
			db.execSQL(SQLQueryBuilder.dropTable(true, table).getSQL());
			db.execSQL(createQuery);
			db.setTransactionSuccessful();
			db.endTransaction();
			return;
		}
		final String tempTable = String.format(Locale.US, "temp_%s_%d", table, System.currentTimeMillis());
		db.beginTransaction();
		db.execSQL(SQLQueryBuilder.alterTable(table).renameTo(tempTable).buildSQL());
		db.execSQL(createQuery);
		final String[] notNullCols = getNotNullColumns(newCols);
		final String insertQuery = createInsertDataQuery(table, tempTable, newColNames, oldCols, colAliases,
				notNullCols, onConflict);
		if (insertQuery != null) {
			db.execSQL(insertQuery);
		}
		db.execSQL(SQLQueryBuilder.dropTable(true, tempTable).getSQL());
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	public static void safeUpgrade(final SQLiteDatabase db, final String table, final String[] newColNames,
			final String[] newColTypes, final boolean dropDirectly, final Map<String, String> colAliases) {
		safeUpgrade(db, table, newColNames, newColTypes, dropDirectly, true, colAliases, SQLInsertIntoQuery.OnConflict.REPLACE);
	}

	private static String createInsertDataQuery(final String table, final String tempTable, final String[] newCols,
			final String[] oldCols, final Map<String, String> colAliases, final String[] notNullCols,
			final SQLInsertIntoQuery.OnConflict onConflict) {
		final SQLInsertIntoQuery.Builder qb = SQLQueryBuilder.insertInto(onConflict, table);
		final List<String> newInsertColsList = new ArrayList<String>();
		for (final String newCol : newCols) {
			final String oldAliasedCol = colAliases != null ? colAliases.get(newCol) : null;
			if (ArrayUtils.contains(oldCols, newCol) || oldAliasedCol != null
					&& ArrayUtils.contains(oldCols, oldAliasedCol)) {
				newInsertColsList.add(newCol);
			}
		}
		final String[] newInsertCols = newInsertColsList.toArray(new String[newInsertColsList.size()]);
		if (!ArrayUtils.contains(newInsertCols, notNullCols)) return null;
		qb.columns(newInsertCols);
		final Columns.Column[] oldDataCols = new Columns.Column[newInsertCols.length];
		for (int i = 0, j = oldDataCols.length; i < j; i++) {
			final String newCol = newInsertCols[i];
			final String oldAliasedCol = colAliases != null ? colAliases.get(newCol) : null;
			if (oldAliasedCol != null && ArrayUtils.contains(oldCols, oldAliasedCol)) {
				oldDataCols[i] = new Columns.Column(oldAliasedCol, newCol);
			} else {
				oldDataCols[i] = new Columns.Column(newCol);
			}
		}
		final SQLSelectQuery.Builder selectOldBuilder = SQLQueryBuilder.select(new Columns(oldDataCols));
		selectOldBuilder.from(new Tables(tempTable));
		qb.select(selectOldBuilder.build());
		return qb.buildSQL();
	}

	private static String[] getColumnNames(final SQLiteDatabase db, final String table) {
		final Cursor cur = db.query(table, null, null, null, null, null, null, "1");
		if (cur == null) return null;
		try {
			return cur.getColumnNames();
		} finally {
			cur.close();
		}
	}

	private static String getCreateSQL(final SQLiteDatabase db, final String table) {
		final SQLSelectQuery.Builder qb = SQLQueryBuilder.select(new Columns.Column("sql"));
		qb.from(new Tables("sqlite_master"));
		qb.where(new Where("type = ? AND name = ?"));
		final Cursor c = db.rawQuery(qb.buildSQL(), new String[] { "table", table });
		if (c == null) return null;
		try {
			if (c.moveToFirst()) return c.getString(0);
			return null;
		} finally {
			c.close();
		}
	}

	private static String[] getNotNullColumns(final NewColumn[] newCols) {
		if (newCols == null) return null;
		final String[] notNullCols = new String[newCols.length];
		int count = 0;
		for (final NewColumn column : newCols) {
			if (column.getType().endsWith(" NOT NULL")) {
				notNullCols[count++] = column.getName();
			}
		}
		return ArrayUtils.subArray(notNullCols, 0, count);
	}

	private static Map<String, String> getTypeMapByCreateQuery(final String query) {
		if (TextUtils.isEmpty(query)) return Collections.emptyMap();
		final int start = query.indexOf("("), end = query.lastIndexOf(")");
		if (start < 0 || end < 0) return Collections.emptyMap();
		final HashMap<String, String> map = new HashMap<String, String>();
		for (final String segment : query.substring(start + 1, end).split(",")) {
			final String trimmed = segment.trim().replaceAll(" +", " ");
			final int idx = trimmed.indexOf(" ");
			map.put(trimmed.substring(0, idx), trimmed.substring(idx + 1, trimmed.length()));
		}
		return map;
	}

}
