package com.easypg.controller.admin;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.easypg.dto.ComplaintRespDTO;
import com.easypg.dto.ComplaintStatsDTO;
import com.easypg.dto.UpdateComplaintActionDTO;
import com.easypg.dto.UpdateComplaintPriorityDTO;
import com.easypg.dto.UpdateComplaintStatusDTO;
import com.easypg.service.admin.AdminComplaintService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/admin/complaints")
@CrossOrigin(origins = "http://localhost:5173")
@AllArgsConstructor
@Validated
@Tag(name = "Admin Complaint Management", description = "APIs for admin to manage all complaints")
public class AdminComplaintController {

    private final AdminComplaintService adminComplaintService;

    // Get all complaints
    @GetMapping
    @Operation(description = "Get all complaints (Admin only)")
    public ResponseEntity<?> getAllComplaints() {
        try {
            List<ComplaintRespDTO> complaints = adminComplaintService.getAllComplaints();
            if (complaints.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            return ResponseEntity.ok(complaints);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Get complaint by ID
    @GetMapping("/{complaintId}")
    @Operation(description = "Get complaint details by ID (Admin only)")
    public ResponseEntity<?> getComplaintDetails(@PathVariable @Min(1) Long complaintId) {
        try {
            ComplaintRespDTO complaint = adminComplaintService.getComplaintDetails(complaintId);
            return ResponseEntity.ok(complaint);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Complaint not found"));
        }
    }

    // Update complaint status
    @PutMapping("/{complaintId}/status")
    @Operation(description = "Update complaint status (Admin only)")
    public ResponseEntity<?> updateComplaintStatus(@PathVariable @Min(1) Long complaintId,
            @RequestBody @Valid UpdateComplaintStatusDTO dto) {
        try {
            ComplaintRespDTO updatedComplaint = adminComplaintService.updateComplaintStatus(complaintId, dto);
            return ResponseEntity.ok(updatedComplaint);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Update complaint priority
    @PutMapping("/{complaintId}/priority")
    @Operation(description = "Update complaint priority (Admin only)")
    public ResponseEntity<?> updateComplaintPriority(@PathVariable @Min(1) Long complaintId,
            @RequestBody @Valid UpdateComplaintPriorityDTO dto) {
        try {
            ComplaintRespDTO updatedComplaint = adminComplaintService.updateComplaintPriority(complaintId, dto);
            return ResponseEntity.ok(updatedComplaint);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Add/Update action taken
    @PutMapping("/{complaintId}/action")
    @Operation(description = "Add/Update action taken for complaint (Admin only)")
    public ResponseEntity<?> updateComplaintAction(@PathVariable @Min(1) Long complaintId,
            @RequestBody @Valid UpdateComplaintActionDTO dto) {
        try {
            ComplaintRespDTO updatedComplaint = adminComplaintService.updateComplaintAction(complaintId, dto);
            return ResponseEntity.ok(updatedComplaint);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Get complaints by status
    @GetMapping("/status/{status}")
    @Operation(description = "Get complaints by status (Admin only)")
    public ResponseEntity<?> getComplaintsByStatus(@PathVariable String status) {
        try {
            List<ComplaintRespDTO> complaints = adminComplaintService.getComplaintsByStatus(status);
            if (complaints.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            return ResponseEntity.ok(complaints);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Get complaints by priority
    @GetMapping("/priority/{priority}")
    @Operation(description = "Get complaints by priority level (Admin only)")
    public ResponseEntity<?> getComplaintsByPriority(@PathVariable String priority) {
        try {
            List<ComplaintRespDTO> complaints = adminComplaintService.getComplaintsByPriority(priority);
            if (complaints.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            return ResponseEntity.ok(complaints);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Get complaint statistics
    @GetMapping("/stats")
    @Operation(description = "Get complaint statistics (Admin only)")
    public ResponseEntity<?> getComplaintStats() {
        try {
            ComplaintStatsDTO stats = adminComplaintService.getComplaintStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Filter complaints with multiple criteria
    @GetMapping("/filter")
    @Operation(description = "Filter complaints with multiple criteria (Admin only)")
    public ResponseEntity<?> filterComplaints(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority) {
        try {
            List<ComplaintRespDTO> complaints = adminComplaintService.filterComplaints(status, priority);
            if (complaints.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            return ResponseEntity.ok(complaints);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Delete complaint (soft delete)
    @DeleteMapping("/{complaintId}")
    @Operation(description = "Soft delete complaint (Admin only)")
    public ResponseEntity<?> deleteComplaint(@PathVariable @Min(1) Long complaintId) {
        try {
            adminComplaintService.deleteComplaint(complaintId);
            return ResponseEntity.ok(Map.of("message", "Complaint deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}