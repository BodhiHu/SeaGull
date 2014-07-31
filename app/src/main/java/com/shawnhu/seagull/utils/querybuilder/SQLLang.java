package com.shawnhu.seagull.utils.querybuilder;

public interface SQLLang extends Cloneable {

	/**
	 * Build SQL query string
	 * 
	 * @return SQL query
	 */
	public String getSQL();
}
