package com.quickwebframework.service;

import javax.sql.DataSource;

/**
 * 服务库服务
 * 
 * @author aaa
 * 
 */
public interface DatabaseService {
	public DataSource getDataSource();
}
