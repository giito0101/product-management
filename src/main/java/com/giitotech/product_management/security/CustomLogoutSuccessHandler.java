package com.giitotech.product_management.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import com.giitotech.product_management.entity.Action;
import com.giitotech.product_management.entity.User;
import com.giitotech.product_management.service.LogService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

	private LogService logService;
	
	public CustomLogoutSuccessHandler(
			LogService theLogService) {
		logService = theLogService;
	}

	@Override
	public void onLogoutSuccess(HttpServletRequest request,
			HttpServletResponse response,
			Authentication authentication)
			throws IOException, ServletException {
		System.out.println("In customLogoutSuccessHandler - Logging out user");
		String userName = authentication.getName();
		
		System.out.println("userName=" + userName);

		HttpSession session = request.getSession(false);

		// ログ取得
		User user = (User)session.getAttribute("user");
		if(user != null) {
			logService.save(Action.LOGOUT, user);
		}
		
		// セッションを無効化
		if (session != null) {
			session.invalidate();
		}

		// ログアウト後のリダイレクト先を設定
		response.sendRedirect(request.getContextPath() + "/loginPage?logout");

	}

}
