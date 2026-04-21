package com.giitotech.product_management.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.giitotech.product_management.dto.LatestStockHistory;
import com.giitotech.product_management.service.ProductService;
import com.giitotech.product_management.service.StockHistoryService;

@Controller
public class HomeController {
	private StockHistoryService stockHistoryService;
	private ProductService productService;

	@Autowired
	public HomeController(
			StockHistoryService theStockHistoryService,
			ProductService theProductService
			) {
		stockHistoryService = theStockHistoryService;
		productService = theProductService;
	}

	@GetMapping("/")
	public String showHome(Model theModel) {
		// 最新の商品履歴を取得
		List<LatestStockHistory> lateststockHistoreis = stockHistoryService.getLatestStockHistories();
		theModel.addAttribute("lateststockHistoreis", lateststockHistoreis);

		// Productの件数取得
		long countProducts = productService.getCountProducts();
		String countProductsMessage = countProducts + "件";
		theModel.addAttribute("countProductsMessage", countProductsMessage);
		
		long countOutOfStockProducts = productService.getCountOutOfStockProducts();
		String countOutOfStockProductsMessage = countOutOfStockProducts + "件";
		theModel.addAttribute("countOutOfStockProductsMessage", countOutOfStockProductsMessage);

		return "product-management/home";

	}
}
