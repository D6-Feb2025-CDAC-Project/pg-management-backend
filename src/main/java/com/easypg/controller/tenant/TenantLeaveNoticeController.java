package com.easypg.controller.tenant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.easypg.dto.LeaveNoticeSubmitDTO;
import com.easypg.entities.BaseUser;
import com.easypg.service.LeaveNoticeService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/tenant/leave-notices")
public class TenantLeaveNoticeController {
	
	private final LeaveNoticeService leaveNoticeService;
	
	@PostMapping
	public ResponseEntity<?> submitLeaveNotice(@RequestBody LeaveNoticeSubmitDTO leaveNoticeSubmitDto, @AuthenticationPrincipal BaseUser userDetails) {
		Long tenantId = userDetails.getId();// Replace with tenant ID from JWT in production
		return ResponseEntity.status(HttpStatus.CREATED).body(leaveNoticeService.submitLeaveNoticeForCurrentTenant(leaveNoticeSubmitDto, tenantId));
	}
	
	@GetMapping
	public ResponseEntity<?> getMyLeaveNotice(@AuthenticationPrincipal BaseUser userDetails) {
		Long tenantId = userDetails.getId();
		return ResponseEntity.ok(leaveNoticeService.getLeaveNoticeForCurrentTenant(tenantId));
	}
	
	@DeleteMapping
	public ResponseEntity<Void> cancelLeaveNotice(@AuthenticationPrincipal BaseUser userDetails){
		Long tenantId = userDetails.getId();
		leaveNoticeService.cancelLeaveNoticeForCurrentTenant(tenantId);
		return ResponseEntity.noContent().build();
	}
	
}
