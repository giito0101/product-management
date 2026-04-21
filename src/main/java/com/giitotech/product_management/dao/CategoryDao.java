package com.giitotech.product_management.dao;

import com.giitotech.product_management.entity.Category;

public interface CategoryDao {
	Category findByName(String name);
}
