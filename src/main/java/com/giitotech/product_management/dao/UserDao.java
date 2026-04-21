package com.giitotech.product_management.dao;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.giitotech.product_management.entity.User;

public interface UserDao {
	
	User findByUserName(String userName);

	User save(User theUser);
}
