package com.easypg.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.easypg.entities.LeaveNotice;
import com.easypg.entities.Tenant;
import com.easypg.enums.NoticeResponseStatus;

public interface LeaveNoticeDao extends JpaRepository<LeaveNotice, Long>{
		Optional<LeaveNotice> findByTenantIdAndIsDeletedFalse(Long tenantId);
		List<LeaveNotice> findByIsDeletedFalse();
		List<LeaveNotice> findByIsDeletedFalseOrderByCreatedAtDesc();
		Optional<LeaveNotice> findByIdAndIsDeletedFalse(Long id);
		
		List<LeaveNotice> findByNoticeResponseStatusAndIsDeletedFalse(NoticeResponseStatus status);
	    List<LeaveNotice> findByIsDeletedFalseAndCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}
