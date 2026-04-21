package com.giitotech.product_management.service;

import com.giitotech.product_management.entity.Action;
import com.giitotech.product_management.entity.User;

public interface LogService {
	public void save(Action action, User user);
}
