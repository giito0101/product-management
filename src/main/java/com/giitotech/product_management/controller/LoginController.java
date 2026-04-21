package com.giitotech.product_management.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
	
	@GetMapping("/loginPage")
	public String showMyLoginPage () {
		return "product-management/login";
	}
	
	// /access-deniedのためのマッピングをリクエストに追加する
	@GetMapping("/access-denied")
	public String showAccessDenied () {
		return "/product-management/access-denied";
	}

}
