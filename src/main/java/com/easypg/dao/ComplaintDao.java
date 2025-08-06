// ComplaintDao.java
package com.easypg.dao;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.easypg.entities.Complaint;
import com.easypg.enums.ComplaintStatus;
import com.easypg.enums.PriorityLevel;

public interface ComplaintDao extends JpaRepository<Complaint, Long> {
    
    // Get all non-deleted complaints ordered by creation date (newest first)
    List<Complaint> findByIsDeletedFalseOrderByCreatedAtDesc();
    
    // Get complaint by ID if not deleted
    Optional<Complaint> findByIdAndIsDeletedFalse(Long id);
    
    // Get complaints by tenant ID (non-deleted only)
    List<Complaint> findByTenantIdAndIsDeletedFalseOrderByCreatedAtDesc(Long tenantId);
    
    // Get complaints by status (non-deleted only)
    List<Complaint> findByComplaintStatusAndIsDeletedFalseOrderByCreatedAtDesc(ComplaintStatus status);
    
    // Get complaints by priority level (non-deleted only)
    List<Complaint> findByPriorityLevelAndIsDeletedFalseOrderByCreatedAtDesc(PriorityLevel priorityLevel);
    
    // Get complaints by tenant and status
    List<Complaint> findByTenantIdAndComplaintStatusAndIsDeletedFalseOrderByCreatedAtDesc(
            Long tenantId, ComplaintStatus status);
    
    // Get complaints by tenant and priority
    List<Complaint> findByTenantIdAndPriorityLevelAndIsDeletedFalseOrderByCreatedAtDesc(
            Long tenantId, PriorityLevel priorityLevel);
    
    // Count complaints by status (non-deleted only)
    long countByComplaintStatusAndIsDeletedFalse(ComplaintStatus status);
    
    // Count complaints by priority level (non-deleted only)
    long countByPriorityLevelAndIsDeletedFalse(PriorityLevel priorityLevel);
    
    // Count total non-deleted complaints
    long countByIsDeletedFalse();
    
    // Count complaints by tenant (non-deleted only)
    long countByTenantIdAndIsDeletedFalse(Long tenantId);
    
    // Count complaints by tenant and status
    long countByTenantIdAndComplaintStatusAndIsDeletedFalse(Long tenantId, ComplaintStatus status);
    
    // Custom query to get complaints with tenant details
    @Query("SELECT c FROM Complaint c JOIN FETCH c.tenant t WHERE c.isDeleted = false ORDER BY c.createdAt DESC")
    List<Complaint> findAllWithTenantDetails();
    
    // Custom query to get complaint by ID with tenant details
    @Query("SELECT c FROM Complaint c JOIN FETCH c.tenant t WHERE c.id = :id AND c.isDeleted = false")
    Optional<Complaint> findByIdWithTenantDetails(@Param("id") Long id);
    
    // Custom query for filtering with multiple criteria
    @Query("SELECT c FROM Complaint c JOIN FETCH c.tenant t WHERE " +
           "(:status IS NULL OR c.complaintStatus = :status) AND " +
           "(:priority IS NULL OR c.priorityLevel = :priority) AND " +
           "(:tenantId IS NULL OR c.tenant.id = :tenantId) AND " +
           "c.isDeleted = false ORDER BY c.createdAt DESC")
    List<Complaint> findComplaintsWithFilters(@Param("status") ComplaintStatus status,
                                            @Param("priority") PriorityLevel priority,
                                            @Param("tenantId") Long tenantId);
    
    // Check if complaint with same title exists for tenant (to avoid duplicates)
    boolean existsByTitleAndTenantIdAndIsDeletedFalse(String title, Long tenantId);
}
