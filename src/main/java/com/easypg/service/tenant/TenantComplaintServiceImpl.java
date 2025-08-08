package com.easypg.service.tenant;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easypg.dao.ComplaintDao;
import com.easypg.dao.TenantDao;
import com.easypg.dto.AddComplaintDTO;
import com.easypg.dto.ComplaintRespDTO;
import com.easypg.entities.Complaint;
import com.easypg.entities.Tenant;
import com.easypg.enums.ComplaintStatus;

@Service
@Transactional
public class TenantComplaintServiceImpl implements TenantComplaintService {

    @Autowired
    private ComplaintDao complaintDao;

    @Autowired
    private TenantDao tenantDao;

    @Override
    public ComplaintRespDTO addNewComplaint(Long tenantId, AddComplaintDTO dto) {
        System.out.println("Service: addNewComplaint for tenant: " + tenantId);
        
        // Find tenant
        Tenant tenant = tenantDao.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found with id: " + tenantId));
        
        // Create new complaint
        Complaint complaint = new Complaint();
        complaint.setTitle(dto.getTitle());
        complaint.setIssue(dto.getIssue());
        complaint.setPriorityLevel(dto.getPriorityLevel());
        complaint.setComplaintStatus(ComplaintStatus.PENDING);
        complaint.setTenant(tenant);
        complaint.setCreatedBy("tenant_" + tenantId);
        complaint.setDeleted(false);
        
        // Save complaint
        Complaint savedComplaint = complaintDao.save(complaint);
        
        // Return the created complaint as DTO
        return mapToComplaintRespDTO(savedComplaint);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComplaintRespDTO> getMyComplaints(Long tenantId) {
        System.out.println("Service: getMyComplaints for tenant: " + tenantId);
        
        // Find tenant
        Tenant tenant = tenantDao.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found with id: " + tenantId));
        
        // Get all complaints for the tenant (excluding deleted ones)
        List<Complaint> complaints = complaintDao.findByTenantAndIsDeletedFalseOrderByCreatedAtDesc(tenant);
        
        return complaints.stream()
                .map(this::mapToComplaintRespDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ComplaintRespDTO updateMyComplaint(Long tenantId, Long complaintId, AddComplaintDTO dto) {
        System.out.println("Service: updateMyComplaint - tenantId: " + tenantId + ", complaintId: " + complaintId);
        
        // Find complaint by ID and validate ownership
        Complaint complaint = complaintDao.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found with id: " + complaintId));
        
        // Check if complaint belongs to the tenant
        if (!complaint.getTenant().getId().equals(tenantId)) {
            throw new RuntimeException("You are not authorized to update this complaint");
        }
        
        // Check if complaint status is PENDING (only pending complaints can be updated)
        if (complaint.getComplaintStatus() != ComplaintStatus.PENDING) {
            throw new RuntimeException("Only pending complaints can be updated");
        }
        
        // Check if complaint is not deleted
        if (complaint.isDeleted()) {
            throw new RuntimeException("Complaint not found");
        }
        
        // Update complaint fields
        complaint.setTitle(dto.getTitle());
        complaint.setIssue(dto.getIssue());
        complaint.setPriorityLevel(dto.getPriorityLevel());
        complaint.setUpdatedBy("tenant_" + tenantId);
        
        // Save updated complaint
        Complaint updatedComplaint = complaintDao.save(complaint);
        
        // Convert to DTO and return
        return mapToComplaintRespDTO(updatedComplaint);
    }

    @Override
    public void deleteMyComplaint(Long tenantId, Long complaintId) {
        System.out.println("Service: deleteMyComplaint - tenantId: " + tenantId + ", complaintId: " + complaintId);
        
        // Find complaint by ID and validate ownership
        Complaint complaint = complaintDao.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found with id: " + complaintId));
        
        // Check if complaint belongs to the tenant
        if (!complaint.getTenant().getId().equals(tenantId)) {
            throw new RuntimeException("You are not authorized to delete this complaint");
        }
        
        // Check if complaint status is PENDING (only pending complaints can be deleted)
        if (complaint.getComplaintStatus() != ComplaintStatus.PENDING) {
            throw new RuntimeException("Only pending complaints can be deleted");
        }
        
        // Check if complaint is not already deleted
        if (complaint.isDeleted()) {
            throw new RuntimeException("Complaint not found");
        }
        
        // Soft delete - set isDeleted flag
        complaint.setDeleted(true);
        complaint.setUpdatedBy("tenant_" + tenantId);
        complaintDao.save(complaint);
        
        System.out.println("Complaint with ID " + complaintId + " deleted successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public ComplaintRespDTO getMyComplaintDetails(Long tenantId, Long complaintId) {
        System.out.println("Service: getMyComplaintDetails - tenantId: " + tenantId + ", complaintId: " + complaintId);
        
        // Find complaint by ID and validate ownership
        Complaint complaint = complaintDao.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found with id: " + complaintId));
        
        // Check if complaint belongs to the tenant
        if (!complaint.getTenant().getId().equals(tenantId)) {
            throw new RuntimeException("You are not authorized to view this complaint");
        }
        
        // Check if complaint is not deleted
        if (complaint.isDeleted()) {
            throw new RuntimeException("Complaint not found");
        }
        
        return mapToComplaintRespDTO(complaint);
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
            dto.setTenantContactNumber(complaint.getTenant().getContactNumber());
            dto.setTenantGender(complaint.getTenant().getGender() != null ? 
                complaint.getTenant().getGender().toString() : null);
        }
        
        return dto;
    }
}