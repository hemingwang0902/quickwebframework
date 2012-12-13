package com.quickwebframework.db.jdbc.mysql.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.osgi.framework.ServiceRegistration;

import com.quickwebframework.db.jdbc.mysql.Activator;
import com.quickwebframework.db.jdbc.mysql.util.JdbcPropertiesInitializer;
import com.quickwebframework.service.DatabaseService;

public class DatabaseServiceImpl implements DatabaseService {

	private DataSource dataSource;

	private String jdbcPropertyFilePath;

	private JdbcPropertiesInitializer jdbcPropertiesInitializer;

	public JdbcPropertiesInitializer getJdbcPropertiesInitializer() {
		return jdbcPropertiesInitializer;
	}

	public void setJdbcPropertiesInitializer(
			JdbcPropertiesInitializer jdbcPropertiesInitializer) {
		this.jdbcPropertiesInitializer = jdbcPropertiesInitializer;
	}

	public DatabaseServiceImpl(String jdbcPropertyFilePath) {
		this.jdbcPropertyFilePath = jdbcPropertyFilePath;
		reloadConfig();
	}

	private void initDataSource() throws IOException {
		File jdbcPropertyFile = new File(jdbcPropertyFilePath);
		if (!jdbcPropertyFile.exists() || !jdbcPropertyFile.isFile()) {
			String message = String.format("Config file [%s] not exist!",
					jdbcPropertyFilePath);
			throw new IOException(message);
		}

		InputStream inputStream = new FileInputStream(jdbcPropertyFile);
		Reader reader = new InputStreamReader(inputStream, "utf-8");
		Properties prop = new Properties();
		prop.load(reader);
		reader.close();
		inputStream.close();

		// 初始化配置
		if (jdbcPropertiesInitializer != null) {
			jdbcPropertiesInitializer.init(prop);
		}

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

	@Override
	public void reloadConfig() {
		dataSource = null;
		try {
			initDataSource();
		} catch (IOException e) {
			throw new RuntimeException("加载数据库配置文件时出错，原因：" + e.getMessage(), e);
		}
		// 如果已经注册，则先取消注册
		unregisterService();
		// 重新注册服务
		registerService();
	}

	private ServiceRegistration<?> databaseServiceRegistration;

	// 注册
	public void registerService() {
		databaseServiceRegistration = Activator.getContext().registerService(
				DatabaseService.class.getName(), this, null);
	}

	// 取消注册
	public void unregisterService() {
		if (databaseServiceRegistration != null) {
			databaseServiceRegistration.unregister();
			databaseServiceRegistration = null;
		}
	}
}