package com.giitotech.product_management.service;

import java.sql.Timestamp;

import org.springframework.stereotype.Service;

import com.giitotech.product_management.dao.LogRepository;
import com.giitotech.product_management.entity.Action;
import com.giitotech.product_management.entity.Log;
import com.giitotech.product_management.entity.User;
import com.giitotech.product_management.util.DataTimeUtil;

import jakarta.transaction.Transactional;

@Service
public class LogServiceImpl implements LogService{
	private LogRepository logRepository;
	public LogServiceImpl (LogRepository logRepository) {
		this.logRepository = logRepository;
	}

	@Transactional
	@Override
	public void save(Action action, User user) {
		Timestamp timestamp = DataTimeUtil.getTimestampInJapanTime();
		Log log = new Log(user, action, timestamp);
		logRepository.save(log);
	}

}
