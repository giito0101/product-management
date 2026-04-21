package com.giitotech.product_management.dto;

public class UserSearchForm extends SearchForm{

    public UserSearchForm(String search, String sortBy,
                          String direction,
                          Integer page, Integer size) {
        super(search, sortBy, direction, page, size);
    }
}
