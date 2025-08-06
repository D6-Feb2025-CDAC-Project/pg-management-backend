package com.easypg.service;

import java.util.List;

import com.easypg.dto.ApiResponse;
import com.easypg.dto.LeaveNoticeResponseAdminDTO;
import com.easypg.dto.LeaveNoticeResponseTenantDTO;
import com.easypg.dto.LeaveNoticeSubmitDTO;

public interface LeaveNoticeService {
	ApiResponse submitLeaveNoticeForCurrentTenant(LeaveNoticeSubmitDTO submitLeaveNotice, Long tenantId);
	LeaveNoticeResponseTenantDTO getLeaveNoticeForCurrentTenant(Long tenantId);
	ApiResponse cancelLeaveNoticeForCurrentTenant(Long tenantId);
	
//	List<LeaveNoticeResponseAdminDTO> getLeaveNotices();
}
