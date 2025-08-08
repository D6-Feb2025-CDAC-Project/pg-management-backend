package com.easypg.service.tenant;

import java.util.List;
import com.easypg.dto.AddComplaintDTO;
import com.easypg.dto.ComplaintRespDTO;

/**
 * Service interface for tenant complaint management operations
 */
public interface TenantComplaintService {

    /**
     * Add new complaint for a tenant
     * @param tenantId - ID of the tenant raising the complaint
     * @param dto - Complaint details
     * @return Created complaint details
     */
    ComplaintRespDTO addNewComplaint(Long tenantId, AddComplaintDTO dto);

    /**
     * Get all complaints for a specific tenant
     * @param tenantId - ID of the tenant
     * @return List of complaints belonging to the tenant
     */
    List<ComplaintRespDTO> getMyComplaints(Long tenantId);

    /**
     * Update complaint for authenticated tenant
     * Only allows updating PENDING complaints
     * @param tenantId - ID of the tenant
     * @param complaintId - ID of the complaint to update
     * @param dto - Updated complaint details
     * @return Updated complaint details
     */
    ComplaintRespDTO updateMyComplaint(Long tenantId, Long complaintId, AddComplaintDTO dto);

    /**
     * Delete complaint for authenticated tenant
     * Only allows deleting PENDING complaints
     * @param tenantId - ID of the tenant
     * @param complaintId - ID of the complaint to delete
     */
    void deleteMyComplaint(Long tenantId, Long complaintId);

    /**
     * Get specific complaint details with ownership validation
     * @param tenantId - ID of the tenant
     * @param complaintId - ID of the complaint
     * @return Complaint details
     */
    ComplaintRespDTO getMyComplaintDetails(Long tenantId, Long complaintId);
}