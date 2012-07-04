package com.quickwebframework.mvc.spring;

import javax.sql.DataSource;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import com.quickwebframework.entity.Log;
import com.quickwebframework.entity.LogFactory;
import com.quickwebframework.service.DatabaseService;

public class TransactionDatabaseService implements DatabaseService {

	public static Log log = LogFactory.getLog(TransactionDatabaseService.class);
	private BundleContext bundleContext;

	// 加了Spring事务支持的DataSource代理对象
	public DataSource dataSourceProxy;

	public TransactionDatabaseService(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
		refreshDataSource();
		bundleContext.addServiceListener(new ServiceListener() {

			@Override
			public void serviceChanged(ServiceEvent arg0) {
				if (arg0.getServiceReference().toString()
						.contains(DatabaseService.class.getName())) {
					refreshDataSource();
				}
			}

		});
	}

	// 刷新DataSource
	private void refreshDataSource() {

		ServiceReference<?>[] serviceReferences = null;
		try {
			serviceReferences = bundleContext.getServiceReferences(
					com.quickwebframework.service.DatabaseService.class
							.getName(), null);
		} catch (Exception e) {
			return;
		}
		if (serviceReferences == null) {
			return;
		}
		log.info("准备刷新com.quickwebframework.mvc.spring.TransactionDatabaseService中的数据源");
		for (ServiceReference<?> serviceReference : serviceReferences) {
			com.quickwebframework.service.DatabaseService databaseService = (com.quickwebframework.service.DatabaseService) bundleContext
					.getService(serviceReference);
			if (TransactionDatabaseService.class.isInstance(databaseService))
				continue;
			DataSource sourceDataSource = databaseService.getDataSource();
			dataSourceProxy = new TransactionAwareDataSourceProxy(
					sourceDataSource);
			return;
		}
	}

	@Override
	public DataSource getDataSource() {
		return dataSourceProxy;
	}
}
