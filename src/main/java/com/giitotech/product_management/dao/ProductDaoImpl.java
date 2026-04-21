package com.giitotech.product_management.dao;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import com.giitotech.product_management.entity.Product;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

@Repository
public class ProductDaoImpl implements ProductDao {
	// entity managerのためのフィールドを定義する
	private EntityManager entityManager;

	// constructor injectionを使ってentity managerを注入する
	@Autowired
	public ProductDaoImpl(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public Page<Product> findProductByKeyword(Pageable pageable, String[] keywords) {
		int maxKeywords = 10;

		// 検索キーワードのリスト作成（空のkeywordsも許容）
		List<String> keywordList = keywords != null ? Arrays.stream(keywords)
				.filter(k -> k != null && !k.trim().isEmpty())
				.limit(maxKeywords)
				.map(k -> "%" + k.trim() + "%") // LIKE句用に加工
				.collect(Collectors.toList()) : new ArrayList<>();

		// ベースクエリ
		StringBuilder jpql = new StringBuilder("SELECT p FROM Product p LEFT JOIN p.category c");
		StringBuilder countJpql = new StringBuilder("SELECT COUNT(p) FROM Product p LEFT JOIN p.category c");

		// WHERE 句の追加
		if (!keywordList.isEmpty()) {
			jpql.append(" WHERE ");
			countJpql.append(" WHERE ");
			for (int i = 0; i < keywordList.size(); i++) {
				if (i > 0) {
					jpql.append(" OR ");
					countJpql.append(" OR ");
				}
				jpql.append("(p.name LIKE :keyword").append(i).append(" OR c.name LIKE :keyword").append(i).append(")");
				countJpql.append("(p.name LIKE :keyword").append(i).append(" OR c.name LIKE :keyword").append(i)
						.append(")");
			}
		}

		// ORDER BY 句の追加（動的）
		Sort sort = pageable.getSort();
		if (sort.isSorted()) {
			jpql.append(" ORDER BY ");
			countJpql.append(" ORDER BY ");

			List<String> orderConditions = new ArrayList<>();
			for (Sort.Order order : sort) {
				String property = order.getProperty();
				String direction = order.getDirection().isAscending() ? "ASC" : "DESC";

				// テーブル名を適切に指定
				if ("category.name".equals(property)) {
					orderConditions.add("c.name " + direction);
				} else {
					orderConditions.add("p." + property + " " + direction);
				}
			}

			jpql.append(String.join(", ", orderConditions));
			countJpql.append(String.join(", ", orderConditions));
		}

		// クエリ作成
		TypedQuery<Product> query = entityManager.createQuery(jpql.toString(), Product.class);
		TypedQuery<Long> countQuery = entityManager.createQuery(countJpql.toString(), Long.class);

		// パラメータ設定
		for (int i = 0; i < keywordList.size(); i++) {
			query.setParameter("keyword" + i, keywordList.get(i));
			countQuery.setParameter("keyword" + i, keywordList.get(i));
		}

		long totalElements = 0;
		List<Product> products = new ArrayList<>();

		// 件数を取得
		totalElements = countQuery.getSingleResult();

		// ページング設定
		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		products = query.getResultList();

		return new PageImpl<>(products, pageable, totalElements);
	}

	@Override
	public Product findProductByNameAndCategoryName(String name, int categoryId) {
		String jpql = "SELECT p FROM Product p " +
				"WHERE p.name = :name AND p.category.id = :categoryId";

		TypedQuery<Product> query = entityManager.createQuery(jpql, Product.class);
		query.setParameter("name", name);
		query.setParameter("categoryId", categoryId);
		Product product = null;

		try {
			product = query.getSingleResult();
		} catch (NoResultException e) {
			product = null;
		}

		return product;
	}

	@Override
	public long countProducts() {
		String jpql = "SELECT COUNT(p) FROM Product p";
		return (long) entityManager.createQuery(jpql).getSingleResult();
	}

	public long countOutofStockProducts() {
		String jpql = "SELECT COUNT(p) FROM Product p WHERE p.stock = 0";
		return (long) entityManager.createQuery(jpql).getSingleResult();
	}

	@Override
	public List<Product> findSoldOutItems(String sortBy, String sortOrder) {
		// 条件を指定して汎用メソッドを呼び出す
		String condition = "p.stock = 0";
		return getSortedProducts(condition, sortBy, sortOrder);
	}

	@Override
	public List<Product> findLowInStockItems(String sortBy, String sortOrder) {
		// 条件を指定して汎用メソッドを呼び出す
		String condition = "p.stock > 0 AND p.stock <= 5";
		return getSortedProducts(condition, sortBy, sortOrder);
	}

	public List<Product> getSortedProducts(String condition, String sortBy, String sortOrder) {
		// サポートされているソート項目とデフォルト値
		Map<String, String> sortFields = Map.of(
				"name", "p.name",
				"stock", "p.stock",
				"category", "c.name",
				"price", "p.price");

		// ソートキーのバリデーション
		String sortField = sortFields.getOrDefault(sortBy, "p.name"); // デフォルトは商品名
		String order = "ASC".equalsIgnoreCase(sortOrder) ? "ASC" : "DESC"; // デフォルトは昇順

		// ベースのJPQL
		String jpql = "SELECT p FROM Product p LEFT JOIN p.category c WHERE " + condition + " ORDER BY " + sortField
				+ " " + order;

		// クエリを作成
		TypedQuery<Product> query = entityManager.createQuery(jpql, Product.class);

		// 結果を取得
		return query.getResultList();
	}

	public List<Product> findListProductByKeywordAndDate(
			String[] keywords, LocalDate startDate, LocalDate endDate, String stockType, String sortBy,
			String direction) {

		String stockCondition;
		// 在庫条件を動的に設定
		switch (stockType) {
		case "low_in_stock_items":
			stockCondition = "p.stock > 0 AND p.stock <= 5";
			break;
		case "sold_out_items":
		default:
			stockCondition = "p.stock = 0";
			break;
		}

		// キーワードを分割し、最大10個に制限
		int maxKeywords = 10;
		List<String> keywordList = Arrays.stream(keywords != null ? keywords : new String[0])
				.limit(maxKeywords)
				.filter(k -> k != null && !k.isEmpty())
				.map(k -> "%" + k + "%")
				.collect(Collectors.toList());

		// サポートされているソート項目を定義
		Map<String, String> sortFields = Map.of(
				"name", "p.name",
				"stock", "p.stock",
				"category", "c.name",
				"price", "p.price");

		// ソートフィールドのバリデーションとデフォルト設定
		String sortField = sortFields.getOrDefault(sortBy, "p.name"); // デフォルトは商品名
		String sortDirection = "ASC".equalsIgnoreCase(direction) ? "ASC" : "DESC"; // デフォルトは昇順

		// JPQLの動的生成
		StringBuilder jpql = new StringBuilder("SELECT p FROM Product p LEFT JOIN p.category c WHERE ");
		jpql.append(stockCondition); // 在庫条件を挿入

		boolean hasKeywordCondition = !keywordList.isEmpty();
		boolean hasStartDate = startDate != null;
		boolean hasEndDate = endDate != null;

		if (hasKeywordCondition) {
			jpql.append(" AND (");
			for (int i = 0; i < keywordList.size(); i++) {
				if (i > 0) {
					jpql.append(" OR ");
				}
				jpql.append("(p.name LIKE :keyword").append(i).append(" OR c.name LIKE :keyword").append(i).append(")");
			}
			jpql.append(")");
		}

		if (hasStartDate) {
			jpql.append(" AND p.lastUpdated >= :startDate");
		}

		if (hasEndDate) {
			jpql.append(" AND p.lastUpdated <= :endDate");
		}

		// ソート句を追加
		jpql.append(" ORDER BY ").append(sortField).append(" ").append(sortDirection);

		// クエリを作成
		TypedQuery<Product> query = entityManager.createQuery(jpql.toString(), Product.class);

		// パラメータを設定
		if (hasKeywordCondition) {
			for (int i = 0; i < keywordList.size(); i++) {
				query.setParameter("keyword" + i, keywordList.get(i));
			}
		}

		if (hasStartDate) {
			query.setParameter("startDate", Timestamp.valueOf(startDate.atStartOfDay()));
		}

		if (hasEndDate) {
			query.setParameter("endDate", Timestamp.valueOf(endDate.atTime(23, 59, 59, 999_000_000)));
		}

		// 結果を取得
		return query.getResultList();
	}

}
