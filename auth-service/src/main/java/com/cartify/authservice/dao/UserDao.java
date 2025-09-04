package com.cartify.authservice.dao;

import com.cartify.authservice.domain.User;

import java.util.Optional;

public interface UserDao {
    Optional<User> findById(long id);
    Optional<User> findByUsername(String username);
    long insert(User u);
}
