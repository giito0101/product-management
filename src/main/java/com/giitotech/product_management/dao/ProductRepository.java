package com.giitotech.product_management.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.giitotech.product_management.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Integer>{
}
