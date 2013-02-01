package com.quickwebframework.db.jdbc;

/**
 * 数据源监听器
 * 
 * @author aaa
 * 
 */
public interface DataSourceListener {
	/**
	 * 数据源改变时
	 * 
	 * @param event
	 */
	public void dataSourceChanged(DataSourceEvent event);
}
