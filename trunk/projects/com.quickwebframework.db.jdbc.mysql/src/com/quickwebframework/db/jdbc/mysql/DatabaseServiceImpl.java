package com.quickwebframework.db.jdbc.mysql;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

import com.quickwebframework.service.DatabaseService;

public class DatabaseServiceImpl implements DatabaseService {

	private DataSource dataSource;

	public DatabaseServiceImpl(Properties prop) {

		// 初始化Datasource
		BasicDataSource basicDataSource = new BasicDataSource();
		dataSource = basicDataSource;

		if (prop.containsKey("jdbc.driver")) {
			basicDataSource.setDriverClassName(prop.getProperty("jdbc.driver"));
		}
		if (prop.containsKey("jdbc.url")) {
			basicDataSource.setUrl(prop.getProperty("jdbc.url"));
		}
		if (prop.containsKey("jdbc.username")) {
			basicDataSource.setUsername(prop.getProperty("jdbc.username"));
		}
		if (prop.containsKey("jdbc.password")) {
			basicDataSource.setPassword(prop.getProperty("jdbc.password"));
		}
		if (prop.containsKey("jdbc.initialSize")) {
			basicDataSource.setInitialSize(Integer.parseInt(prop
					.getProperty("jdbc.initialSize")));
		}
		if (prop.containsKey("jdbc.maxActive")) {
			basicDataSource.setMaxActive(Integer.parseInt(prop
					.getProperty("jdbc.maxActive")));
		}
		if (prop.containsKey("jdbc.maxIdle")) {
			basicDataSource.setMaxIdle(Integer.parseInt(prop
					.getProperty("jdbc.maxIdle")));
		}
		if (prop.containsKey("jdbc.minIdle")) {
			basicDataSource.setMinIdle(Integer.parseInt(prop
					.getProperty("jdbc.minIdle")));
		}
		if (prop.containsKey("jdbc.testOnBorrow")) {
			basicDataSource.setTestOnBorrow(Boolean.parseBoolean(prop
					.getProperty("jdbc.testOnBorrow")));
		}
		if (prop.containsKey("jdbc.timeBetweenEvictionRunsMillis")) {
			basicDataSource
					.setTimeBetweenEvictionRunsMillis(Integer.parseInt(prop
							.getProperty("jdbc.timeBetweenEvictionRunsMillis")));
		}
	}

	@Override
	public DataSource getDataSource() {
		return dataSource;
	}
}