package com.giitotech.product_management.service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.giitotech.product_management.dao.CategoryRepository;
import com.giitotech.product_management.dao.ProductDao;
import com.giitotech.product_management.dao.ProductRepository;
import com.giitotech.product_management.dto.ProductForm;
import com.giitotech.product_management.dto.ProductStockStatus;
import com.giitotech.product_management.entity.Category;
import com.giitotech.product_management.entity.Product;
import com.giitotech.product_management.util.DataTimeUtil;

import jakarta.transaction.Transactional;

@Service
public class ProductServiceImpl implements ProductService {
	private ProductRepository productRepository;
	private ProductDao productDao;
	private CategoryRepository categoryRepository;
	private CategoryService categoryService;
	private Logger logger = Logger.getLogger(getClass().getName());

	@Autowired
	public ProductServiceImpl(
			ProductRepository productRepository,
			ProductDao productDao,
			CategoryRepository categoryRepository,
			CategoryService categoryService) {
		this.productRepository = productRepository;
		this.productDao = productDao;
		this.categoryRepository = categoryRepository;
		this.categoryService = categoryService;
	}

	@Override
	@Transactional
	public Product save(ProductForm productForm) {
		Optional<Category> cateogryOpt = categoryRepository.findById(productForm.getCategoryId());

		Product savedProduct = null;
		if (cateogryOpt.isPresent()) {
			// 結果がある場合の処理
			Category cateogry = cateogryOpt.get();
			Product product = new Product(productForm.getName(), productForm.getPrice(), productForm.getStock(),
					cateogry);

			// productForm idがnullじゃなければproductにidをsetする
			if (productForm.getId() != null) {
				product.setId(productForm.getId());
			}
			Timestamp timestamp = DataTimeUtil.getTimestampInJapanTime();

			// 最新更新日を設定
			product.setLastUpdated(timestamp);

			savedProduct = productRepository.save(product);
			logger.info("Successfully created product: " + productForm.getName());
		} else {
			// 結果がない場合の処理
			logger.warning("CategoryIdが見つかりません");
		}
		return savedProduct;
	}

	@Override
	public Product findById(Integer id) {
		return productRepository.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void deleteById(int id) {
		productRepository.deleteById(id);
	}

	public Page<ProductStockStatus> getProductsWithStockStatus(Page<Product> products) {

		Page<ProductStockStatus> productStockStatuses = products.map(product -> {
			String stockStatus;
			if (product.getStock() == 0) {
				stockStatus = "在庫無し";
			} else if (product.getStock() <= 5) {
				stockStatus = "残り少ない";
			} else {
				stockStatus = "在庫有り";
			}
			ProductStockStatus productStockStatus = new ProductStockStatus(product.getId(), product.getName(),
					product.getCategory().getName(), product.getPrice(), product.getStock(), stockStatus);
			return productStockStatus;
		});

		return productStockStatuses;
	}

	public Page<ProductStockStatus> findPaginatedByKeyword(Pageable pageable, String[] keywords) {
		Page<Product> products = productDao.findProductByKeyword(pageable, keywords);
		return getProductsWithStockStatus(products);
	}

	public Product findProductByNameAndCategoryName(String name, int categoryId) {
		return productDao.findProductByNameAndCategoryName(name, categoryId);
	}

	@Override
	public long getCountProducts() {
		return productDao.countProducts();
	}

	@Override
	public long getCountOutOfStockProducts() {
		return productDao.countOutofStockProducts();
	}

	@Override
	public List<ProductStockStatus> findSoldOutItems(String sortBy, String sortOrder) {
		List<Product> products = productDao.findSoldOutItems(sortBy, sortOrder);
		String stockStatus = "在庫無し";
		return getListProductsWithStockStatus(products, stockStatus);
	}

	public List<ProductStockStatus> getListProductsWithStockStatus(List<Product> products, String stockStatus) {

		List<ProductStockStatus> productStockStatuses = products.stream()
				.map(product -> {
					return new ProductStockStatus(
							product.getId(),
							product.getName(),
							product.getCategory().getName(),
							product.getPrice(),
							product.getStock(),
							stockStatus);
				})
				.collect(Collectors.toList());

		return productStockStatuses;
	}

	@Override
	public List<ProductStockStatus> findLowInStockItems(String sortBy, String sortOrder) {
		List<Product> products = productDao.findLowInStockItems(sortBy, sortOrder);
		String stockStatus = "残り少ない";
		return getListProductsWithStockStatus(products, stockStatus);
	}

	@Override
	public List<ProductStockStatus> findListProductStockStatusByKeywordAndDate(
			String[] keywords,
			LocalDate startDate,
			LocalDate endDate, 
			String stockType, 
			String sortBy, 
			String direction) {
		List<Product> products = productDao.findListProductByKeywordAndDate(
				keywords, 
				startDate, 
				endDate, 
				stockType,
				sortBy, 
				direction);
		return getProductsWithStockStatus(products);
	}

	public List<ProductStockStatus> getProductsWithStockStatus(List<Product> products) {

		List<ProductStockStatus> productStockStatuses = new ArrayList<>();

		for (Product product : products) {
			String stockStatus;
			if (product.getStock() == 0) {
				stockStatus = "在庫無し";
			} else if (product.getStock() <= 5) {
				stockStatus = "残り少ない";
			} else {
				stockStatus = "在庫有り";
			}
			ProductStockStatus productStockStatus = new ProductStockStatus(
					product.getId(), 
					product.getName(),
					product.getCategory().getName(), 
					product.getPrice(), 
					product.getStock(), 
					stockStatus);
			productStockStatuses.add(productStockStatus);
		}

		return productStockStatuses;
	}

	@Override
	public boolean isProductExist(String productName, int categoryId) {
		Product existing = findProductByNameAndCategoryName(
				productName,
				categoryId);

		return existing != null;
	}

	// categoryId に一致する categoryName を取得
	public String getCategoryNameById(int categoryId) {
		Category category = categoryService.findCategoryById(categoryId);
		return category != null ? category.getName() : null;
	}

	@Override
	public int calculateQuantityDifference(ProductForm productForm, Product preProduct) {
		int quantityDifference = 0;

		// もし保存前のProductが存在すれば、在庫差分を計算
		if (preProduct != null) {
			quantityDifference = productForm.getStock() - preProduct.getStock();
		} else {
			// 新規の場合はproductFormの在庫が差分
			quantityDifference = productForm.getStock();
		}

		return quantityDifference;
	}

	@Override
	public String getRedirectPath(ProductForm productForm) {
		String path = "";

		if (productForm.getId() != null) {
			path = "redirect:/product/edit";
		} else {
			path = "redirect:/product/register";
		}
		return path;
	}

	@Override
	public boolean isProductExist(Integer id) {
		Product existing = findById(id);

		return existing != null;
	}
	
	public List<ProductStockStatus> getProductsByFileType(String fileType, String sortBy, String direction) {
	    if ("sold_out_items".equals(fileType)) {
	        return findSoldOutItems(sortBy, direction);
	    } else if ("low_in_stock_items".equals(fileType)) {
	        return findLowInStockItems(sortBy, direction);
	    } else {
	        throw new IllegalArgumentException("Invalid fileType: " + fileType);
	    }
	}
}
