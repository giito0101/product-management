package com.giitotech.product_management.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.giitotech.product_management.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {

}
