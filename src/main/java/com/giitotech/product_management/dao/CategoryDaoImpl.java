package com.giitotech.product_management.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.giitotech.product_management.entity.Category;

import jakarta.persistence.EntityManager;

@Repository
public class CategoryDaoImpl implements CategoryDao {
	// entity managerのためのフィールドを定義する
	private EntityManager entityManager;

	// constructor injectionを使ってentity managerを注入する
	@Autowired
	public CategoryDaoImpl(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
    // name フィールドを検索するメソッド
	public Category findByName(String name) {
		String jpql = "SELECT c FROM Category c WHERE c.name = :name";
		return entityManager.createQuery(jpql, Category.class)
				.setParameter("name", name)
				.getSingleResult();
	}
}
