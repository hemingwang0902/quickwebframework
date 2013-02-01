package com.quickwebframework.db.orm.quickorm;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.ServiceEvent;

import com.quickorm.config.Database;
import com.quickorm.core.QuickormTemplate;
import com.quickorm.core.impl.QuickormTemplateImpl;
import com.quickwebframework.db.jdbc.DataSourceContext;
import com.quickwebframework.db.jdbc.DataSourceEvent;
import com.quickwebframework.db.jdbc.DataSourceListener;
import com.quickwebframework.db.orm.quickorm.support.Activator;
import com.quickwebframework.framework.FrameworkContext;
import com.quickwebframework.framework.WebContext;

public class QuickormContext extends FrameworkContext {
	private static QuickormContext instance;

	public static QuickormContext getInstance() {
		if (instance == null)
			instance = new QuickormContext();
		return instance;
	}

	// 配置名称与QuickormTemplate映射
	private static Map<String, QuickormTemplate> propertyNamepropertyNameObjectMapMap = new HashMap<String, QuickormTemplate>();
	private DataSourceListener dataSourceListener;

	@Override
	protected BundleContext getBundleContext() {
		return Activator.getContext();
	}

	public QuickormContext() {
		dataSourceListener = new DataSourceListener() {

			@Override
			public void dataSourceChanged(DataSourceEvent event) {
				if (DataSourceEvent.REMOVED == event.getType()) {
					propertyNamepropertyNameObjectMapMap.remove(event
							.getPropertyName());
				}
			}
		};
	}

	@Override
	protected void init(int arg) {
		DataSourceContext.addDataSourceListener(dataSourceListener);
	}

	@Override
	protected void destory(int arg) {
		DataSourceContext.removeDataSourceListener(dataSourceListener);
	}

	@Override
	protected void bundleChanged(BundleEvent event) {

	}

	@Override
	protected void serviceChanged(ServiceEvent event) {

	}

	public static QuickormTemplate getDefaultTemplate() {
		return getTemplate(DataSourceContext.DEFAULT_DATASOURCE_PROPERTY_NAME);
	}

	public static QuickormTemplate getTemplate(String propertyName) {
		if (!propertyNamepropertyNameObjectMapMap.containsKey(propertyName)) {
			// 创建
			propertyNamepropertyNameObjectMapMap.put(propertyName,
					innerGetTemplate(propertyName));
		}
		return propertyNamepropertyNameObjectMapMap.get(propertyName);
	}

	private static QuickormTemplate innerGetTemplate(String propertyName) {
		String configProperty = null;
		if (DataSourceContext.DEFAULT_DATASOURCE_PROPERTY_NAME
				.equals(propertyName)) {
			configProperty = "qwf-db-orm-quickorm.properties";
		} else {
			configProperty = "qwf-db-orm-quickorm." + propertyName
					+ ".properties";
		}
		String quickormPropertyFilePath = WebContext
				.getQwfConfig(configProperty);
		if (quickormPropertyFilePath == null
				|| quickormPropertyFilePath.isEmpty()) {
			throw new RuntimeException("在QuickWebFramework配置文件中未找到配置项："
					+ configProperty);
		}
		quickormPropertyFilePath = WebContext
				.getRealPath(quickormPropertyFilePath);
		File quickormPropertyFile = new File(quickormPropertyFilePath);
		if (!quickormPropertyFile.exists() || !quickormPropertyFile.isFile()) {
			String message = String.format("配置文件 [%s] 不存在!",
					quickormPropertyFilePath);
			throw new RuntimeException(message);
		}
		// 读取配置文件
		Properties prop = null;
		try {
			InputStream inputStream = new FileInputStream(quickormPropertyFile);
			Reader reader = new InputStreamReader(inputStream, "utf-8");
			prop = new Properties();
			prop.load(reader);
			reader.close();
			inputStream.close();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

		DataSource dataSource = DataSourceContext.getDataSource(propertyName);
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
		return quickormTemplateImpl;
	}
}
