package com.giitotech.product_management.service;

import java.util.List;

import com.giitotech.product_management.dto.LatestStockHistory;
import com.giitotech.product_management.entity.Product;

public interface StockHistoryService {
	public void save(int quantityDifference, Product product);
	
    public List<LatestStockHistory> getLatestStockHistories();
}
