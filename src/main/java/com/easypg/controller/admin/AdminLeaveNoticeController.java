package com.easypg.controller.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.easypg.dto.ApiResponse;
import com.easypg.dto.DepositSettlementDTO;
import com.easypg.dto.LeaveNoticeResponseAdminDTO;
import com.easypg.dto.UpdateNoticeStatusDTO;
import com.easypg.dto.UpdateReviewNotesDTO;
import com.easypg.service.LeaveNoticeService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/admin/leave-notices")

public class AdminLeaveNoticeController {
    
    private final LeaveNoticeService leaveNoticeService;

    @GetMapping
    public ResponseEntity<List<LeaveNoticeResponseAdminDTO>> getAllLeaveNotices() {
        List<LeaveNoticeResponseAdminDTO> notices = leaveNoticeService.getLeaveNotices();
        return ResponseEntity.ok(notices);
    }

    @PutMapping("/{noticeId}/status")
    public ResponseEntity<ApiResponse> updateNoticeStatus(
            @PathVariable Long noticeId, 
            @RequestBody UpdateNoticeStatusDTO statusUpdateDTO) {
        ApiResponse response = leaveNoticeService.updateNoticeStatus(noticeId, statusUpdateDTO);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{noticeId}/review-notes")
    public ResponseEntity<ApiResponse> updateReviewNotes(
            @PathVariable Long noticeId,
            @RequestBody UpdateReviewNotesDTO reviewNotesDTO) {
        ApiResponse response = leaveNoticeService.updateReviewNotes(noticeId, reviewNotesDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{noticeId}/settlement")
    public ResponseEntity<ApiResponse> processDepositSettlement(
            @PathVariable Long noticeId,
            @RequestBody DepositSettlementDTO settlementDTO) {
        ApiResponse response = leaveNoticeService.processDepositSettlement(noticeId, settlementDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{noticeId}")
    public ResponseEntity<LeaveNoticeResponseAdminDTO> getNoticeById(@PathVariable Long noticeId) {
        LeaveNoticeResponseAdminDTO notice = leaveNoticeService.getLeaveNoticeById(noticeId);
        return ResponseEntity.ok(notice);
    }
    
    @GetMapping("/{noticeId}/settlement/eligibility")
    public ResponseEntity<Map<String, Object>> checkSettlementEligibility(@PathVariable Long noticeId) {
        boolean canProcess = leaveNoticeService.canProcessSettlement(noticeId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("canProcessSettlement", canProcess);
        response.put("message", canProcess ? 
            "Settlement can be processed" : 
            "Settlement cannot be processed - check notice status and settlement state");
        
        return ResponseEntity.ok(response);
    }
    
    
}