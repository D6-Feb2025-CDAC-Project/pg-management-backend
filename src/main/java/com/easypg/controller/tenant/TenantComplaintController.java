package com.easypg.controller.tenant;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.easypg.dto.AddComplaintDTO;
import com.easypg.dto.ComplaintRespDTO;
import com.easypg.entities.BaseUser;
import com.easypg.service.tenant.TenantComplaintService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/tenant/complaints")
@CrossOrigin(origins = "http://localhost:5173")
@AllArgsConstructor
@Validated
@Tag(name = "Tenant Complaint Management", description = "APIs for tenants to manage their own complaints")
public class TenantComplaintController {

    private final TenantComplaintService tenantComplaintService;

    // Submit a new complaint
    @PostMapping
    @Operation(description = "Submit new complaint")
    public ResponseEntity<?> submitMyComplaint(@RequestBody @Valid AddComplaintDTO dto, @AuthenticationPrincipal BaseUser userDetails) {
        try {
            Long tenantId = userDetails.getId(); // Replace with tenant ID from JWT in production
            ComplaintRespDTO createdComplaint = tenantComplaintService.addNewComplaint(tenantId, dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdComplaint);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Get all complaints of the tenant
    @GetMapping("/my-complaints")
    @Operation(description = "Get all complaints of the authenticated tenant")
    public ResponseEntity<?> getMyComplaintsAuth(@AuthenticationPrincipal BaseUser userDetails) {
        try {
        	System.out.println("user details : "+userDetails);
        	System.out.println("before");
        	System.out.println("HI"+ userDetails.getId());
        	System.out.println("after");
            Long tenantId = userDetails.getId();
           
            System.out.println("tenant id : "+tenantId);
            List<ComplaintRespDTO> complaints = tenantComplaintService.getMyComplaints(tenantId);
            if (complaints.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            return ResponseEntity.ok(complaints);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Update a complaint
    @PutMapping("/{complaintId}")
    @Operation(description = "Update a tenant complaint")
    public ResponseEntity<?> updateMyComplaint(
            @PathVariable @Min(1) @Max(10000) Long complaintId,
            @RequestBody @Valid AddComplaintDTO dto, @AuthenticationPrincipal BaseUser userDetails) {
        try {
            Long tenantId = userDetails.getId();
            ComplaintRespDTO updatedComplaint = tenantComplaintService.updateMyComplaint(tenantId, complaintId, dto);
            return ResponseEntity.ok(updatedComplaint);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Delete a complaint
    @DeleteMapping("/{complaintId}")
    @Operation(description = "Delete a tenant complaint")
    public ResponseEntity<?> deleteMyComplaint(@PathVariable @Min(1) @Max(10000) Long complaintId, @AuthenticationPrincipal BaseUser userDetails) {
        try {
            Long tenantId = userDetails.getId();
            tenantComplaintService.deleteMyComplaint(tenantId, complaintId);
            return ResponseEntity.ok(Map.of("message", "Complaint deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Get details of a specific complaint
    @GetMapping("/{complaintId}")
    @Operation(description = "Get details of a specific complaint")
    public ResponseEntity<?> getMyComplaintDetails(@PathVariable @Min(1) @Max(10000) Long complaintId, @AuthenticationPrincipal BaseUser userDetails) {
        try {
            Long tenantId = userDetails.getId();
            ComplaintRespDTO complaint = tenantComplaintService.getMyComplaintDetails(tenantId, complaintId);
            return ResponseEntity.ok(complaint);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Complaint not found"));
        }
    }
}