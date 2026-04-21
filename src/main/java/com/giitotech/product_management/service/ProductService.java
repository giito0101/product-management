package com.giitotech.product_management.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.giitotech.product_management.dto.ProductForm;
import com.giitotech.product_management.dto.ProductStockStatus;
import com.giitotech.product_management.entity.Product;

public interface ProductService {
	public Product save(ProductForm productForm);

    public Product findById(Integer id);
    
    public void deleteById(int id);
    
    public Page<ProductStockStatus> findPaginatedByKeyword(Pageable pageable, String[] search);
    
    public Product findProductByNameAndCategoryName(String name, int categoryId);
    
    public long getCountProducts();
    
    public long getCountOutOfStockProducts();

	public List<ProductStockStatus> findSoldOutItems(String sortBy, String sortOrder);
	
	public List<ProductStockStatus> findLowInStockItems(String sortBy, String sortOrder);
	
	public List<ProductStockStatus> findListProductStockStatusByKeywordAndDate(String[] keywords, LocalDate startDate,LocalDate endDate, String stockCondition, String sortBy, String direction);

	public boolean isProductExist(String name, int categoryId);

	public String getCategoryNameById(int categoryId);

	public int calculateQuantityDifference(ProductForm productForm, Product preProduct);

	public String getRedirectPath(ProductForm productForm);

	public boolean isProductExist(Integer id);
	
	public List<ProductStockStatus> getProductsByFileType(String fileType, String sortBy, String direction);
}
