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

import org.apache.commons.logging.Log;

public class BakLogger {

	private String name;
	private Log commonsLog;

	protected BakLogger(String name, Log commonsLog) {
		this.name = name;
		this.commonsLog = commonsLog;
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
		commonsLog.trace(message);
	}

	public void trace(Object message, Throwable t) {
		commonsLog.trace(message, t);
	}

	public void debug(Object message) {
		commonsLog.debug(message);
	}

	public void debug(Object message, Throwable t) {
		commonsLog.debug(message, t);
	}

	public void error(Object message) {
		commonsLog.error(message);
	}

	public void error(Object message, Throwable t) {
		commonsLog.error(message, t);
	}

	public void fatal(Object message) {
		commonsLog.fatal(message);
	}

	public void fatal(Object message, Throwable t) {
		commonsLog.fatal(message, t);
	}

	public void info(Object message) {
		commonsLog.info(message);
	}

	public void info(Object message, Throwable t) {
		commonsLog.info(message, t);
	}

	public void warn(Object message) {
		commonsLog.warn(message);
	}

	public void warn(Object message, Throwable t) {
		commonsLog.warn(message, t);
	}
}