package com.shawnhu.seagull.utils.querybuilder;


import java.util.Locale;

public class Where implements SQLLang {
	private final String expr;

	public Where(final String expr) {
		this.expr = expr;
	}

	@Override
	public String getSQL() {
		return expr;
	}

	public static Where and(final Where... expressions) {
		return new Where(toExpr(expressions, "AND"));
	}

	public static Where equals(final Columns.Column l, final Columns.Column r) {
		return new Where(String.format(Locale.US, "%s = %s", l.getSQL(), r.getSQL()));
	}

	public static Where equals(final Columns.Column l, final long r) {
		return new Where(String.format(Locale.US, "%s = %d", l.getSQL(), r));
	}

	public static Where equals(final Columns.Column l, final String r) {
		return new Where(String.format(Locale.US, "%s = '%s'", l.getSQL(), r));
	}

	public static Where equals(final String l, final long r) {
		return new Where(String.format(Locale.US, "%s = %d", l, r));
	}

	public static Where in(final Columns.Column column, final Selectable in) {
		return new Where(String.format("%s IN(%s)", column.getSQL(), in.getSQL()));
	}

	public static Where notEquals(final String l, final long r) {
		return new Where(String.format(Locale.US, "%s != %d", l, r));
	}

	public static Where notEquals(final String l, final String r) {
		return new Where(String.format("%s != %s", l, r));
	}

	public static Where notIn(final Columns.Column column, final Selectable in) {
		return new Where(String.format("%s NOT IN(%s)", column.getSQL(), in.getSQL()));
	}

	public static Where notNull(final Columns.Column column) {
		return new Where(String.format("%s NOT NULL", column.getSQL()));
	}

	public static Where or(final Where... expressions) {
		return new Where(toExpr(expressions, "OR"));
	}

	private static String toExpr(final Where[] array, final String token) {
		final StringBuilder builder = new StringBuilder();
		builder.append('(');
		final int length = array.length;
		for (int i = 0; i < length; i++) {
			if (i > 0) {
				builder.append(String.format(" %s ", token));
			}
			builder.append(array[i].getSQL());
		}
		builder.append(')');
		return builder.toString();
	}
}
