package com.giitotech.product_management.dto;

import com.giitotech.product_management.entity.Product;

public class ProductStockStatus {
	
	private Product product;
	private int id;

	private String name;
	private String categoryName;

	private int price;
	private int stock;
	private String stockStatus;
	
	// コンストラクターの生成
	public ProductStockStatus() {
	}
	
	public ProductStockStatus(int id, String name, String categoryName, int price, int stock, String stockStatus) {
		this.id = id;
		this.name = name;
		this.price = price;
		this.stock = stock;
		this.stockStatus = stockStatus;
		this.categoryName = categoryName;
	}

	// setter/getterの生成
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCategoryName() {
		return categoryName;
	}
	
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public String getStockStatus() {
		return stockStatus;
	}

	public void setStockStatus(String stockStatus) {
		this.stockStatus = stockStatus;
	}

	// toStringの生成
	@Override
	public String toString() {
		return "ProductStockStatus [product=" + product + ", stockStatus=" + stockStatus + "]";
	}
	
}
