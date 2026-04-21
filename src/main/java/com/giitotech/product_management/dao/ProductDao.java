package com.giitotech.product_management.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.giitotech.product_management.entity.Product;

public interface ProductDao {
	
	public Page<Product> findProductByKeyword(Pageable pageable, String[] keywords);

	public Product findProductByNameAndCategoryName(String name, int categoryId);
	
//	public Page<Product> findProductsWithCategory(Pageable pageable);
	
	public long countProducts();
	
	public long countOutofStockProducts();
	
	public List<Product> findSoldOutItems(String sortBy, String sortOrder);
	
	public List<Product> findLowInStockItems(String sortBy, String sortOrder);
	
	public List<Product> findListProductByKeywordAndDate(String[] keywords, LocalDate startDate, LocalDate endDate, String stockType, String sortBy, String direction);

}
