package com.quickwebframework.db.jdbc;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

//数据源代理类
public class DataSourceProxy implements DataSource {

	private DataSource target;

	public DataSourceProxy(DataSource target) {
		setTargetDataSource(target);
	}

	public void setTargetDataSource(DataSource target) {
		this.target = target;
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return target.getLogWriter();
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return target.getLoginTimeout();
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return target.getParentLogger();
	}

	@Override
	public void setLogWriter(PrintWriter arg0) throws SQLException {
		target.setLogWriter(arg0);
	}

	@Override
	public void setLoginTimeout(int arg0) throws SQLException {
		target.setLoginTimeout(arg0);
	}

	@Override
	public boolean isWrapperFor(Class<?> arg0) throws SQLException {
		return target.isWrapperFor(arg0);
	}

	@Override
	public <T> T unwrap(Class<T> arg0) throws SQLException {
		return target.unwrap(arg0);
	}

	@Override
	public Connection getConnection() throws SQLException {
		return target.getConnection();
	}

	@Override
	public Connection getConnection(String arg0, String arg1)
			throws SQLException {
		return target.getConnection(arg0, arg1);
	}
}
