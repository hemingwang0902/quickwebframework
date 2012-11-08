package com.quickwebframework.db.orm.quickorm.service.impl;

import java.util.Properties;

import javax.sql.DataSource;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

import com.quickorm.config.Database;
import com.quickorm.core.QuickormTemplate;
import com.quickorm.core.impl.QuickormTemplateImpl;
import com.quickwebframework.db.orm.quickorm.service.DatabaseService;
import com.quickwebframework.entity.Log;
import com.quickwebframework.entity.LogFactory;

public class DatabaseServiceImpl implements DatabaseService {

	public static Log log = LogFactory.getLog(DatabaseServiceImpl.class);

	private Properties prop;
	private BundleContext bundleContext;
	private DataSource dataSource;
	private QuickormTemplate quickormTemplate;

	public DatabaseServiceImpl(BundleContext bundleContext, Properties prop) {
		this.bundleContext = bundleContext;
		this.prop = prop;

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

		log.debug("准备刷新com.quickwebframework.db.orm.quickorm.service.impl.DatabaseServiceImpl中的数据源");
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
		QuickormTemplateImpl quickormTemplateImpl = new QuickormTemplateImpl(
				dataSource);
		if (prop.containsKey("quickorm.database")) {
			quickormTemplateImpl.setDatabase(Database.valueOf(prop
					.getProperty("quickorm.database")));
		} else if (prop.containsKey("quickorm.showSql")) {
			quickormTemplateImpl.setShowSql(Boolean.valueOf(prop
					.getProperty("quickorm.showSql")));
		} else if (prop.containsKey("quickorm.showSqlLogLevel")) {
			quickormTemplateImpl.setShowSqlLogLevel(prop
					.getProperty("quickorm.showSqlLogLevel"));
		}
		quickormTemplate = quickormTemplateImpl;
	}

	@Override
	public QuickormTemplate getQuickormTemplate() {
		return quickormTemplate;
	}
}
