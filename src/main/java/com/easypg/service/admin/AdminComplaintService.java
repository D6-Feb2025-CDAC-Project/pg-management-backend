package com.easypg.service.admin;

import java.util.List;
import com.easypg.dto.ComplaintRespDTO;
import com.easypg.dto.ComplaintStatsDTO;
import com.easypg.dto.UpdateComplaintActionDTO;
import com.easypg.dto.UpdateComplaintPriorityDTO;
import com.easypg.dto.UpdateComplaintStatusDTO;
import com.easypg.dto.ApiResponse;

public interface AdminComplaintService {
    
    // View operations - Admin can see all complaints
    List<ComplaintRespDTO> getAllComplaints();
    ComplaintRespDTO getComplaintDetails(Long complaintId);
    
    // Management operations - Admin exclusive
    ApiResponse updateComplaintStatus(Long complaintId, UpdateComplaintStatusDTO dto);
    ApiResponse updateComplaintAction(Long complaintId, UpdateComplaintActionDTO dto);
    ApiResponse updateComplaintPriority(Long complaintId, UpdateComplaintPriorityDTO dto);
    ApiResponse deleteComplaint(Long complaintId);
    
    // Filter operations - Admin can filter all complaints
    List<ComplaintRespDTO> getComplaintsByStatus(String status);
    List<ComplaintRespDTO> getComplaintsByPriority(String priority);
    List<ComplaintRespDTO> getComplaintsByTenant(Long tenantId);
    
    // Advanced filtering
    List<ComplaintRespDTO> filterComplaints(String status, String priority);
    
    // Statistics - Global stats for admin dashboard
    ComplaintStatsDTO getComplaintStats();
    
    // Bulk operations (Admin only)
    ApiResponse bulkUpdateStatus(List<Long> complaintIds, String status);
}