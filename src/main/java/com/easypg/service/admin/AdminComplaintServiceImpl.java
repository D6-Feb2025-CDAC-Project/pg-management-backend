package com.easypg.service.admin;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easypg.dao.ComplaintDao;
import com.easypg.dto.ComplaintRespDTO;
import com.easypg.dto.ComplaintStatsDTO;
import com.easypg.dto.UpdateComplaintActionDTO;
import com.easypg.dto.UpdateComplaintPriorityDTO;
import com.easypg.dto.UpdateComplaintStatusDTO;
import com.easypg.entities.Complaint;
import com.easypg.enums.ComplaintStatus;
import com.easypg.enums.PriorityLevel;

@Service
@Transactional
public class AdminComplaintServiceImpl implements AdminComplaintService {

    @Autowired
    private ComplaintDao complaintDao;

    @Override
    @Transactional(readOnly = true)
    public List<ComplaintRespDTO> getAllComplaints() {
        System.out.println("Admin Service: getAllComplaints");
        
        List<Complaint> complaints = complaintDao.findAllActiveComplaintsOrderByCreatedAtDesc();
        
        return complaints.stream()
                .map(this::mapToComplaintRespDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ComplaintRespDTO getComplaintDetails(Long complaintId) {
        System.out.println("Admin Service: getComplaintDetails - complaintId: " + complaintId);
        
        Complaint complaint = complaintDao.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found with id: " + complaintId));
        
        if (complaint.isDeleted()) {
            throw new RuntimeException("Complaint not found");
        }
        
        return mapToComplaintRespDTO(complaint);
    }

    @Override
    public ComplaintRespDTO updateComplaintStatus(Long complaintId, UpdateComplaintStatusDTO dto) {
        System.out.println("Admin Service: updateComplaintStatus - complaintId: " + complaintId + 
                          ", status: " + dto.getComplaintStatus());
        
        Complaint complaint = complaintDao.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found with id: " + complaintId));
        
        if (complaint.isDeleted()) {
            throw new RuntimeException("Complaint not found");
        }
        
        // Update status
        complaint.setComplaintStatus(dto.getComplaintStatus());
        complaint.setUpdatedBy("admin");
        
        // Set resolved date if status is RESOLVED
        if (dto.getComplaintStatus() == ComplaintStatus.RESOLVED) {
            complaint.setResolvedDate(LocalDateTime.now());
        } else {
            complaint.setResolvedDate(null);
        }
        
        Complaint savedComplaint = complaintDao.save(complaint);
        return mapToComplaintRespDTO(savedComplaint);
    }

    @Override
    public ComplaintRespDTO updateComplaintPriority(Long complaintId, UpdateComplaintPriorityDTO dto) {
        System.out.println("Admin Service: updateComplaintPriority - complaintId: " + complaintId + 
                          ", priority: " + dto.getPriorityLevel());
        
        Complaint complaint = complaintDao.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found with id: " + complaintId));
        
        if (complaint.isDeleted()) {
            throw new RuntimeException("Complaint not found");
        }
        
        // Update priority
        complaint.setPriorityLevel(dto.getPriorityLevel());
        complaint.setUpdatedBy("admin");
        
        Complaint savedComplaint = complaintDao.save(complaint);
        return mapToComplaintRespDTO(savedComplaint);
    }

    @Override
    public ComplaintRespDTO updateComplaintAction(Long complaintId, UpdateComplaintActionDTO dto) {
        System.out.println("Admin Service: updateComplaintAction - complaintId: " + complaintId);
        
        Complaint complaint = complaintDao.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found with id: " + complaintId));
        
        if (complaint.isDeleted()) {
            throw new RuntimeException("Complaint not found");
        }
        
        // Update action taken and set status to IN_PROGRESS if it's PENDING
        complaint.setActionTaken(dto.getActionTaken());
        complaint.setUpdatedBy("admin");
        
        if (complaint.getComplaintStatus() == ComplaintStatus.PENDING) {
            complaint.setComplaintStatus(ComplaintStatus.IN_PROGRESS);
        }
        
        Complaint savedComplaint = complaintDao.save(complaint);
        return mapToComplaintRespDTO(savedComplaint);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComplaintRespDTO> getComplaintsByStatus(String status) {
        System.out.println("Admin Service: getComplaintsByStatus - status: " + status);
        
        try {
            ComplaintStatus complaintStatus = ComplaintStatus.valueOf(status.toUpperCase());
            List<Complaint> complaints = complaintDao.findByComplaintStatusAndIsDeletedFalseOrderByCreatedAtDesc(complaintStatus);
            
            return complaints.stream()
                    .map(this::mapToComplaintRespDTO)
                    .collect(Collectors.toList());
                    
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status: " + status + ". Valid statuses are: PENDING, RESOLVED, IN_PROGRESS");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComplaintRespDTO> getComplaintsByPriority(String priority) {
        System.out.println("Admin Service: getComplaintsByPriority - priority: " + priority);
        
        try {
            PriorityLevel priorityLevel = PriorityLevel.valueOf(priority.toUpperCase());
            List<Complaint> complaints = complaintDao.findByPriorityLevelAndIsDeletedFalseOrderByCreatedAtDesc(priorityLevel);
            
            return complaints.stream()
                    .map(this::mapToComplaintRespDTO)
                    .collect(Collectors.toList());
                    
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid priority: " + priority + 
                    ". Valid priorities are: HIGH, LOW, MODERATE, GENERAL, IMPORTANT, URGENT");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ComplaintStatsDTO getComplaintStats() {
        System.out.println("Admin Service: getComplaintStats");
        
        ComplaintStatsDTO stats = new ComplaintStatsDTO();
        
        // Get total count
        stats.setTotalComplaints(complaintDao.countByIsDeletedFalse());
        
        // Get counts by status
        stats.setPendingCount(complaintDao.countByComplaintStatusAndIsDeletedFalse(ComplaintStatus.PENDING));
        stats.setInProgressCount(complaintDao.countByComplaintStatusAndIsDeletedFalse(ComplaintStatus.IN_PROGRESS));
        stats.setResolvedCount(complaintDao.countByComplaintStatusAndIsDeletedFalse(ComplaintStatus.RESOLVED));
        
        // Get counts by priority
        stats.setHighPriorityCount(complaintDao.countByPriorityLevelAndIsDeletedFalse(PriorityLevel.HIGH));
        stats.setUrgentPriorityCount(complaintDao.countByPriorityLevelAndIsDeletedFalse(PriorityLevel.URGENT));
        stats.setModeratePriorityCount(complaintDao.countByPriorityLevelAndIsDeletedFalse(PriorityLevel.MODERATE));
        stats.setLowPriorityCount(complaintDao.countByPriorityLevelAndIsDeletedFalse(PriorityLevel.LOW));
        
        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComplaintRespDTO> filterComplaints(String status, String priority) {
        System.out.println("Admin Service: filterComplaints - status: " + status + ", priority: " + priority);
        
        List<Complaint> complaints;
        
        if (status != null && priority != null) {
            // Filter by both status and priority
            ComplaintStatus complaintStatus = ComplaintStatus.valueOf(status.toUpperCase());
            PriorityLevel priorityLevel = PriorityLevel.valueOf(priority.toUpperCase());
            complaints = complaintDao.findByComplaintStatusAndPriorityLevelAndIsDeletedFalseOrderByCreatedAtDesc(
                    complaintStatus, priorityLevel);
        } else if (status != null) {
            // Filter by status only
            ComplaintStatus complaintStatus = ComplaintStatus.valueOf(status.toUpperCase());
            complaints = complaintDao.findByComplaintStatusAndIsDeletedFalseOrderByCreatedAtDesc(complaintStatus);
        } else if (priority != null) {
            // Filter by priority only
            PriorityLevel priorityLevel = PriorityLevel.valueOf(priority.toUpperCase());
            complaints = complaintDao.findByPriorityLevelAndIsDeletedFalseOrderByCreatedAtDesc(priorityLevel);
        } else {
            // No filters, return all
            complaints = complaintDao.findAllActiveComplaintsOrderByCreatedAtDesc();
        }
        
        return complaints.stream()
                .map(this::mapToComplaintRespDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteComplaint(Long complaintId) {
        System.out.println("Admin Service: deleteComplaint - complaintId: " + complaintId);
        
        Complaint complaint = complaintDao.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found with id: " + complaintId));
        
        if (complaint.isDeleted()) {
            throw new RuntimeException("Complaint already deleted");
        }
        
        // Soft delete
        complaint.setDeleted(true);
        complaint.setUpdatedBy("admin");
        complaintDao.save(complaint);
        
        System.out.println("Complaint with ID " + complaintId + " deleted successfully by admin");
    }

    // Helper method to convert Complaint entity to ComplaintRespDTO
    private ComplaintRespDTO mapToComplaintRespDTO(Complaint complaint) {
        ComplaintRespDTO dto = new ComplaintRespDTO();
        dto.setId(complaint.getId());
        dto.setTitle(complaint.getTitle());
        dto.setIssue(complaint.getIssue());
        dto.setComplaintStatus(complaint.getComplaintStatus());
        dto.setPriorityLevel(complaint.getPriorityLevel());
        dto.setResolvedDate(complaint.getResolvedDate());
        dto.setActionTaken(complaint.getActionTaken());
        dto.setCreatedAt(complaint.getCreatedAt());
        dto.setUpdatedAt(complaint.getUpdatedAt());
        
        // Map tenant information
        if (complaint.getTenant() != null) {
            dto.setTenantId(complaint.getTenant().getId());
            // Add tenant name  if available in Tenant entity
            if (complaint.getTenant().getUser() != null) {
                dto.setTenantName(complaint.getTenant().getUser().getUsername());
            }
            // dto.setTenantEmail(complaint.getTenant().getEmail());
            dto.setTenantContactNumber(complaint.getTenant().getContactNumber());
            dto.setTenantGender(complaint.getTenant().getGender() != null ? 
                complaint.getTenant().getGender().toString() : null);
        }
        
        return dto;
    }
}