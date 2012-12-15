package com.quickwebframework.service;

import javax.sql.DataSource;

/**
 * 服务库服务
 * 
 * @author aaa
 * 
 */
public interface DatabaseService {
	/**
	 * 得到数据源对象
	 * 
	 * @return
	 */
	public DataSource getDataSource();

	/**
	 * 重新加载配置
	 */
	public void reloadConfig();
}
