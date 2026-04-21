package com.giitotech.product_management.service;

import com.giitotech.product_management.dto.UserSearchForm;
import org.springframework.data.domain.Pageable;

public interface PagingService {
	public Pageable createPageable(String sortBy, String direction, int page, int size);

	public Pageable createPageable(UserSearchForm userSearchForm);
}
