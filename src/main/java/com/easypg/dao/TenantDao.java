package com.easypg.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.easypg.entities.Tenant;

public interface TenantDao extends JpaRepository<Tenant, Long>{
	
	Optional<Tenant> findByIdAndIsDeletedFalse(Long tenantId);
}
