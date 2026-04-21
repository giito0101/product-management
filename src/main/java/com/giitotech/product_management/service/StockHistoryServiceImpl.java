package com.giitotech.product_management.service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.giitotech.product_management.dao.StockHistoryDao;
import com.giitotech.product_management.dao.StockHistoryRepository;
import com.giitotech.product_management.dto.LatestStockHistory;
import com.giitotech.product_management.entity.Product;
import com.giitotech.product_management.entity.StockHistory;
import com.giitotech.product_management.util.DataTimeUtil;

@Service
public class StockHistoryServiceImpl implements StockHistoryService {
	private StockHistoryRepository stockHistoryRepository;
	private ProductService productService;
	private StockHistoryDao stockHistoryDao;

	@Autowired
	public StockHistoryServiceImpl(
			StockHistoryRepository stockHistoryRepository,
			ProductService productService,
			StockHistoryDao stockHistoryDao) {
		this.stockHistoryRepository = stockHistoryRepository;
		this.productService = productService;
		this.stockHistoryDao = stockHistoryDao;

	}

	@Override
	public void save(int quantityDifference, Product savedProduct) {
		StockHistory stockHistory = new StockHistory(quantityDifference, savedProduct);

		// 日付を設定
		Timestamp timestamp = DataTimeUtil.getTimestampInJapanTime();

		stockHistory.setTimestamp(timestamp);

		stockHistoryRepository.save(stockHistory);
	}

	@Override
	public List<LatestStockHistory> getLatestStockHistories() {
		int showCount = 15;
	    List<LatestStockHistory> list = stockHistoryDao.findLatestStockHistories(showCount);
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");
	    
	    for (LatestStockHistory dto : list) {
	        // Timestampがnullでなければ、指定フォーマットに変換
	        if (dto.getLatestTimestamp() != null) {
	            String formatted = formatter.format(dto.getLatestTimestamp());
	            dto.setFormattedLatestTimestamp(formatted);
	        }
	        
			String stockStatus;

			// 在庫数に応じてステータスを設定
			if(dto.getChangeQuantity() == 0) {
				stockStatus = "新規";
			} else if (dto.getHistoryStock() == 0) {
				stockStatus = "在庫無し";
			} else if (dto.getHistoryStock() <= 5) {
				stockStatus = "残り少ない";
			} else {
				stockStatus = "在庫有り";
			}

			// DTOにステータスを設定
			dto.setStockStatus(stockStatus);
	    }
	    return list;
	}
}
