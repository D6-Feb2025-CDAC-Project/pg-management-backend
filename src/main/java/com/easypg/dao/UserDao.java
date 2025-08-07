package com.easypg.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.easypg.entities.User;

public interface UserDao extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
  
}