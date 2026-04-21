package com.giitotech.product_management.service;

import com.giitotech.product_management.dto.UserForm;

import jakarta.servlet.http.HttpSession;

public interface UserSessionService {
	public UserForm getUserForm(HttpSession session);
	public UserForm getUserForm(HttpSession session, int userId);
}
