package com.easypg.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.easypg.entities.LeaveNotice;
import com.easypg.entities.Tenant;

public interface LeaveNoticeDAO extends JpaRepository<LeaveNotice, Long>{
		Optional<LeaveNotice> findByTenantIdAndIsDeletedFalse(Long tenantId);
		List<LeaveNotice> findByIsDeletedFalse();
}
