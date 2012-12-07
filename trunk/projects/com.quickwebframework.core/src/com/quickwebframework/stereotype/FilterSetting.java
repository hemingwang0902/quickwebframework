package com.quickwebframework.stereotype;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

@Target(value = { ElementType.TYPE })
@Retention(value = RetentionPolicy.RUNTIME)
public @interface FilterSetting {
	/**
	 * 过滤器顺序
	 * 
	 * @return
	 */
	public int index();
}
