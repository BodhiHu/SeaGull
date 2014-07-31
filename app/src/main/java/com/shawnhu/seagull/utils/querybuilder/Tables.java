package com.shawnhu.seagull.utils.querybuilder;

public class Tables implements Selectable {

	private final String[] tables;

	public Tables(final String... tables) {
		this.tables = tables;
	}

	@Override
	public String getSQL() {
		return Utils.toString(tables, ',', false);
	}

}
