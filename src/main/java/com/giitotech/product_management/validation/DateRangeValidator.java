package com.giitotech.product_management.validation;

import com.giitotech.product_management.dto.ProductSearchForm;
import com.giitotech.product_management.dto.SearchForm;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateRangeValidator implements ConstraintValidator<ValidDateRange, ProductSearchForm> {

	@Override
	public boolean isValid(ProductSearchForm value, ConstraintValidatorContext context) {
		if (value == null || value.getStartDate() == null || value.getEndDate() == null) {
			return true; // null値のバリデーションは別のアノテーションに任せる
		}

		return !value.getStartDate().isAfter(value.getEndDate());
	}

}
