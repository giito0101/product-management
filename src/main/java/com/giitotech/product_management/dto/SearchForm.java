package com.giitotech.product_management.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.giitotech.product_management.validation.KeywordSplit;
import com.giitotech.product_management.validation.NotFutureDate;
import com.giitotech.product_management.validation.ValidDateRange;

import jakarta.validation.constraints.Size;

public abstract class SearchForm {
	//	検索ワード
	@Size(min=0, max=100, message="検索文字数が適切ではありません")
	@KeywordSplit
	private String search;
	
	private String sortBy;
	
	private String direction;

	private Integer page;

	private Integer size;

	public SearchForm (String search, String sortBy, String direction, Integer page, Integer size) {
		this.search = search;
		this.sortBy = sortBy;
		this.direction = direction;
		this.page = page;
		this.size = size;
	}

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	public String getSortBy() {
		return sortBy;
	}
	
	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
	}
	
	public String getDirection() {
		return direction;
	}
	
	public void setDirection(String direction) {
		this.direction = direction;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getSize() {
		return size;
	}
	public void setSize(Integer size) {
		this.size = size;
	}

	public SearchForm() {
	}

	@Override
	public String toString() {
		return "SearchForm{" +
				"search='" + search + '\'' +
				", sortBy='" + sortBy + '\'' +
				", direction='" + direction + '\'' +
				", page='" + page + '\'' +
				", size='" + size + '\'' +
				'}';
	}
}
