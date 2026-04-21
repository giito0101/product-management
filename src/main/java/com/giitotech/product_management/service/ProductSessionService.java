package com.giitotech.product_management.service;

import com.giitotech.product_management.dto.ProductForm;

import jakarta.servlet.http.HttpSession;

public interface ProductSessionService {
	public ProductForm getProductForm(HttpSession session, String referer);
	public ProductForm getProductForm(HttpSession session, String referer, int productId);
}
