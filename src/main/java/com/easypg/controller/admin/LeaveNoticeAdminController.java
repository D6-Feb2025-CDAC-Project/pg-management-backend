package com.easypg.controller.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.easypg.service.LeaveNoticeService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/admin/leave-notices")
public class LeaveNoticeAdminController {
	private final LeaveNoticeService leaveNoticeService;
	
	@GetMapping
	public ResponseEntity<?> getAllLeaveNotice(){
		return ResponseEntity.ok(leaveNoticeService.getLeaveNotices());
	}
}
