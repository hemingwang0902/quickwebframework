package com.quickwebframework.framework;

public class ContextManager {
	public static void initAllContext() {
		OsgiContext.getInstance();
		LogContext.getInstance();
		WebContext.getInstance();
		IocContext.getInstance();
		ThreadContext.getInstance();

		for (FrameworkContext context : FrameworkContext.getContexts()) {
			context.init();
		}
	}

	public static void destoryAllContext() {
		for (FrameworkContext context : FrameworkContext.getContexts()) {
			context.destory();
		}
	}
}
