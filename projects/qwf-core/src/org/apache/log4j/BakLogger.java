/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.log4j;

import com.quickwebframework.entity.Log;

public class BakLogger {

	private String name;
	private Log qwfLog;

	protected BakLogger(String name, Log qwfLog) {
		this.name = name;
		this.qwfLog = qwfLog;
	}

	static public BakLogger getLogger(String name) {
		return BakLogManager.getLogger(name);
	}

	static public BakLogger getLogger(Class<?> clazz) {
		return BakLogManager.getLogger(clazz.getName());
	}

	public static BakLogger getRootLogger() {
		return BakLogManager.getRootLogger();
	}

	public final String getName() {
		return name;
	}

	public boolean isDebugEnabled() {
		return true;
	}

	public boolean isInfoEnabled() {
		return true;
	}

	public boolean isTraceEnabled() {
		return true;
	}

	public void trace(Object message) {
		qwfLog.trace(message);
	}

	public void trace(Object message, Throwable t) {
		qwfLog.trace(message, t);
	}

	public void debug(Object message) {
		qwfLog.debug(message);
	}

	public void debug(Object message, Throwable t) {
		qwfLog.debug(message, t);
	}

	public void error(Object message) {
		qwfLog.error(message);
	}

	public void error(Object message, Throwable t) {
		qwfLog.error(message, t);
	}

	public void fatal(Object message) {
		qwfLog.fatal(message);
	}

	public void fatal(Object message, Throwable t) {
		qwfLog.fatal(message, t);
	}

	public void info(Object message) {
		qwfLog.info(message);
	}

	public void info(Object message, Throwable t) {
		qwfLog.info(message, t);
	}

	public void warn(Object message) {
		qwfLog.warn(message);
	}

	public void warn(Object message, Throwable t) {
		qwfLog.warn(message, t);
	}
}