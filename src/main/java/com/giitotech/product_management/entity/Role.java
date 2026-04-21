package com.giitotech.product_management.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.awt.*;

@Entity
@Table(name = "role")
public class Role {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", columnDefinition = "int")
	private Long id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "display_name")
	private String displayName;

	public Role() {
	}

	public Role(Long id, String name, String displayName) {
		this.id = id;
		this.name = name;
		this.displayName = displayName;

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public String toString() {
		return "Role [id=" + id + ", name=" + name + ", displayName=" + displayName + "]";
	}
	
}
