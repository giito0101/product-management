package com.giitotech.product_management.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.giitotech.product_management.dao.CategoryRepository;
import com.giitotech.product_management.entity.Category;

@Service
public class CategoryServiceImpl implements CategoryService{
	private CategoryRepository categoryRepository;
	
    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

	@Override
	public void save(Category cagegory) {
		categoryRepository.save(cagegory);
	}

	@Override
	public Category findById(int id) {
		Category category = categoryRepository.findById(id).orElse(null);
		return category;
	}

	@Override
	public void deleteById(int id) {
		categoryRepository.deleteById(id);
	}

	@Override
	public List<Category> findAll() {
		List<Category> categorys = categoryRepository.findAll(Sort.by("id"));
		return categorys;
	}
	
    // categoryId に一致するカテゴリを取得
    public Category findCategoryById(Integer categoryId) {
        return categoryRepository.findById(categoryId).orElse(null); // カテゴリが見つからない場合は null を返す
    }

}
