package com.quickwebframework.db.jdbc.mysql.util;

import java.util.Properties;

/**
 * JDBC配置初始化器
 * 
 * @author aaa
 * 
 */
public interface JdbcPropertiesInitializer {
	/**
	 * 初始化JDBC配置
	 * 
	 * @param prop
	 *            JDBC配置
	 */
	public void init(Properties prop);
}
