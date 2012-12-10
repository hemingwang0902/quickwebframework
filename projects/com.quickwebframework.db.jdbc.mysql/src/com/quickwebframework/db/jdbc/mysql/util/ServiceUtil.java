package com.quickwebframework.db.jdbc.mysql.util;

import com.quickwebframework.db.jdbc.mysql.Activator;

public class ServiceUtil {

	/**
	 * 设置JDBC配置
	 * 
	 * @param initializer
	 */
	public static void setJdbcPropertiesInitializer(
			JdbcPropertiesInitializer initializer) {
		Activator.getDatabaseService()
				.setJdbcPropertiesInitializer(initializer);
	}
}
