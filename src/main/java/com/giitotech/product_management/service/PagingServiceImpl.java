package com.giitotech.product_management.service;

import com.giitotech.product_management.dto.SearchForm;
import com.giitotech.product_management.dto.UserSearchForm;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class PagingServiceImpl implements PagingService{
	@Override
	public Pageable createPageable(String sortBy, String direction, int page, int size) {
		// pageとsizeのバリデーション
		if (page < 0 || size <= 0) {
			throw new IllegalArgumentException("Page number and size must be non-negative and greater than zero.");
		}

		// directionが不正な場合のみチェック
		if (!"asc".equalsIgnoreCase(direction) && !"desc".equalsIgnoreCase(direction)) {
			return PageRequest.of(page, size); // デフォルトのソート方向を使用
		}

		// sortByが空でない場合だけ使用
		Sort sort;
		if(sortBy != null && !sortBy.trim().isEmpty()) {
			if(direction.equalsIgnoreCase("asc")) {
				sort = Sort.by(sortBy).ascending();
			} else {
				sort = Sort.by(sortBy).descending();
			}
		} else {
			// デフォルトはソートなし
			sort = Sort.unsorted();
		}

		return PageRequest.of(page, size, sort);
	}

	public Pageable createPageable(UserSearchForm userSearchForm) {
		String sortBy = userSearchForm.getSortBy();
		String direction = userSearchForm.getDirection();
		int page = userSearchForm.getPage();
		int size = userSearchForm.getSize();

	    // pageとsizeのバリデーション
	    if (page < 0 || size <= 0) {
	        throw new IllegalArgumentException("Page number and size must be non-negative and greater than zero.");
	    }
		
	    // directionが不正な場合のみチェック
	    if (!"asc".equalsIgnoreCase(direction) && !"desc".equalsIgnoreCase(direction)) {
	        return PageRequest.of(page, size); // デフォルトのソート方向を使用
	    }

	    // sortByが空でない場合だけ使用
	    Sort sort;
	    if(sortBy != null && !sortBy.trim().isEmpty()) {
	    	if(direction.equalsIgnoreCase("asc")) {
	    		sort = Sort.by(sortBy).ascending();
	    	} else {
	    		sort = Sort.by(sortBy).descending();
	    	}
	    } else {
	    	// デフォルトはソートなし
	    	sort = Sort.unsorted();
	    }

	    return PageRequest.of(page, size, sort);
	}
}
