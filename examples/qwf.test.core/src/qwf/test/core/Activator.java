package qwf.test.core;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.quickwebframework.entity.Log;
import com.quickwebframework.entity.LogFactory;
import com.quickwebframework.framework.IocContext;
import com.quickwebframework.ioc.spring.util.ApplicationContextListener;
import com.quickwebframework.ioc.spring.util.BundleApplicationContextUtils;
import com.quickwebframework.service.DatabaseService;

public class Activator implements BundleActivator {

	private static Log log = LogFactory.getLog(Activator.class);

	private static BundleContext context;

	public static BundleContext getContext() {
		return context;
	}

	// 自定义Bean名称生成器类
	public class MySpringBeanNameGenerator implements BeanNameGenerator {

		public String generateBeanName(BeanDefinition arg0,
				BeanDefinitionRegistry arg1) {
			String beanClassName = arg0.getBeanClassName();
			String beanName = "quickwebframework_bean_"
					+ beanClassName.substring(0, 1).toLowerCase()
					+ beanClassName.substring(1);
			return beanName;
		}
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;

		DatabaseService databaseService = (DatabaseService) bundleContext
				.getService(bundleContext
						.getServiceReference(DatabaseService.class.getName()));
		databaseService.reloadConfig();

		// 添加一个ApplicationContext监听器
		BundleApplicationContextUtils
				.addApplicationContextListener(new ApplicationContextListener() {

					// 开始时
					public void contextStarting(
							ApplicationContext applicationContext, Bundle bundle) {
						if (AnnotationConfigApplicationContext.class
								.isInstance(applicationContext)) {
							AnnotationConfigApplicationContext annotationConfigApplicationContext = (AnnotationConfigApplicationContext) applicationContext;
							// 设置Bean名称生成器
							annotationConfigApplicationContext
									.setBeanNameGenerator(new MySpringBeanNameGenerator());
						}
					}

					// 开始后
					public void contextStarted(
							ApplicationContext applicationContext, Bundle bundle) {
					}

				});

		log.warn("-----"
				+ IocContext.getBeanDefinitionCount(
						bundleContext.getBundle()));
		log.info("qwf.test.core插件已启动!");
	}

	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;

		log.info("qwf.test.core插件已停止!");
	}

}
