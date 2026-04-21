package com.giitotech.product_management.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ProductForm {

	Integer id;
	
	@NotNull(message = "商品名を入力してください")
	@Size(min=2, max=100, message="１文字より多く文字を入力してください")
	String name;
	
	@NotNull(message = "カテゴリを選択してください")
	@Min(value=0, message="不正なカテゴリが設定されています")
	Integer categoryId;

	@Max(value=1000000, message = "在庫数が不正です")
	@Min(value=0, message = "在庫数が不正です")
	int stock;
	
	@Max(value=1000000000, message = "金額が不正です")
	@Min(value=10, message = "金額が不正です")
	int price;
	
	String categoryName;
	
	public ProductForm() {
		
	}

	public ProductForm(
		Integer id,
		String name,
		Integer categoryId,
		int stock,
		int price
		) {
		this.id = id;
		this.name = name;
		this.categoryId = categoryId;
		this.stock = stock;
		this.price = price;
	}

	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	
	public void setName(String name) {
		this.name = name;
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public String getCategoryName() {
		return categoryName;
	}
	
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
}
