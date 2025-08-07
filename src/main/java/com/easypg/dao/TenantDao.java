package com.easypg.dao;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.easypg.entities.Tenant;
import com.easypg.entities.User;

public interface TenantDao extends JpaRepository<Tenant, Long>{

	
	
}
