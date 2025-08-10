package com.easypg.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.easypg.entities.BaseUser;
import com.easypg.enums.UserRole;

public interface UserDao extends JpaRepository<BaseUser, Long> {
    boolean existsByEmail(String email);

	Optional<BaseUser> findByEmail(String identifier);
	
	Optional<BaseUser> findByEmailAndUserRole(String email, UserRole userRole);
  
}