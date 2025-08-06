package com.easypg.service.tenant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easypg.custom_exceptions.InvalidInputException;
import com.easypg.custom_exceptions.ResourceNotFoundException;
import com.easypg.custom_exceptions.UnauthorizedAccessException;
import com.easypg.dao.ComplaintDao;
//import com.easypg.dao.TenantDao;
import com.easypg.dto.AddComplaintDTO;
import com.easypg.dto.ApiResponse;
import com.easypg.dto.ComplaintRespDTO;
import com.easypg.dto.ComplaintStatsDTO;
import com.easypg.entities.Complaint;
import com.easypg.entities.Tenant;
import com.easypg.enums.ComplaintStatus;
import com.easypg.enums.PriorityLevel;

import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class TenantComplaintServiceImpl implements TenantComplaintService {
    
    private final ComplaintDao complaintDao;
    //private final TenantDao tenantDao;

    @Override
    public ApiResponse addNewComplaint(Long tenantId, AddComplaintDTO dto) {
        // Validate tenant exists and is active
//        Tenant tenant = tenantDao.findByIdAndIsDeletedFalse(tenantId)
//                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with ID: " + tenantId));
        
        // Check for duplicate complaint title for the same tenant (business rule)
        if (complaintDao.existsByTitleAndTenantIdAndIsDeletedFalse(dto.getTitle(), tenantId)) {
            throw new InvalidInputException("You already have a complaint with this title. Please use a different title.");
        }
        
        // Create new complaint entity
        Complaint complaint = new Complaint();
        complaint.setTitle(dto.getTitle());
        complaint.setIssue(dto.getIssue());
        complaint.setPriorityLevel(dto.getPriorityLevel());
        //complaint.setTenant(tenant);
        complaint.setComplaintStatus(ComplaintStatus.PENDING); // Always starts as PENDING
        complaint.setDeleted(false);
        
        // Save the complaint
        Complaint savedComplaint = complaintDao.save(complaint);
        
        return new ApiResponse("Your complaint has been submitted successfully with ID: " + savedComplaint.getId() + 
                              ". You will be notified once it's reviewed by our team.");
    }

    @Override
    public List<ComplaintRespDTO> getMyComplaints(Long tenantId) {
        // Validate tenant exists
       // validateTenantExists(tenantId);
        
        return complaintDao.findByTenantIdAndIsDeletedFalseOrderByCreatedAtDesc(tenantId)
                .stream()
                .map(this::mapToTenantComplaintRespDTO)
                .toList();
    }

    @Override
    public ComplaintRespDTO getMyComplaintDetails(Long tenantId, Long complaintId) {
        // Validate tenant exists
      //  validateTenantExists(tenantId);
        
        Complaint complaint = complaintDao.findByIdAndIsDeletedFalse(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with ID: " + complaintId));
        
        // Security check - ensure complaint belongs to this tenant
        if (!complaint.getTenant().getId().equals(tenantId)) {
            throw new UnauthorizedAccessException("You can only view your own complaints");
        }
        
        return mapToTenantComplaintRespDTO(complaint);
    }

    @Override
    public List<ComplaintRespDTO> getMyComplaintsByStatus(Long tenantId, String status) {
        // Validate tenant exists
      //  validateTenantExists(tenantId);
        
        try {
            ComplaintStatus complaintStatus = ComplaintStatus.valueOf(status.toUpperCase());
            return complaintDao.findByTenantIdAndComplaintStatusAndIsDeletedFalseOrderByCreatedAtDesc(tenantId, complaintStatus)
                    .stream()
                    .map(this::mapToTenantComplaintRespDTO)
                    .toList();
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException("Invalid status: " + status + 
                    ". Valid options: PENDING, IN_PROGRESS, RESOLVED");
        }
    }

    @Override
    public List<ComplaintRespDTO> getMyComplaintsByPriority(Long tenantId, String priority) {
        // Validate tenant exists
       // validateTenantExists(tenantId);
        
        try {
            PriorityLevel priorityLevel = PriorityLevel.valueOf(priority.toUpperCase());
            return complaintDao.findByTenantIdAndPriorityLevelAndIsDeletedFalseOrderByCreatedAtDesc(tenantId, priorityLevel)
                    .stream()
                    .map(this::mapToTenantComplaintRespDTO)
                    .toList();
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException("Invalid priority: " + priority + 
                    ". Valid options: HIGH, LOW, MODERATE, GENERAL, IMPORTANT, URGENT");
        }
    }

    @Override
    public ComplaintStatsDTO getMyComplaintStats(Long tenantId) {
        // Validate tenant exists
       // validateTenantExists(tenantId);
        
        // Get all complaints for this tenant
        List<Complaint> myComplaints = complaintDao.findByTenantIdAndIsDeletedFalseOrderByCreatedAtDesc(tenantId);
        
        ComplaintStatsDTO stats = new ComplaintStatsDTO();
        
        // Calculate tenant-specific stats
        stats.setTotalComplaints(myComplaints.size());
        stats.setPendingComplaints(myComplaints.stream()
                .filter(c -> c.getComplaintStatus() == ComplaintStatus.PENDING).count());
        stats.setInProgressComplaints(myComplaints.stream()
                .filter(c -> c.getComplaintStatus() == ComplaintStatus.IN_PROGRESS).count());
        stats.setResolvedComplaints(myComplaints.stream()
                .filter(c -> c.getComplaintStatus() == ComplaintStatus.RESOLVED).count());
        
        // Priority-wise stats for this tenant
        stats.setHighPriorityComplaints(myComplaints.stream()
                .filter(c -> c.getPriorityLevel() == PriorityLevel.HIGH).count());
        stats.setModeratePriorityComplaints(myComplaints.stream()
                .filter(c -> c.getPriorityLevel() == PriorityLevel.MODERATE).count());
        stats.setLowPriorityComplaints(myComplaints.stream()
                .filter(c -> c.getPriorityLevel() == PriorityLevel.LOW).count());
        stats.setUrgentPriorityComplaints(myComplaints.stream()
                .filter(c -> c.getPriorityLevel() == PriorityLevel.URGENT).count());
        stats.setImportantPriorityComplaints(myComplaints.stream()
                .filter(c -> c.getPriorityLevel() == PriorityLevel.IMPORTANT).count());
        stats.setGeneralPriorityComplaints(myComplaints.stream()
                .filter(c -> c.getPriorityLevel() == PriorityLevel.GENERAL).count());
        
        return stats;
    }

    @Override
    public Map<String, Object> checkMyComplaintStatus(Long tenantId, Long complaintId) {
        // Validate tenant exists
       // validateTenantExists(tenantId);
        
        Complaint complaint = complaintDao.findByIdAndIsDeletedFalse(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with ID: " + complaintId));
        
        // Security check - ensure complaint belongs to this tenant
        if (!complaint.getTenant().getId().equals(tenantId)) {
            throw new UnauthorizedAccessException("You can only check your own complaints");
        }
        
        // Return tenant-friendly status information
        Map<String, Object> statusInfo = new HashMap<>();
        statusInfo.put("complaintId", complaint.getId());
        statusInfo.put("title", complaint.getTitle());
        statusInfo.put("status", complaint.getComplaintStatus().name());
        statusInfo.put("statusDescription", getStatusDescription(complaint.getComplaintStatus()));
        statusInfo.put("priority", complaint.getPriorityLevel().name());
        statusInfo.put("submittedOn", complaint.getCreatedAt());
        statusInfo.put("lastUpdated", complaint.getUpdatedAt());
        
        // Action taken information
        if (complaint.getActionTaken() != null && !complaint.getActionTaken().trim().isEmpty()) {
            statusInfo.put("actionTaken", complaint.getActionTaken());
        } else {
            statusInfo.put("actionTaken", "No action taken yet. Your complaint is in queue for review.");
        }
        
        // Resolution information
        if (complaint.getResolvedDate() != null) {
            statusInfo.put("resolvedOn", complaint.getResolvedDate());
            statusInfo.put("resolutionMessage", "Your complaint has been successfully resolved.");
        } else {
            statusInfo.put("resolvedOn", null);
            statusInfo.put("estimatedResolution", getEstimatedResolutionTime(complaint.getPriorityLevel()));
        }
        
        return statusInfo;
    }

    @Override
    public boolean isMyComplaint(Long tenantId, Long complaintId) {
        try {
            Complaint complaint = complaintDao.findByIdAndIsDeletedFalse(complaintId)
                    .orElse(null);
            return complaint != null && complaint.getTenant().getId().equals(tenantId);
        } catch (Exception e) {
            return false;
        }
    }
    
    // Helper methods
//    private void validateTenantExists(Long tenantId) {
//        if (!tenantDao.existsByIdAndIsDeletedFalse(tenantId)) {
//            throw new ResourceNotFoundException("Tenant not found with ID: " + tenantId);
//        }
//    }
//    
    private String getStatusDescription(ComplaintStatus status) {
        return switch (status) {
            case PENDING -> "Your complaint has been received and is waiting for review.";
            case IN_PROGRESS -> "Our team is actively working on your complaint.";
            case RESOLVED -> "Your complaint has been successfully resolved.";
        };
    }
    
    private String getEstimatedResolutionTime(PriorityLevel priority) {
        return switch (priority) {
            case URGENT -> "Within 24 hours";
            case HIGH -> "Within 2-3 business days";
            case IMPORTANT -> "Within 3-5 business days";
            case MODERATE -> "Within 5-7 business days";
            case LOW -> "Within 7-10 business days";
            case GENERAL -> "Within 10-15 business days";
        };
    }
    
    // Helper method to map entity to Tenant-specific DTO (limited information)
    private ComplaintRespDTO mapToTenantComplaintRespDTO(Complaint complaint) {
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
        
        // Limited tenant info (only their own basic details)
        if (complaint.getTenant() != null) {
            dto.setTenantId(complaint.getTenant().getId());
            // Don't expose sensitive tenant information in tenant service
            // Tenant already knows their own details
        }
        
        return dto;
    }
}