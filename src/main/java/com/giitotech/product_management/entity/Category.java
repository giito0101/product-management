package com.giitotech.product_management.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
@Entity
@Table(name="category")
public class Category {
	
	// columnの設定
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

    @Column(name = "name")
	private String name;
    
    @OneToMany(mappedBy = "category",
			fetch = FetchType.LAZY,
			cascade = {CascadeType.PERSIST, CascadeType.MERGE,
					   CascadeType.DETACH, CascadeType.REFRESH})
    private List<Product> products;

	// コンストラクターの生成
	public Category() {
	}
	public Category(String name) {
		this.name = name;
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

	// toStringの生成
	@Override
	public String toString() {
		return "Category [id=" + id + ", name=" + name + "]";
	}
	
	public List<Product> getProducts() {
		return products;
	}
	
	public void setProducts(List<Product> products) {
		this.products = products;
	}
	
	public void add (Product product) {
		if(product == null) {
			products = new ArrayList<>();
		}
		
		products.add(product);

		product.setCategory(this);
	}
}
