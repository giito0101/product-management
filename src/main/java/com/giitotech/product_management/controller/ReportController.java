package com.giitotech.product_management.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class ReportController {
	@GetMapping("/report")
	public String report() {
		return "product-management/report";
	}
	
	@GetMapping("/soldOutItems")
	public String soldOutItems() {
		return "product-management/sold_out_items";
	}
	
	@GetMapping("/lowInStock")
	public String lowInStock() {
		return "product-management/low_in_stock";
	}
}
