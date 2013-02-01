package com.quickwebframework.db.jdbc;

import javax.sql.DataSource;

public class DataSourceEvent {
	/**
	 * 数据源被创建
	 */
	public final static int CREATED = 0x00000000;
	/**
	 * 数据源被移除
	 */
	public final static int REMOVED = 0x00000001;

	private String propertyName;
	private DataSource dataSource;
	private int type;

	/**
	 * 得到配置名称
	 * 
	 * @return
	 */
	public String getPropertyName() {
		return propertyName;
	}

	/**
	 * 得到数据库
	 * 
	 * @return
	 */
	public DataSource getDataSource() {
		return dataSource;
	}

	/**
	 * 得到事件类型
	 * 
	 * @return
	 */
	public int getType() {
		return type;
	}

	/**
	 * 构造函数
	 * 
	 * @param dataSource
	 *            数据源
	 * @param type
	 *            事件类型
	 */
	public DataSourceEvent(String propertyName, DataSource dataSource, int type) {
		this.propertyName = propertyName;
		this.dataSource = dataSource;
		this.type = type;
	}
}
