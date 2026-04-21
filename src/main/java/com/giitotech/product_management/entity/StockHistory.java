package com.giitotech.product_management.entity;

import java.sql.Timestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="stock_history")
public class StockHistory {

	// columnの生成
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private int id;

	@Column(name="change_quantity")
	private int changeQuantity;

	@Column(name="timestamp")
	private Timestamp timestamp;
	
	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, 
            CascadeType.DETACH, CascadeType.REFRESH})
	@JoinColumn(name="product_id")
	private Product product;

	// コンストラクターの生成
	public StockHistory() {
	}

	public StockHistory(int changeQuantity, Product product) {
		this.changeQuantity = changeQuantity;
		this.product = product;
	}

	// setter/getterの生成
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getChangeQuantity() {
		return changeQuantity;
	}
	public void setChangeQuantity(int changeQuantity) {
		this.changeQuantity = changeQuantity;
	}
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	public Product getProduct() {
		return product;
	}
	
	public void setProduct(Product product) {
		this.product = product;
	}

	// toStringの生成
	@Override
	public String toString() {
		return "StockHistory [id=" + id + ", change_quantity=" + changeQuantity
				+ ", timestamp=" + timestamp + "]";
	}
}
