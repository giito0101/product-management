package com.giitotech.product_management.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.giitotech.product_management.dto.LatestStockHistory;

import jakarta.persistence.EntityManager;

@Repository
public class StockHistoryDaoImpl implements StockHistoryDao{
	private EntityManager entityManager;
	
	@Autowired
	public StockHistoryDaoImpl (EntityManager theEntityManager) {
		entityManager = theEntityManager;
	}
	
	public List<LatestStockHistory> findLatestStockHistories(int maxResults) {
	    String jpql = "select new com.giitotech.product_management.dto.LatestStockHistory(" +
	                  "p.name, " +
	                  "c.name, " +
	                  "p.stock, " +
	                  "s.changeQuantity, " +
	                  "s.timestamp, " +
	                  "null, " +
	                  "(select sum(s2.changeQuantity) from StockHistory s2 " +
	                  " where s2.product = s.product and  s2.timestamp <= s.timestamp)" +
	                  ") " +
	                  "from StockHistory s " +
	                  "join s.product p " +
	                  "join p.category c " +
	                  "order by s.timestamp desc";
	    
	    return entityManager.createQuery(jpql, LatestStockHistory.class)
	                        .setMaxResults(maxResults)
	                        .getResultList();
	}
}
