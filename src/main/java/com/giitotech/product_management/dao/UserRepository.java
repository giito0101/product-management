package com.giitotech.product_management.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.giitotech.product_management.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUserName(String userName);

    Optional<User> findByEmail(String email);
}
