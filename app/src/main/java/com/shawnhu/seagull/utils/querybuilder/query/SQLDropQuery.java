package com.shawnhu.seagull.utils.querybuilder.query;


import com.shawnhu.seagull.utils.querybuilder.SQLLang;

public class SQLDropQuery implements SQLLang {

	private final String table;

	public SQLDropQuery(final String table) {
		if (table == null) throw new NullPointerException();
		this.table = table;
	}

	@Override
	public String getSQL() {
		return String.format("DROP TABLE %s", table);
	}

}
