package com.giitotech.product_management.controller;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Logger;

import com.giitotech.product_management.dto.ProductSearchForm;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.giitotech.product_management.dto.ProductForm;
import com.giitotech.product_management.dto.ProductStockStatus;
import com.giitotech.product_management.dto.SearchForm;
import com.giitotech.product_management.entity.Action;
import com.giitotech.product_management.entity.Product;
import com.giitotech.product_management.entity.User;
import com.giitotech.product_management.exception.ProductNotFoundException;
import com.giitotech.product_management.service.CategoryService;
import com.giitotech.product_management.service.KeywordService;
import com.giitotech.product_management.service.LogService;
import com.giitotech.product_management.service.PagingService;
import com.giitotech.product_management.service.ProductService;
import com.giitotech.product_management.service.ProductSessionService;
import com.giitotech.product_management.service.StockHistoryService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/product")
public class ProductController {

	private ProductService productService;
	private CategoryService categoryService;
	private StockHistoryService stockHistoryService;
	private KeywordService keywordService;
	private PagingService pagingService;
	private ProductSessionService productSessionService;
	private LogService logService;
	private Logger logger = Logger.getLogger(getClass().getName());

	@Autowired
	public ProductController(ProductService theProductService,
			CategoryService theCategoryService,
			StockHistoryService theStockHistoryService,
			PagingService thePagingService,
			KeywordService theKeywordService,
			ProductSessionService theProductSessionService,
			LogService theLogService) {
		productService = theProductService;
		categoryService = theCategoryService;
		stockHistoryService = theStockHistoryService;
		pagingService = thePagingService;
		keywordService = theKeywordService;
		productSessionService = theProductSessionService;
		logService = theLogService;
	}

	@GetMapping("/showList")
	public String list(
			@RequestParam(defaultValue = "name") String sortBy,
			@RequestParam(defaultValue = "asc") String direction,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "") String keyword,
			Model theModel,
			@ModelAttribute("productSearchForm") ProductSearchForm productSearchForm) {

		productSearchForm.setSearch(keyword);
		String keywords[] = keywordService.processKeywords(keyword);

		// ソートとページングの設定
		Pageable pageable = pagingService.createPageable(sortBy, direction, page, size);

		Page<ProductStockStatus> theProductsStockStatuses = productService.findPaginatedByKeyword(pageable, keywords);

		// ステータスとプロダクトをリクエストに渡す
		theModel.addAttribute("productsWithStatus", theProductsStockStatuses.getContent());
		theModel.addAttribute("currentPage", page);
		theModel.addAttribute("totalPages", theProductsStockStatuses.getTotalPages());
		theModel.addAttribute("pageSize", size);
		theModel.addAttribute("sortBy", sortBy);
		theModel.addAttribute("direction", direction);
		theModel.addAttribute("productSearchForm", productSearchForm);

		return "product-management/product_list";
	}

	@GetMapping("/register")
	public String register(
			Model theModel,
			HttpServletRequest request) {

		String referer = request.getHeader("Referer"); // リファラを取得
		HttpSession session = request.getSession(false); // セッションが存在する場合のみ取得

		// セッションをサービス層で管理し、productFormを取得
		ProductForm productForm = productSessionService.getProductForm(session, referer);

		// 必要な属性をモデルにセット
		theModel.addAttribute("productForm", productForm);
		theModel.addAttribute("categories", categoryService.findAll());

		return "product-management/product_register";
	}

	@PostMapping("/registerConfirmProcessing")
	public String registerConfirmProcessing(
			@Valid @ModelAttribute("productForm") ProductForm productForm,
			BindingResult theBindingResult,
			Model theModel,
			HttpSession session) {

		// フォームを検証する
		if (theBindingResult.hasErrors()) {
			theModel.addAttribute("categories", categoryService.findAll());
			return "product-management/product_register";
		}

		int categoryId = productForm.getCategoryId().intValue();
		// 商品とカテゴリのペアが既に存在しているかチェック
		boolean isProductExist = productService.isProductExist(productForm.getName(), categoryId);
		if (isProductExist) {
			theModel.addAttribute("categories", categoryService.findAll());
			theModel.addAttribute("registrationError", "商品とカテゴリのペアが既に存在しています");
			logger.warning("Product name already exists.");
			return "product-management/product_register";
		}

		// categoryId に一致する categoryName を取得
		String categoryName = productService.getCategoryNameById(categoryId);
		productForm.setCategoryName(categoryName);

		// キャンセルした時の処理も兼ねて、セッションに格納
		session.setAttribute("productForm", productForm);
		theModel.addAttribute("title", "商品登録");

		return "product-management/product_confirm";
	}

	@PostMapping("/save")
	public String save(
			HttpSession session,
			Model theModel,
			RedirectAttributes redirectAttributes) {
		ProductForm productForm = (ProductForm) session.getAttribute("productForm");

		// ProductFormからProductを保存する前の状態を取得
		Product preProduct = null;

		if (productForm.getId() != null) {
			preProduct = productService.findById(productForm.getId());
			if (preProduct == null) {
				throw new ProductNotFoundException("Product not found with ID: " + productForm.getId());
			}
			// Productのコピーを作成
			preProduct = new Product(preProduct); // Productクラスにコピー用のコンストラクタを用意する必要があります
		}

		// ProductFormを基にProductを保存
		Product savedProduct = productService.save(productForm);

		// 在庫の変更数を計算
		int quantityDifference = productService.calculateQuantityDifference(productForm, preProduct);

		// リダイレクトパスを決定
		String path = productService.getRedirectPath(productForm);

		// 新規登録または変更があれば在庫履歴を保存
		if (productForm.getId() == null || quantityDifference != 0) {
			stockHistoryService.save(quantityDifference, savedProduct);
		}

		// 編集画面にリダイレクトされるときのパラメータ
		if (productForm.getId() != null) {
			redirectAttributes.addAttribute("productId", productForm.getId());
		}
		
		User theUser = (User)session.getAttribute("user");
		
		// ログ取得
		if(productForm.getId() == null) {
			logService.save(Action.ADD_PRODUCT, theUser);
		} else {
			logService.save(Action.UPDATE_PRODUCT, theUser);
		}

		redirectAttributes.addFlashAttribute("isNewUser", true);

		return path;
	}

	@GetMapping("/edit")
	public String edit(
			@RequestParam("productId") int productId,
			Model model,
			HttpServletRequest request) {
		String referer = request.getHeader("Referer"); // リファラを取得
		System.out.println("Referer: " + referer); // デバッグ用に出力

		HttpSession session = request.getSession(false); // セッションが存在する場合のみ取得

		ProductForm productForm = productSessionService.getProductForm(session, referer, productId);

		model.addAttribute("productForm", productForm);
		// 全てのカテゴリを取得
		model.addAttribute("categories", categoryService.findAll());

		return "product-management/product_edit";
	}

	@PostMapping("/editConfirmProcessing")
	public String editConfirmProcessing(
			@Valid @ModelAttribute("productForm") ProductForm productForm,
			BindingResult theBindingResult,
			Model theModel,
			HttpSession session) {

		// フォームを検証する
		if (theBindingResult.hasErrors()) {
			theModel.addAttribute("categories", categoryService.findAll());
			return "product-management/product_edit";
		}

		int categoryId = productForm.getCategoryId().intValue();

		// 商品IDが存在する場合、データベースで確認
		boolean isProductExist = productService.isProductExist(productForm.getId());
		if (!isProductExist) {
			// 商品が見つからない場合の処理
			theModel.addAttribute("registrationError", "指定された商品は存在しません。");
			theModel.addAttribute("categories", categoryService.findAll());
			return "product-management/product_edit";
		}

		// categoryId に一致する categoryName を取得
		String categoryName = productService.getCategoryNameById(categoryId);
		productForm.setCategoryName(categoryName);

		// 次の処理を一覧画面でも保持したいので、セッションに格納
		session.setAttribute("productForm", productForm);
		theModel.addAttribute("title", "商品編集");

		return "product-management/product_confirm";
	}

	@GetMapping("/delete")
	public String delete(
			@RequestParam("productId") int productId,
			Model model,
			@RequestParam("path") String path,
			RedirectAttributes redirectAttributes,
			HttpSession session) {
		// 商品を削除する
		productService.deleteById(productId);
		String redirectPath;

		if ("low_in_stock".equals(path)) {
			redirectPath = "redirect:/product/lowInStockItems";
		} else if ("sold_out".equals(path)) {
			redirectPath = "redirect:/product/soldOutItems";
		} else if ("list".equals(path)) {
			redirectPath = "redirect:/product/showList";
		} else {
			throw new IllegalArgumentException("削除する画面が分かりません: " + path);
		}
		
		redirectAttributes.addFlashAttribute("isUserDelete", true);
		
		User theUser = (User)session.getAttribute("user");
		logService.save(Action.DELETE_PRODUCT, theUser);

		// 指定されたパスへリダイレクトする
		return redirectPath;
	}

	@GetMapping("/search")
	public String search(
			@RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
			@RequestParam(defaultValue = "asc") String direction,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			Model theModel,
			@Valid @ModelAttribute("productSearchForm") ProductSearchForm productSearchForm,
			BindingResult theBindingResult) {
		String keyword = productSearchForm.getSearch().trim();
		String[] keywords = null;

		keywords = keywordService.processKeywords(keyword);

		// ソートとページングの設定
		Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

		Pageable pageable = PageRequest.of(page, size, sort);

		Page<ProductStockStatus> theProductsStockStatuses = null;

		if (theBindingResult.hasErrors()) {
			keywords = null;
			theProductsStockStatuses = productService.findPaginatedByKeyword(pageable, keywords);

			theModel.addAttribute("productSearchForm", productSearchForm);
			theModel.addAttribute("productsWithStatus", theProductsStockStatuses);
			theModel.addAttribute("currentPage", page);
			theModel.addAttribute("totalPages", theProductsStockStatuses.getTotalPages());
			theModel.addAttribute("pageSize", size);
			theModel.addAttribute("sortBy", sortBy);
			theModel.addAttribute("direction", direction);
			return "product-management/product_list";
		}

		theProductsStockStatuses = productService.findPaginatedByKeyword(pageable, keywords);

		theModel.addAttribute("productsWithStatus", theProductsStockStatuses);
		theModel.addAttribute("currentPage", page);
		theModel.addAttribute("totalPages", theProductsStockStatuses.getTotalPages());
		theModel.addAttribute("pageSize", size);
		theModel.addAttribute("sortBy", sortBy);
		theModel.addAttribute("direction", direction);
		theModel.addAttribute("productSearchForm", productSearchForm);

		// /商品一覧画面を表示
		return "product-management/product_list";
	}

	@GetMapping("/report")
	public String report() {
		return "product-management/report";
	}

	@GetMapping("/soldOutItems")
	public String soldOutItems(
			Model theModel,
			@RequestParam(defaultValue = "name") String sortBy,
			@RequestParam(defaultValue = "asc") String direction) {
		List<ProductStockStatus> theProductsStockStatuses = productService.findSoldOutItems(sortBy, direction);

		theModel.addAttribute("productSearchForm", new ProductSearchForm());
		theModel.addAttribute("productsWithStatus", theProductsStockStatuses);
		theModel.addAttribute("sortBy", sortBy);
		theModel.addAttribute("direction", direction);

		return "product-management/sold_out_items";
	}

	@GetMapping("/lowInStockItems")
	public String lowInStock(
			Model theModel,
			@RequestParam(defaultValue = "name") String sortBy,
			@RequestParam(defaultValue = "asc") String direction) {
		List<ProductStockStatus> theProductsStockStatuses = productService.findLowInStockItems(sortBy, direction);

		theModel.addAttribute("productSearchForm", new ProductSearchForm());
		theModel.addAttribute("productsWithStatus", theProductsStockStatuses);
		theModel.addAttribute("sortBy", sortBy);
		theModel.addAttribute("direction", direction);

		return "product-management/low_in_stock_items";
	}

	@PostMapping("/searchItems")
	public String search(
			@Valid @ModelAttribute("productSearchForm") ProductSearchForm productSearchForm,
			BindingResult theBindingResult,
			Model theModel,
			@RequestParam(defaultValue = "name") String sortBy,
			@RequestParam(defaultValue = "asc") String direction,
			@RequestParam(defaultValue = "sold_out_items") String stockType) {
		String path;
		if (stockType.equals("sold_out_items")) {
			path = "product-management/sold_out_items";

			if (theBindingResult.hasErrors()) {
				List<ProductStockStatus> theProductsStockStatuses = productService.findSoldOutItems(sortBy, direction);
				theModel.addAttribute("productSearchForm", productSearchForm);
				theModel.addAttribute("productsWithStatus", theProductsStockStatuses);
				theModel.addAttribute("sortBy", sortBy);
				theModel.addAttribute("direction", direction);
				return path;
			}
		} else {
			path = "product-management/low_in_stock_items";

			if (theBindingResult.hasErrors()) {
				List<ProductStockStatus> theProductsStockStatuses = productService.findLowInStockItems(sortBy,
						direction);
				theModel.addAttribute("productSearchForm", productSearchForm);
				theModel.addAttribute("productsWithStatus", theProductsStockStatuses);
				theModel.addAttribute("sortBy", sortBy);
				theModel.addAttribute("direction", direction);
				return path;
			}
		}

		// 入力された検索ワードを配列で取得
		String[] keywords;
		keywords = keywordService.processKeywords(productSearchForm.getSearch());

		List<ProductStockStatus> theProductsStockStatuses = productService.findListProductStockStatusByKeywordAndDate(
				keywords,
				productSearchForm.getStartDate(),
				productSearchForm.getEndDate(),
				stockType, 
				sortBy, 
				direction);

		theModel.addAttribute("productsWithStatus", theProductsStockStatuses);
		theModel.addAttribute("productSearchForm", productSearchForm);
		theModel.addAttribute("sortBy", sortBy);
		theModel.addAttribute("direction", direction);

		// 指定パスの画面を表示
		return path;
	}

	@GetMapping("/csv")
	public void exportCSV(
			HttpServletResponse response,
			@RequestParam(defaultValue = "name") String sortBy,
			@RequestParam(defaultValue = "asc") String direction,
			@RequestParam(defaultValue = "sold_out_items") String fileType) throws IOException {

		// ファイル名の設定
		String fileName = fileType + ".csv";

		// ファイル名とコンテンツタイプの設定
		response.setContentType("text/csv; charset=Windows-31J");
		response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

		// データの取得
	    List<ProductStockStatus> theProductsStockStatuses = productService.getProductsByFileType(fileType, sortBy, direction);

		// CSV書き込み
		try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(response.getOutputStream(), "Windows-31J"))) {
			writer.println("商品名,カテゴリ,在庫数,価格"); // ヘッダー行
			for (ProductStockStatus productStockStatus : theProductsStockStatuses) {
				writer.println(String.format("%s,%s,%d,%s",
						productStockStatus.getName(),
						productStockStatus.getCategoryName(),
						productStockStatus.getStock(),
						productStockStatus.getPrice()));
			}
		}
	}

	@GetMapping("/excel")
	public void exportExcel(
			HttpServletResponse response,
			@RequestParam(defaultValue = "name") String sortBy,
			@RequestParam(defaultValue = "asc") String direction,
			@RequestParam(defaultValue = "sold_out_items") String fileType) throws IOException {

		// ファイル名の設定
		String fileName = fileType + ".xlsx";

		// ファイル名とコンテンツタイプの設定
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

		// データの取得
	    List<ProductStockStatus> theProductsStockStatuses = productService.getProductsByFileType(fileType, sortBy, direction);

		// ワークブックとシートを作成
		try (Workbook workbook = new XSSFWorkbook()) {
			Sheet sheet = workbook.createSheet(fileType.replace("_", " ").toUpperCase());

			// ヘッダー行を作成
			Row headerRow = sheet.createRow(0);
			headerRow.createCell(0).setCellValue("商品名");
			headerRow.createCell(1).setCellValue("カテゴリ");
			headerRow.createCell(2).setCellValue("在庫数");
			headerRow.createCell(3).setCellValue("価格");

			// データ行を作成
			int rowNum = 1;
			for (ProductStockStatus productStockStatus : theProductsStockStatuses) {
				Row row = sheet.createRow(rowNum++);
				row.createCell(0).setCellValue(productStockStatus.getName());
				row.createCell(1).setCellValue(productStockStatus.getCategoryName());
				row.createCell(2).setCellValue(productStockStatus.getStock());
				row.createCell(3).setCellValue(productStockStatus.getPrice());
			}

			// Excelデータを書き込み
			workbook.write(response.getOutputStream());
		}
	}
}
