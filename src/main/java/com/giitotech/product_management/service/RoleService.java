package com.giitotech.product_management.service;

import java.util.List;

import com.giitotech.product_management.dto.UserForm;
import com.giitotech.product_management.entity.Role;

public interface RoleService {
	public List<Role> findAll();
	
	public void setRoleToUserForm(UserForm userForm, List<Role> roles);
}
