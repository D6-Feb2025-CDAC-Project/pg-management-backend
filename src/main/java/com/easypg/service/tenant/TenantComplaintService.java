package com.easypg.service.tenant;

import java.util.List;
import java.util.Map;

import com.easypg.dto.AddComplaintDTO;
import com.easypg.dto.ApiResponse;
import com.easypg.dto.ComplaintRespDTO;
import com.easypg.dto.ComplaintStatsDTO;

public interface TenantComplaintService {
    
    // Complaint submission - Tenant can create new complaints
    ApiResponse addNewComplaint(Long tenantId, AddComplaintDTO dto);
    
    // View operations - Tenant can only see their own complaints
    List<ComplaintRespDTO> getMyComplaints(Long tenantId);
    ComplaintRespDTO getMyComplaintDetails(Long tenantId, Long complaintId);
    
    // Filter operations - Tenant can filter their own complaints
    List<ComplaintRespDTO> getMyComplaintsByStatus(Long tenantId, String status);
    List<ComplaintRespDTO> getMyComplaintsByPriority(Long tenantId, String priority);
    
    // Statistics - Personal stats only
    ComplaintStatsDTO getMyComplaintStats(Long tenantId);
    
    // Utility operations
    Map<String, Object> checkMyComplaintStatus(Long tenantId, Long complaintId);
    
    // Validation helper
    boolean isMyComplaint(Long tenantId, Long complaintId);
}