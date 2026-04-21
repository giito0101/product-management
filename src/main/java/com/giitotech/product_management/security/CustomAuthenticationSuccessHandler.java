package com.giitotech.product_management.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.giitotech.product_management.entity.Action;
import com.giitotech.product_management.entity.User;
import com.giitotech.product_management.service.LogService;
import com.giitotech.product_management.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
	
	private UserService userService;
	private LogService logService;
	
	public CustomAuthenticationSuccessHandler(UserService theUserService, LogService theLogService) {
		userService = theUserService;
		logService = theLogService;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) 
			throws IOException, ServletException {
		
		System.out.println("In customAuthenticationSuccessHandler");
		
		String userName = authentication.getName();
		
		System.out.println("userName=" + userName);
		
		User theUser = userService.findByUserName(userName);
		
		HttpSession session = request.getSession();
		
		// sessionを設定
		session.setAttribute("user", theUser);
		
		// ログ取得
		User user = (User)session.getAttribute("user");
		if(user != null) {
			logService.save(Action.LOGIN, user);
		}
		
		// 進むホームページへ
		response.sendRedirect(request.getContextPath() + "/");
	}

}
