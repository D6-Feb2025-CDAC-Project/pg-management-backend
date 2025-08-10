package com.easypg.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


import com.easypg.entities.Tenant;
import com.easypg.entities.BaseUser;

public interface TenantDao extends JpaRepository<Tenant, Long>{


	Optional<Tenant> findByIdAndIsDeletedFalse(Long tenantId);

	boolean existsByIdAndIsDeletedFalse(Long tenantId);
	Optional<Tenant> findByUserEmailAndIsDeletedFalse(String email);
}
