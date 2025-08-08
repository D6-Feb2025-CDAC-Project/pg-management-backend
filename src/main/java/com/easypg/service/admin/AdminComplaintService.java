package com.easypg.service.admin;

import java.util.List;
import com.easypg.dto.ComplaintRespDTO;
import com.easypg.dto.ComplaintStatsDTO;
import com.easypg.dto.UpdateComplaintActionDTO;
import com.easypg.dto.UpdateComplaintPriorityDTO;
import com.easypg.dto.UpdateComplaintStatusDTO;

public interface AdminComplaintService {

    /**
     * Get all complaints (Admin only)
     * @return List of all complaints with tenant details
     */
    List<ComplaintRespDTO> getAllComplaints();

    /**
     * Get complaint details by ID
     * @param complaintId - ID of the complaint
     * @return Complaint details
     */
    ComplaintRespDTO getComplaintDetails(Long complaintId);

    /**
     * Update complaint status
     * @param complaintId - ID of the complaint
     * @param dto - Status update details
     * @return Updated complaint details
     */
    ComplaintRespDTO updateComplaintStatus(Long complaintId, UpdateComplaintStatusDTO dto);

    /**
     * Update complaint priority
     * @param complaintId - ID of the complaint
     * @param dto - Priority update details
     * @return Updated complaint details
     */
    ComplaintRespDTO updateComplaintPriority(Long complaintId, UpdateComplaintPriorityDTO dto);

    /**
     * Add/Update action taken for complaint
     * @param complaintId - ID of the complaint
     * @param dto - Action details
     * @return Updated complaint details
     */
    ComplaintRespDTO updateComplaintAction(Long complaintId, UpdateComplaintActionDTO dto);

    /**
     * Get complaints by status
     * @param status - Status to filter by
     * @return List of complaints with specified status
     */
    List<ComplaintRespDTO> getComplaintsByStatus(String status);

    /**
     * Get complaints by priority
     * @param priority - Priority level to filter by
     * @return List of complaints with specified priority
     */
    List<ComplaintRespDTO> getComplaintsByPriority(String priority);

    /**
     * Get complaint statistics for admin dashboard
     * @return Statistics DTO with counts by status and priority
     */
    ComplaintStatsDTO getComplaintStats();

    /**
     * Filter complaints with multiple criteria
     * @param status - Optional status filter
     * @param priority - Optional priority filter
     * @return Filtered list of complaints
     */
    List<ComplaintRespDTO> filterComplaints(String status, String priority);

    /**
     * Delete complaint (soft delete)
     * @param complaintId - ID of the complaint to delete
     */
    void deleteComplaint(Long complaintId);
}