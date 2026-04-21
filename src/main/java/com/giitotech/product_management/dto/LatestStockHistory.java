package com.giitotech.product_management.dto;

import java.sql.Timestamp;

public class LatestStockHistory {
	
    private String productName;
    private String categoryName;
    private Integer productStock;
    private Integer changeQuantity;
    private Timestamp latestTimestamp;
    private String stockStatus;
    private String formattedLatestTimestamp;
    private Long historyStock;
    

	//コンストラクターを生成
    public LatestStockHistory() {
    	
    }
    
    public LatestStockHistory(
    		String productName, 
    		String categoryName, 
    		Integer productStock,
			Integer changeQuantity, 
			Timestamp latestTimestamp,
			String formattedLatestTimestamp,
			Long historyStock
			) {
		this.productName = productName;
		this.categoryName = categoryName;
		this.productStock = productStock;
		this.changeQuantity = changeQuantity;
		this.latestTimestamp = latestTimestamp;
		this.formattedLatestTimestamp = formattedLatestTimestamp;
		this.historyStock = historyStock;
	}


	// getterとsetter
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public Integer getProductStock() {
		return productStock;
	}
	public void setProductStock(Integer productStock) {
		this.productStock = productStock;
	}
	public Integer getChangeQuantity() {
		return changeQuantity;
	}
	public void setChangeQuantity(Integer changeQuantity) {
		this.changeQuantity = changeQuantity;
	}
	public Timestamp getLatestTimestamp() {
		return latestTimestamp;
	}
	public void setLatestTimestamp(Timestamp latestTimestamp) {
		this.latestTimestamp = latestTimestamp;
	}
	public String getStockStatus() {
		return stockStatus;
	}
	
	public void setStockStatus(String stockStatus) {
		this.stockStatus = stockStatus;
	}
	
    // フォーマットされた日付を取得
    public String getFormattedLatestTimestamp() {
    	return formattedLatestTimestamp;
    }

    public void setFormattedLatestTimestamp(String formattedLatestTimestamp) {
    	this.formattedLatestTimestamp = formattedLatestTimestamp;
    }
    
    public Long getHistoryStock() {
    	return historyStock;
    }
    
    public void setHistoryStock(Long historyStock) {
    	this.historyStock = historyStock;
    }
    
	@Override
	public String toString() {
		return "LatestStockHistory [productName=" + productName + ", categoryName=" + categoryName + ", productStock="
				+ productStock + ", changeQuantity=" + changeQuantity + ", latestTimestamp=" + latestTimestamp
				+ ", stockStatus=" + stockStatus + "]";
	}

}
