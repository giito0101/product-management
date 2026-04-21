package com.giitotech.product_management.dao;

import java.util.List;

import com.giitotech.product_management.dto.LatestStockHistory;

public interface StockHistoryDao {
	public List<LatestStockHistory> findLatestStockHistories(int showCount);
}
