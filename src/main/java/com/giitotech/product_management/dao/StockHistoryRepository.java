package com.giitotech.product_management.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.giitotech.product_management.entity.StockHistory;

public interface StockHistoryRepository extends JpaRepository<StockHistory, Integer>{

}
