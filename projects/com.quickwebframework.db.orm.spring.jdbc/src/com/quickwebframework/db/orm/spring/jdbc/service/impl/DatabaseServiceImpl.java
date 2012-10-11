package com.quickwebframework.db.orm.spring.jdbc.service.impl;

import javax.sql.DataSource;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.springframework.jdbc.core.JdbcTemplate;

import com.quickwebframework.db.orm.spring.jdbc.service.DatabaseService;
import com.quickwebframework.entity.Log;
import com.quickwebframework.entity.LogFactory;

public class DatabaseServiceImpl implements DatabaseService {
	public static Log log = LogFactory.getLog(DatabaseServiceImpl.class);
	private BundleContext bundleContext;
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;

	public DatabaseServiceImpl(BundleContext bundleContext) {
		this.bundleContext = bundleContext;

		refreshDataSource();
		bundleContext.addServiceListener(new ServiceListener() {

			@Override
			public void serviceChanged(ServiceEvent arg0) {
				if (arg0.getServiceReference()
						.toString()
						.contains(
								com.quickwebframework.service.DatabaseService.class
										.getName())) {
					refreshDataSource();
				}
			}
		});
	}

	// 刷新DataSource
	private void refreshDataSource() {
		DataSource tmpDataSource = null;
		ServiceReference<?>[] serviceReferences = null;
		try {
			serviceReferences = bundleContext.getServiceReferences(
					com.quickwebframework.service.DatabaseService.class
							.getName(), null);
		} catch (Exception e) {
			return;
		}
		if (serviceReferences == null)
			return;
		log.info("准备刷新com.quickwebframework.db.orm.spring.jdbc.service.impl.DatabaseServiceImpl中的数据源");
		for (ServiceReference<?> serviceReference : serviceReferences) {
			com.quickwebframework.service.DatabaseService databaseService = (com.quickwebframework.service.DatabaseService) bundleContext
					.getService(serviceReference);
			tmpDataSource = databaseService.getDataSource();
			if ("org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy"
					.equals(tmpDataSource.getClass().getName())) {
				break;
			}
		}
		dataSource = tmpDataSource;

		// 初始化quickormTemplate
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
}
