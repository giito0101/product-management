package com.giitotech.product_management.dao;

import com.giitotech.product_management.entity.Role;

public interface RoleDao {
	public Role findRoleByName(String theRoleName);
}
