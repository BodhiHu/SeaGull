package com.shawnhu.seagull.utils.querybuilder.query;


import com.shawnhu.seagull.utils.querybuilder.SQLLang;

public interface IBuilder<T extends SQLLang> {

	public T build();

	/**
	 * Equivalent to {@link #build()}.{@link #SQLLang.getSQL()}
	 * 
	 * @return
	 */
	public String buildSQL();

}
