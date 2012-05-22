package com.quickwebframework.log.log4j;

import com.quickwebframework.entity.Log;
import com.quickwebframework.service.LogService;

public class LogServiceImpl implements LogService {
	private Log log;

	public LogServiceImpl() {
		log = new LogImpl();
	}

	@Override
	public Log getLog() {
		return log;
	}
}
