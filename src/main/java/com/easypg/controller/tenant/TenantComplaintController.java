package com.easypg.controller.tenant;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.easypg.dto.AddComplaintDTO;
import com.easypg.dto.ComplaintRespDTO;
import com.easypg.dto.ComplaintStatsDTO;
import com.easypg.service.tenant.TenantComplaintService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/tenant/complaints")
@CrossOrigin(origins = "http://localhost:3000")
@AllArgsConstructor
@Validated
@Tag(name = "Tenant Complaint Management", description = "APIs for tenants to manage their own complaints")
public class TenantComplaintController {

    private final TenantComplaintService tenantComplaintService;

    /*
     * SUBMIT NEW COMPLAINT - Tenant can raise new complaint
     * URL - http://host:port/tenant/complaints/{tenantId}
     * Method - POST 
     * Payload - JSON representation of complaint (without tenantId)
     * Resp - success - SC 201 + ApiResp - success mesg
     */
//    @PostMapping("/{tenantId}")
//    @Operation(description = "Submit new complaint (Tenant)")
//    public ResponseEntity<?> submitComplaint(@PathVariable @Min(1) Long tenantId,
//            @RequestBody @Valid AddComplaintDTO dto) {
//        System.out.println("Tenant " + tenantId + ": submitting complaint " + dto);
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(tenantComplaintService.addNewComplaint(tenantId, dto));
//    }

    /*
     * SUBMIT COMPLAINT (AUTHENTICATED) - For when JWT auth is implemented
     * URL - http://host:port/tenant/complaints
     * Method - POST 
     */
    @PostMapping
    @Operation(description = "Submit new complaint (Authenticated Tenant)")
    public ResponseEntity<?> submitMyComplaint(@RequestBody @Valid AddComplaintDTO dto) {
        System.out.println("Authenticated tenant: submitting complaint " + dto);
        // TODO: Get tenantId from JWT token/authentication context
        // Long tenantId = getCurrentTenantId();
        Long tenantId = 1L; // Temporary - replace with actual authentication logic
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tenantComplaintService.addNewComplaint(tenantId, dto));
    }

    /*
     * GET MY COMPLAINTS - Tenant can view their own complaints only
     * URL - http://host:port/tenant/complaints/{tenantId}/my-complaints
     * Method - GET 
     */
    @GetMapping("/{tenantId}/my-complaints")
    @Operation(description = "Get my complaints (Tenant)")
    public ResponseEntity<?> getMyComplaints(@PathVariable @Min(1) Long tenantId) {
        System.out.println("Tenant " + tenantId + ": getting my complaints");
        List<ComplaintRespDTO> complaints = tenantComplaintService.getMyComplaints(tenantId);
        if (complaints.isEmpty())
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        return ResponseEntity.ok(complaints);
    }

    /*
     * GET MY COMPLAINTS (AUTHENTICATED) - For when JWT auth is implemented
     * URL - http://host:port/tenant/complaints/my-complaints
     * Method - GET 
     */
    @GetMapping("/my-complaints")
    @Operation(description = "Get my complaints (Authenticated Tenant)")
    public ResponseEntity<?> getMyComplaintsAuth() {
        System.out.println("Authenticated tenant: getting my complaints");
        // TODO: Get tenantId from JWT token/authentication context
        // Long tenantId = getCurrentTenantId();
        Long tenantId = 1L; // Temporary - replace with actual authentication logic
        List<ComplaintRespDTO> complaints = tenantComplaintService.getMyComplaints(tenantId);
        if (complaints.isEmpty())
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        return ResponseEntity.ok(complaints);
    }

    /*
     * GET SPECIFIC COMPLAINT - Tenant can view their own complaint details
     * URL - http://host:port/tenant/complaints/{tenantId}/complaint/{complaintId}
     * Method - GET 
     */
    @GetMapping("/{tenantId}/complaint/{complaintId}")
    @Operation(description = "Get specific complaint details (Tenant)")
    public ResponseEntity<?> getMyComplaintDetails(@PathVariable @Min(1) Long tenantId,
            @PathVariable @Min(1) @Max(10000) Long complaintId) {
        System.out.println("Tenant " + tenantId + ": getting complaint details " + complaintId);
        
        try {
            // Get complaint details with security validation
            ComplaintRespDTO complaint = tenantComplaintService.getMyComplaintDetails(tenantId, complaintId);
            return ResponseEntity.ok(complaint);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Complaint not found"));
        }
    }

    /*
     * GET MY COMPLAINTS BY STATUS - Tenant can filter their own complaints
     * URL - http://host:port/tenant/complaints/{tenantId}/status/{status}
     * Method - GET 
     */
    @GetMapping("/{tenantId}/status/{status}")
    @Operation(description = "Get my complaints by status (Tenant)")
    public ResponseEntity<?> getMyComplaintsByStatus(@PathVariable @Min(1) Long tenantId,
            @PathVariable String status) {
        System.out.println("Tenant " + tenantId + ": getting complaints by status " + status);
        
        List<ComplaintRespDTO> filteredComplaints = tenantComplaintService.getMyComplaintsByStatus(tenantId, status);
        
        if (filteredComplaints.isEmpty())
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        return ResponseEntity.ok(filteredComplaints);
    }

    /*
     * GET MY COMPLAINTS BY PRIORITY - Tenant can filter their own complaints
     * URL - http://host:port/tenant/complaints/{tenantId}/priority/{priority}
     * Method - GET 
     */
    @GetMapping("/{tenantId}/priority/{priority}")
    @Operation(description = "Get my complaints by priority (Tenant)")
    public ResponseEntity<?> getMyComplaintsByPriority(@PathVariable @Min(1) Long tenantId,
            @PathVariable String priority) {
        System.out.println("Tenant " + tenantId + ": getting complaints by priority " + priority);
        
        List<ComplaintRespDTO> filteredComplaints = tenantComplaintService.getMyComplaintsByPriority(tenantId, priority);
        
        if (filteredComplaints.isEmpty())
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        return ResponseEntity.ok(filteredComplaints);
    }

    /*
     * GET MY COMPLAINT STATS - Tenant can see their complaint statistics
     * URL - http://host:port/tenant/complaints/{tenantId}/stats
     * Method - GET 
     */
    @GetMapping("/{tenantId}/stats")
    @Operation(description = "Get my complaint statistics (Tenant)")
    public ResponseEntity<?> getMyComplaintStats(@PathVariable @Min(1) Long tenantId) {
        System.out.println("Tenant " + tenantId + ": getting my complaint stats");
        
        ComplaintStatsDTO stats = tenantComplaintService.getMyComplaintStats(tenantId);
        return ResponseEntity.ok(stats);
    }

    /*
     * CHECK COMPLAINT STATUS - Quick status check for a complaint
     * URL - http://host:port/tenant/complaints/{tenantId}/check-status/{complaintId}
     * Method - GET 
     */
    @GetMapping("/{tenantId}/check-status/{complaintId}")
    @Operation(description = "Check complaint status (Tenant)")
    public ResponseEntity<?> checkComplaintStatus(@PathVariable @Min(1) Long tenantId,
            @PathVariable @Min(1) Long complaintId) {
        System.out.println("Tenant " + tenantId + ": checking status for complaint " + complaintId);
        
        try {
            // Get status information with security validation
            Map<String, Object> statusInfo = tenantComplaintService.checkMyComplaintStatus(tenantId, complaintId);
            return ResponseEntity.ok(statusInfo);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Complaint not found"));
        }
    }
}