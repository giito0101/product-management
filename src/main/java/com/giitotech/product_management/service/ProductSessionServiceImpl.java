package com.giitotech.product_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.giitotech.product_management.dto.ProductForm;
import com.giitotech.product_management.entity.Product;
import com.giitotech.product_management.exception.ProductNotFoundException;

import jakarta.servlet.http.HttpSession;

@Service
public class ProductSessionServiceImpl implements ProductSessionService{
	private ProductService productService;

	@Autowired
	public ProductSessionServiceImpl(ProductService theProductService) {
		this.productService = theProductService;
	}

	// セッションが有効かどうかを判定し、必要ならば productForm を取得
	public ProductForm getProductForm(HttpSession session, String referer) {
		// リファラが指定のパスと一致しない場合、セッションから productForm を削除
		if (referer == null || !referer.contains("/product/registerConfirmProcessing")) {
			session.removeAttribute("productForm");
			return new ProductForm(); // 新しいインスタンスを返す
		}

		// セッションから productForm を取得
		ProductForm productForm = (ProductForm) session.getAttribute("productForm");
		if (productForm == null) {
			productForm = new ProductForm(); // セッションにproductFormがなければ新しく作成
		}
		return productForm;
	}

	public ProductForm getProductForm(HttpSession session, String referer, int productId) {
		ProductForm productForm = null;
		
		// リファラが指定のパスと一致しない場合、セッションから productForm を削除
		if (referer == null || !referer.contains("/product/editConfirmProcessing")) {
			session.removeAttribute("productForm");
			Product product = productService.findById(productId);

			productForm = new ProductForm(
					product.getId(),
					product.getName(),
					product.getCategory().getId(),
					product.getStock(),
					product.getPrice());

			// categoryId に一致する categoryName を取得
			String categoryName = productService.getCategoryNameById(product.getCategory().getId());
			productForm.setCategoryName(categoryName);
		} else {
			// セッションから productForm を取得
			productForm = (ProductForm) session.getAttribute("productForm");
		}

		if (productForm == null) {
            throw new ProductNotFoundException("Product not found with referer: " + referer);
		}
		return productForm;
	}
}
