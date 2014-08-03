package com.shawnhu.seagull.utils.querybuilder;

public class Utils {

	public static String toString(final Object[] array, final char token, final boolean include_space) {
		final StringBuilder builder = new StringBuilder();
		final int length = array.length;
		for (int i = 0; i < length; i++) {
			final String id_string = String.valueOf(array[i]);
			if (id_string != null) {
				if (i > 0) {
					builder.append(include_space ? token + " " : token);
				}
				builder.append(id_string);
			}
		}
		return builder.toString();
	}

	public static String toString(final SQLLang[] array) {
		final StringBuilder builder = new StringBuilder();
		final int length = array.length;
		for (int i = 0; i < length; i++) {
			final String id_string = array[i].getSQL();
			if (id_string != null) {
				if (i > 0) {
					builder.append(", ");
				}
				builder.append(id_string);
			}
		}
		return builder.toString();
	}

    public static Selectable getColumnsFromProjection(final String... projection) {
		if (projection == null) return new AllColumns();
		final int length = projection.length;
		final Columns.Column[] columns = new Columns.Column[length];
		for (int i = 0; i < length; i++) {
			columns[i] = new Columns.Column(projection[i]);
		}
		return new Columns(columns);
	}
}
