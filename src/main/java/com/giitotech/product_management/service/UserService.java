package com.giitotech.product_management.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.giitotech.product_management.dto.UserForm;
import com.giitotech.product_management.dto.UserRolesDto;
import com.giitotech.product_management.entity.User;


public interface UserService extends UserDetailsService {
	
	public User findByUserName(String userName);
	
    public void deleteById(int id);

    public Page<UserRolesDto> searchUsers(Pageable pageable, String[] keywords, String sortBy, String direction);

    public void validateForInsert(UserForm userForm);

    public void validateForUpdate(UserForm userForm);

    User save(UserForm userForm);
}
