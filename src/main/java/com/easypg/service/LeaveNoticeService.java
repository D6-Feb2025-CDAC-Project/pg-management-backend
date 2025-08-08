package com.easypg.service;

import java.util.List;

import com.easypg.dto.ApiResponse;
import com.easypg.dto.DepositSettlementDTO;
import com.easypg.dto.LeaveNoticeResponseAdminDTO;
import com.easypg.dto.LeaveNoticeResponseTenantDTO;
import com.easypg.dto.LeaveNoticeSubmitDTO;
import com.easypg.dto.UpdateNoticeStatusDTO;
import com.easypg.dto.UpdateReviewNotesDTO;

public interface LeaveNoticeService {
	//tenant controller methods
	ApiResponse submitLeaveNoticeForCurrentTenant(LeaveNoticeSubmitDTO submitLeaveNotice, Long tenantId);
	LeaveNoticeResponseTenantDTO getLeaveNoticeForCurrentTenant(Long tenantId);
	ApiResponse cancelLeaveNoticeForCurrentTenant(Long tenantId);
	
	//admin controller methods
	List<LeaveNoticeResponseAdminDTO> getLeaveNotices();
	LeaveNoticeResponseAdminDTO getLeaveNoticeById(Long noticeId);
    ApiResponse updateNoticeStatus(Long noticeId, UpdateNoticeStatusDTO statusUpdateDTO);
    ApiResponse updateReviewNotes(Long noticeId, UpdateReviewNotesDTO reviewNotesDTO);
    ApiResponse processDepositSettlement(Long noticeId, DepositSettlementDTO settlementDTO);

    // 
    boolean canProcessSettlement(Long noticeId);
}
