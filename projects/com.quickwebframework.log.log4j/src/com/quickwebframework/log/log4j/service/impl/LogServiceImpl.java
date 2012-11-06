package com.quickwebframework.log.log4j.service.impl;

import java.util.HashMap;
import java.util.Map;

import com.quickwebframework.entity.Log;
import com.quickwebframework.log.log4j.entity.impl.LogImpl;
import com.quickwebframework.service.LogService;

public class LogServiceImpl implements LogService {
	Map<String, Log> logMap;

	public LogServiceImpl() {
		logMap = new HashMap<String, Log>();

	}

	@Override
	public Log getLog(String name) {
		Log log = null;
		synchronized (logMap) {
			if (logMap.containsKey(name)) {
				log = logMap.get(name);
			} else {
				log = new LogImpl(name);
				logMap.put(name, log);
			}
		}
		return log;
	}

	@Override
	public Log getLog(Class<?> clazz) {
		return getLog(clazz.getName());
	}
}
