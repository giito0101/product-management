package com.giitotech.product_management.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.giitotech.product_management.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer>{
}
