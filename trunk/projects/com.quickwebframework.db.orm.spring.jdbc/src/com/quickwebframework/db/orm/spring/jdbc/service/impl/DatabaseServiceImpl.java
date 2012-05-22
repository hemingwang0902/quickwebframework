package com.quickwebframework.db.orm.spring.jdbc.service.impl;

import javax.sql.DataSource;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.springframework.jdbc.core.JdbcTemplate;

import com.quickwebframework.db.orm.spring.jdbc.service.DatabaseService;

public class DatabaseServiceImpl implements DatabaseService {

	private BundleContext bundleContext;
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;

	public DatabaseServiceImpl(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

	private DataSource getDataSource() {
		ServiceReference serviceReference = bundleContext
				.getServiceReference(com.quickwebframework.service.DatabaseService.class
						.getName());
		if (serviceReference == null) {
			return null;
		}
		com.quickwebframework.service.DatabaseService databaseService = (com.quickwebframework.service.DatabaseService) bundleContext
				.getService(serviceReference);
		return databaseService.getDataSource();
	}

	@Override
	public JdbcTemplate getJdbcTemplate() {
		DataSource tmpDS = getDataSource();
		if (dataSource == null || !dataSource.equals(tmpDS)) {
			dataSource = tmpDS;
			jdbcTemplate = new JdbcTemplate(dataSource);
		}
		return jdbcTemplate;
	}
}
