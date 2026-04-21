package com.giitotech.product_management.service;

import java.util.List;

import com.giitotech.product_management.entity.Category;

public interface CategoryService {
    public void save(Category cagegory);

    public Category findById(int id);
    
    public void deleteById(int id);
    
    public List<Category> findAll();
    
    public Category findCategoryById(Integer categoryId);
}
