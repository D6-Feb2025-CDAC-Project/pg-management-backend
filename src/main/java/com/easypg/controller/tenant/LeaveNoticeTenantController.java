package com.easypg.controller.tenant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.easypg.dto.LeaveNoticeSubmitDTO;
import com.easypg.service.LeaveNoticeService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/tenant/leave-notices")
@CrossOrigin(origins = "http://localhost:5173")
public class LeaveNoticeTenantController {
	
	private final LeaveNoticeService leaveNoticeService;
	
	@PostMapping("/{tenantId}")
	public ResponseEntity<?> submitLeaveNotice(@RequestBody LeaveNoticeSubmitDTO leaveNoticeSubmitDto, @PathVariable Long tenantId) {
		return ResponseEntity.status(HttpStatus.CREATED).body(leaveNoticeService.submitLeaveNoticeForCurrentTenant(leaveNoticeSubmitDto, tenantId));
	}
	
	@GetMapping("/{tenantId}")
	public ResponseEntity<?> getMyLeaveNotice(@PathVariable Long tenantId) {
		return ResponseEntity.ok(leaveNoticeService.getLeaveNoticeForCurrentTenant(tenantId));
	}
	
	@DeleteMapping("/{tenantId}")
	public ResponseEntity<Void> cancelLeaveNotice(@PathVariable Long tenantId){
		leaveNoticeService.cancelLeaveNoticeForCurrentTenant(tenantId);
		return ResponseEntity.noContent().build();
	}
	
}
