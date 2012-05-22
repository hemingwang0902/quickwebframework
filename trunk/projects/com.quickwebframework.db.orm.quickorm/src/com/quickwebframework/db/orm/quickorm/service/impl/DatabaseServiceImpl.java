package com.quickwebframework.db.orm.quickorm.service.impl;

import java.util.Properties;

import javax.sql.DataSource;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.quickorm.config.Database;
import com.quickorm.core.QuickormTemplate;
import com.quickorm.core.impl.QuickormTemplateImpl;
import com.quickwebframework.db.orm.quickorm.service.DatabaseService;

public class DatabaseServiceImpl implements DatabaseService {

	private Properties prop;
	private BundleContext bundleContext;
	private DataSource dataSource;
	private QuickormTemplate quickormTemplate;

	public DatabaseServiceImpl(BundleContext bundleContext, Properties prop) {
		this.bundleContext = bundleContext;
		this.prop = prop;
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
	public QuickormTemplate getQuickormTemplate() {
		DataSource tmpDS = getDataSource();
		if (dataSource == null || !dataSource.equals(tmpDS)) {
			dataSource = tmpDS;

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
		return quickormTemplate;
	}
}
