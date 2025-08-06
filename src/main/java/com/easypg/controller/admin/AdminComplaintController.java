package com.easypg.controller.admin;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.easypg.dto.ComplaintRespDTO;
import com.easypg.dto.ComplaintStatsDTO;
import com.easypg.dto.UpdateComplaintActionDTO;
import com.easypg.dto.UpdateComplaintPriorityDTO;
import com.easypg.dto.UpdateComplaintStatusDTO;
import com.easypg.service.admin.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/admin/complaints")
@CrossOrigin(origins = "http://localhost:3000")
@AllArgsConstructor
@Validated
@Tag(name = "Admin Complaint Management", description = "APIs for admin to manage all complaints")
public class AdminComplaintController {

    private final AdminComplaintService adminComplaintService;

    /*
     * GET ALL COMPLAINTS - Admin can view all complaints
     * URL - http://host:port/admin/complaints
     * Method - GET 
     * Resp - List of all complaints with tenant details
     */
    @GetMapping
    @Operation(description = "Get all complaints (Admin only)")
    public ResponseEntity<?> getAllComplaints() {
        System.out.println("Admin: getting all complaints");
        List<ComplaintRespDTO> complaints = adminComplaintService.getAllComplaints();
        if (complaints.isEmpty())
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        return ResponseEntity.ok(complaints);
    }

    /*
     * GET COMPLAINT BY ID - Admin can view any complaint details
     * URL - http://host:port/admin/complaints/{complaintId}
     * Method - GET 
     */
    @GetMapping("/{complaintId}")
    @Operation(description = "Get complaint details by ID (Admin only)")
    public ResponseEntity<?> getComplaintDetails(@PathVariable @Min(1) @Max(10000) Long complaintId) {
        System.out.println("Admin: getting complaint details " + complaintId);
        return ResponseEntity.ok(adminComplaintService.getComplaintDetails(complaintId));
    }

    /*
     * UPDATE COMPLAINT STATUS - Admin can change status (Pending/InProgress/Resolved)
     * URL - http://host:port/admin/complaints/{complaintId}/status
     * Method - PUT
     */
    @PutMapping("/{complaintId}/status")
    @Operation(description = "Update complaint status (Admin only)")
    public ResponseEntity<?> updateComplaintStatus(@PathVariable @Min(1) Long complaintId,
            @RequestBody @Valid UpdateComplaintStatusDTO dto) {
        System.out.println("Admin: updating complaint status " + complaintId + " to " + dto);
        return ResponseEntity.ok(adminComplaintService.updateComplaintStatus(complaintId, dto));
    }

    /*
     * ADD ACTION TAKEN - Admin can add what action was taken
     * URL - http://host:port/admin/complaints/{complaintId}/action
     * Method - PUT
     */
    @PutMapping("/{complaintId}/action")
    @Operation(description = "Add/Update action taken for complaint (Admin only)")
    public ResponseEntity<?> updateComplaintAction(@PathVariable @Min(1) Long complaintId,
            @RequestBody @Valid UpdateComplaintActionDTO dto) {
        System.out.println("Admin: adding action for complaint " + complaintId);
        return ResponseEntity.ok(adminComplaintService.updateComplaintAction(complaintId, dto));
    }

    /*
     * UPDATE COMPLAINT PRIORITY - Admin can change priority level
     * URL - http://host:port/admin/complaints/{complaintId}/priority
     * Method - PUT
     */
    @PutMapping("/{complaintId}/priority")
    @Operation(description = "Update complaint priority (Admin only)")
    public ResponseEntity<?> updateComplaintPriority(@PathVariable @Min(1) Long complaintId,
            @RequestBody @Valid UpdateComplaintPriorityDTO dto) {
        System.out.println("Admin: updating complaint priority " + complaintId);
        return ResponseEntity.ok(adminComplaintService.updateComplaintPriority(complaintId, dto));
    }

    /*
     * SOFT DELETE COMPLAINT - Admin can delete complaints
     * URL - http://host:port/admin/complaints/{complaintId}
     * Method - DELETE
     */
    @DeleteMapping("/{complaintId}")
    @Operation(description = "Soft delete complaint (Admin only)")
    public ResponseEntity<?> deleteComplaint(@PathVariable @Min(1) Long complaintId) {
        System.out.println("Admin: deleting complaint " + complaintId);
        return ResponseEntity.ok(adminComplaintService.deleteComplaint(complaintId));
    }

    /*
     * FILTER BY STATUS - Admin can filter complaints by status
     * URL - http://host:port/admin/complaints/status/{status}
     * Method - GET
     */
    @GetMapping("/status/{status}")
    @Operation(description = "Get complaints by status (Admin only)")
    public ResponseEntity<?> getComplaintsByStatus(@PathVariable String status) {
        System.out.println("Admin: getting complaints by status " + status);
        List<ComplaintRespDTO> complaints = adminComplaintService.getComplaintsByStatus(status);
        if (complaints.isEmpty())
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        return ResponseEntity.ok(complaints);
    }

    /*
     * FILTER BY PRIORITY - Admin can filter complaints by priority
     * URL - http://host:port/admin/complaints/priority/{priorityLevel}
     * Method - GET
     */
    @GetMapping("/priority/{priorityLevel}")
    @Operation(description = "Get complaints by priority level (Admin only)")
    public ResponseEntity<?> getComplaintsByPriority(@PathVariable String priorityLevel) {
        System.out.println("Admin: getting complaints by priority " + priorityLevel);
        List<ComplaintRespDTO> complaints = adminComplaintService.getComplaintsByPriority(priorityLevel);
        if (complaints.isEmpty())
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        return ResponseEntity.ok(complaints);
    }

    /*
     * GET COMPLAINTS BY TENANT - Admin can view specific tenant's complaints
     * URL - http://host:port/admin/complaints/tenant/{tenantId}
     * Method - GET
     */
    @GetMapping("/tenant/{tenantId}")
    @Operation(description = "Get complaints by tenant ID (Admin only)")
    public ResponseEntity<?> getComplaintsByTenant(@PathVariable @Min(1) Long tenantId) {
        System.out.println("Admin: getting complaints for tenant " + tenantId);
        List<ComplaintRespDTO> complaints = adminComplaintService.getComplaintsByTenant(tenantId);
        if (complaints.isEmpty())
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        return ResponseEntity.ok(complaints);
    }

    /*
     * GET COMPLAINT STATISTICS - Admin dashboard stats
     * URL - http://host:port/admin/complaints/stats
     * Method - GET
     */
    @GetMapping("/stats")
    @Operation(description = "Get complaint statistics (Admin only)")
    public ResponseEntity<?> getComplaintStats() {
        System.out.println("Admin: getting complaint statistics");
        ComplaintStatsDTO stats = adminComplaintService.getComplaintStats();
        return ResponseEntity.ok(stats);
    }

    /*
     * ADVANCED FILTERING - Admin can filter with multiple criteria
     * URL - http://host:port/admin/complaints/filter?status=PENDING&priority=HIGH&tenantId=1
     * Method - GET
     */
    @GetMapping("/filter")
    @Operation(description = "Filter complaints with multiple criteria (Admin only)")
    public ResponseEntity<?> filterComplaints(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) Long tenantId) {
        System.out.println("Admin: filtering complaints - status:" + status + 
                          ", priority:" + priority + ", tenantId:" + tenantId);
        
        List<ComplaintRespDTO> complaints = adminComplaintService.filterComplaints(status, priority, tenantId);
        
        if (complaints.isEmpty())
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        return ResponseEntity.ok(complaints);
    }
}