package com.easypg.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.easypg.entities.Complaint;
import com.easypg.entities.Tenant;
import com.easypg.enums.ComplaintStatus;
import com.easypg.enums.PriorityLevel;

@Repository
public interface ComplaintDao extends JpaRepository<Complaint, Long> {

    // Find all complaints for a tenant (excluding deleted ones) - ordered by creation date desc
    List<Complaint> findByTenantAndIsDeletedFalseOrderByCreatedAtDesc(Tenant tenant);
    
    // Find complaint by ID and tenant (for ownership validation)
    @Query("SELECT c FROM Complaint c WHERE c.id = :complaintId AND c.tenant = :tenant AND c.isDeleted = false")
    Optional<Complaint> findByIdAndTenantAndIsDeletedFalse(@Param("complaintId") Long complaintId, @Param("tenant") Tenant tenant);
    
    // Count complaints by status for a tenant (excluding deleted)
    long countByTenantAndComplaintStatusAndIsDeletedFalse(Tenant tenant, ComplaintStatus status);

	List<Complaint> findByTenantId(Long id);
	
// ========== NEW ADMIN METHODS ==========
    
    // Find all active complaints (for admin) - ordered by creation date desc
    @Query("SELECT c FROM Complaint c WHERE c.isDeleted = false ORDER BY c.createdAt DESC")
    List<Complaint> findAllActiveComplaintsOrderByCreatedAtDesc();
    
    // Find complaints by status (for admin) - ordered by creation date desc
    List<Complaint> findByComplaintStatusAndIsDeletedFalseOrderByCreatedAtDesc(ComplaintStatus status);
    
    // Find complaints by priority (for admin) - ordered by creation date desc
    List<Complaint> findByPriorityLevelAndIsDeletedFalseOrderByCreatedAtDesc(PriorityLevel priority);
    
    // Find complaints by both status and priority (for admin filtering)
    List<Complaint> findByComplaintStatusAndPriorityLevelAndIsDeletedFalseOrderByCreatedAtDesc(
            ComplaintStatus status, PriorityLevel priority);
    
    // Count total active complaints (for admin stats)
    long countByIsDeletedFalse();
    
    // Count complaints by status (for admin stats)
    long countByComplaintStatusAndIsDeletedFalse(ComplaintStatus status);
    
    // Count complaints by priority (for admin stats)
    long countByPriorityLevelAndIsDeletedFalse(PriorityLevel priority);
    
    // Get complaints by tenant ID (for admin to view specific tenant's complaints)
    @Query("SELECT c FROM Complaint c WHERE c.tenant.id = :tenantId AND c.isDeleted = false ORDER BY c.createdAt DESC")
    List<Complaint> findByTenantIdAndIsDeletedFalseOrderByCreatedAtDesc(@Param("tenantId") Long tenantId);
}