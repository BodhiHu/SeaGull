package com.shawnhu.seagull.utils;

import java.util.ArrayList;
import java.util.List;

public class ListUtils {

	public static List<Long> fromArray(final long[] array) {
		if (array == null) return null;
		final List<Long> list = new ArrayList<Long>();
		for (final long item : array) {
			list.add(item);
		}
		return list;
	}

	public static <T> String toString(final List<T> list, final char token, final boolean include_space) {
		final StringBuilder builder = new StringBuilder();
		final int size = list.size();
		for (int i = 0; i < size; i++) {
			final String item_string = String.valueOf(list.get(i));
			if (item_string != null) {
				if (i > 0) {
					builder.append(include_space ? token + " " : token);
				}
				builder.append(item_string);
			}
		}
		return builder.toString();
	}

	public static String toStringForSQL(final List<String> list) {
		final int size = list != null ? list.size() : 0;
		final StringBuilder builder = new StringBuilder();
		for (int i = 0; i < size; i++) {
			if (i > 0) {
				builder.append(',');
			}
			builder.append('?');
		}
		return builder.toString();
	}
}
