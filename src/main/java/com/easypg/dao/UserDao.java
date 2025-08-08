package com.easypg.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.easypg.entities.User;

public interface UserDao extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

	Optional<User> findByEmail(String identifier);
	
	
  
}