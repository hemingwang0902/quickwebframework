package com.quickwebframework.db.orm.spring.jdbc.service;

import org.springframework.jdbc.core.JdbcTemplate;

public interface DatabaseService {
	public JdbcTemplate getJdbcTemplate();
}
