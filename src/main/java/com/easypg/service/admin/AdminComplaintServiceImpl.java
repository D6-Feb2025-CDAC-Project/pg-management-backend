package com.easypg.service.admin;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easypg.custom_exceptions.InvalidInputException;
import com.easypg.custom_exceptions.ResourceNotFoundException;
import com.easypg.dao.ComplaintDao;
import com.easypg.dto.ApiResponse;
import com.easypg.dto.ComplaintRespDTO;
import com.easypg.dto.ComplaintStatsDTO;
import com.easypg.dto.UpdateComplaintActionDTO;
import com.easypg.dto.UpdateComplaintPriorityDTO;
import com.easypg.dto.UpdateComplaintStatusDTO;
import com.easypg.entities.Complaint;
import com.easypg.enums.ComplaintStatus;
import com.easypg.enums.PriorityLevel;

import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class AdminComplaintServiceImpl implements AdminComplaintService {
    
    private final ComplaintDao complaintDao;

    @Override
    public List<ComplaintRespDTO> getAllComplaints() {
        return complaintDao.findAllWithTenantDetails()
                .stream()
                .map(this::mapToAdminComplaintRespDTO)
                .toList();
    }

    @Override
    public ComplaintRespDTO getComplaintDetails(Long complaintId) {
        Complaint entity = complaintDao.findByIdWithTenantDetails(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with ID: " + complaintId));
        
        return mapToAdminComplaintRespDTO(entity);
    }

    @Override
    public ApiResponse updateComplaintStatus(Long complaintId, UpdateComplaintStatusDTO dto) {
        Complaint complaint = complaintDao.findByIdAndIsDeletedFalse(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with ID: " + complaintId));
        
        ComplaintStatus oldStatus = complaint.getComplaintStatus();
        complaint.setComplaintStatus(dto.getStatus());
        
        // Auto-set resolved date when status changes to RESOLVED
        if (dto.getStatus() == ComplaintStatus.RESOLVED && oldStatus != ComplaintStatus.RESOLVED) {
            complaint.setResolvedDate(LocalDateTime.now());
        }
        
        // Clear resolved date if status changes from RESOLVED
        if (dto.getStatus() != ComplaintStatus.RESOLVED && oldStatus == ComplaintStatus.RESOLVED) {
            complaint.setResolvedDate(null);
        }
        
        return new ApiResponse("Complaint status updated to " + dto.getStatus() + " for ID: " + complaintId);
    }

    @Override
    public ApiResponse updateComplaintAction(Long complaintId, UpdateComplaintActionDTO dto) {
        Complaint complaint = complaintDao.findByIdAndIsDeletedFalse(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with ID: " + complaintId));
        
        complaint.setActionTaken(dto.getActionTaken());
        
        // Auto-update status to IN_PROGRESS when action is first added
        if (complaint.getComplaintStatus() == ComplaintStatus.PENDING) {
            complaint.setComplaintStatus(ComplaintStatus.IN_PROGRESS);
        }
        
        return new ApiResponse("Action taken updated for complaint ID: " + complaintId);
    }

    @Override
    public ApiResponse updateComplaintPriority(Long complaintId, UpdateComplaintPriorityDTO dto) {
        Complaint complaint = complaintDao.findByIdAndIsDeletedFalse(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with ID: " + complaintId));
        
        PriorityLevel oldPriority = complaint.getPriorityLevel();
        complaint.setPriorityLevel(dto.getPriorityLevel());
        
        return new ApiResponse("Complaint priority updated from " + oldPriority + 
                              " to " + dto.getPriorityLevel() + " for ID: " + complaintId);
    }

    @Override
    public ApiResponse deleteComplaint(Long complaintId) {
        Complaint complaint = complaintDao.findByIdAndIsDeletedFalse(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with ID: " + complaintId));
        
        // Soft delete
        complaint.setDeleted(true);
        return new ApiResponse("Complaint deleted successfully with ID: " + complaintId);
    }

    @Override
    public List<ComplaintRespDTO> getComplaintsByStatus(String status) {
        try {
            ComplaintStatus complaintStatus = ComplaintStatus.valueOf(status.toUpperCase());
            return complaintDao.findByComplaintStatusAndIsDeletedFalseOrderByCreatedAtDesc(complaintStatus)
                    .stream()
                    .map(this::mapToAdminComplaintRespDTO)
                    .toList();
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException("Invalid complaint status: " + status + 
                    ". Valid options: PENDING, IN_PROGRESS, RESOLVED");
        }
    }

    @Override
    public List<ComplaintRespDTO> getComplaintsByPriority(String priority) {
        try {
            PriorityLevel priorityLevel = PriorityLevel.valueOf(priority.toUpperCase());
            return complaintDao.findByPriorityLevelAndIsDeletedFalseOrderByCreatedAtDesc(priorityLevel)
                    .stream()
                    .map(this::mapToAdminComplaintRespDTO)
                    .toList();
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException("Invalid priority level: " + priority + 
                    ". Valid options: HIGH, LOW, MODERATE, GENERAL, IMPORTANT, URGENT");
        }
    }

    @Override
    public List<ComplaintRespDTO> getComplaintsByTenant(Long tenantId) {
        return complaintDao.findByTenantIdAndIsDeletedFalseOrderByCreatedAtDesc(tenantId)
                .stream()
                .map(this::mapToAdminComplaintRespDTO)
                .toList();
    }

    @Override
    public List<ComplaintRespDTO> filterComplaints(String status, String priority) {
        ComplaintStatus complaintStatus = null;
        PriorityLevel priorityLevel = null;
        
        // Parse status if provided
        if (status != null && !status.equalsIgnoreCase("all")) {
            try {
                complaintStatus = ComplaintStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new InvalidInputException("Invalid complaint status: " + status);
            }
        }
        
        // Parse priority if provided
        if (priority != null && !priority.equalsIgnoreCase("all")) {
            try {
                priorityLevel = PriorityLevel.valueOf(priority.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new InvalidInputException("Invalid priority level: " + priority);
            }
        }
        
        return complaintDao.findComplaintsWithFilters(complaintStatus, priorityLevel)
                .stream()
                .map(this::mapToAdminComplaintRespDTO)
                .toList();
    }

    @Override
    public ComplaintStatsDTO getComplaintStats() {
        ComplaintStatsDTO stats = new ComplaintStatsDTO();
        
        // Status-wise counts
        stats.setTotalComplaints(complaintDao.countByIsDeletedFalse());
        stats.setPendingComplaints(complaintDao.countByComplaintStatusAndIsDeletedFalse(ComplaintStatus.PENDING));
        stats.setInProgressComplaints(complaintDao.countByComplaintStatusAndIsDeletedFalse(ComplaintStatus.IN_PROGRESS));
        stats.setResolvedComplaints(complaintDao.countByComplaintStatusAndIsDeletedFalse(ComplaintStatus.RESOLVED));
        
        // Priority-wise counts
        stats.setHighPriorityComplaints(complaintDao.countByPriorityLevelAndIsDeletedFalse(PriorityLevel.HIGH));
        stats.setModeratePriorityComplaints(complaintDao.countByPriorityLevelAndIsDeletedFalse(PriorityLevel.MODERATE));
        stats.setLowPriorityComplaints(complaintDao.countByPriorityLevelAndIsDeletedFalse(PriorityLevel.LOW));
        stats.setUrgentPriorityComplaints(complaintDao.countByPriorityLevelAndIsDeletedFalse(PriorityLevel.URGENT));
        stats.setImportantPriorityComplaints(complaintDao.countByPriorityLevelAndIsDeletedFalse(PriorityLevel.IMPORTANT));
        stats.setGeneralPriorityComplaints(complaintDao.countByPriorityLevelAndIsDeletedFalse(PriorityLevel.GENERAL));
        
        return stats;
    }

    @Override
    public ApiResponse bulkUpdateStatus(List<Long> complaintIds, String status) {
        try {
            ComplaintStatus newStatus = ComplaintStatus.valueOf(status.toUpperCase());
            int updatedCount = 0;
            
            for (Long complaintId : complaintIds) {
                try {
                    Complaint complaint = complaintDao.findByIdAndIsDeletedFalse(complaintId)
                            .orElseThrow(() -> new ResourceNotFoundException("Complaint not found: " + complaintId));
                    
                    complaint.setComplaintStatus(newStatus);
                    if (newStatus == ComplaintStatus.RESOLVED) {
                        complaint.setResolvedDate(LocalDateTime.now());
                    }
                    updatedCount++;
                } catch (Exception e) {
                    System.err.println("Failed to update complaint " + complaintId + ": " + e.getMessage());
                }
            }
            
            return new ApiResponse("Bulk status update completed. Updated " + updatedCount + 
                                  " out of " + complaintIds.size() + " complaints to " + newStatus);
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException("Invalid status: " + status);
        }
    }
    
    // Helper method to map entity to Admin-specific DTO (includes full tenant details)
    private ComplaintRespDTO mapToAdminComplaintRespDTO(Complaint complaint) {
        ComplaintRespDTO dto = new ComplaintRespDTO();
        
        // Map complaint fields
        dto.setId(complaint.getId());
        dto.setTitle(complaint.getTitle());
        dto.setIssue(complaint.getIssue());
        dto.setComplaintStatus(complaint.getComplaintStatus());
        dto.setPriorityLevel(complaint.getPriorityLevel());
        dto.setResolvedDate(complaint.getResolvedDate());
        dto.setActionTaken(complaint.getActionTaken());
        dto.setCreatedAt(complaint.getCreatedAt());
        dto.setUpdatedAt(complaint.getUpdatedAt());
        
        // Map full tenant details (Admin can see all tenant info)
        if (complaint.getTenant() != null) {
            dto.setTenantId(complaint.getTenant().getId());
            if (complaint.getTenant().getUser() != null) {
                dto.setTenantName(complaint.getTenant().getUser().getUsername());
                dto.setTenantEmail(complaint.getTenant().getUser().getEmail());
            }
            dto.setTenantContactNumber(complaint.getTenant().getContactNumber());
            dto.setTenantGender(complaint.getTenant().getGender());
        }
        
        return dto;
    }
}