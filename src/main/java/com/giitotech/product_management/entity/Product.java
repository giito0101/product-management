package com.giitotech.product_management.entity;

import java.sql.Timestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="product")
public class Product {

	// columnの生成
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
	private int id;

    @Column(name="name")
	private String name;
    
    @Column(name="price")
	private int price;
    
    @Column(name="stock")
	private int stock;

    @Column(name="last_updated")
    private Timestamp lastUpdated;
    
	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, 
            CascadeType.DETACH, CascadeType.REFRESH},
			fetch = FetchType.LAZY)
	@JoinColumn(name="category_id")
    private Category category;

	// コンストラクターの生成
	public Product() {
	}
	
	public Product(String name, int price, int stock, Category category) {
		this.name = name;
		this.price = price;
		this.stock = stock;
		this.category = category;
	}
	
    // コピー用のコンストラクタ
    public Product(Product other) {
        this.id = other.id;
        this.name = other.name;
        this.stock = other.stock;
        this.price = other.price;
		this.category = other.category;
    }

	// setter/getterの生成
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getPrice() {
		return price;
	}
	
	public void setPrice(int price) {
		this.price = price;
	}
	
	public int getStock() {
		return stock;
	}
	
	public void setStock(int stock) {
		this.stock = stock;
	}

	public Category getCategory() {
		return category;
	}
	
	public void setCategory(Category category) {
		this.category = category;
	}
	
	public Timestamp getLastUpdated() {
		return lastUpdated;
	}
	
	public void setLastUpdated(Timestamp lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	// toStringの生成
	@Override
	public String toString() {
		return "Product [id=" + id + ", name=" + name + ", price=" + price + ", stock=" + stock + ", category="
				+ category + "]";
	}
	
}
