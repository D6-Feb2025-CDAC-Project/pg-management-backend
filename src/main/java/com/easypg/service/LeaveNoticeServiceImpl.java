package com.easypg.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.easypg.dao.LeaveNoticeDAO;
import com.easypg.dto.ApiResponse;
import com.easypg.dto.LeaveNoticeResponseAdminDTO;
import com.easypg.dto.LeaveNoticeResponseTenantDTO;
import com.easypg.dto.LeaveNoticeSubmitDTO;
import com.easypg.entities.LeaveNotice;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
@Transactional
public class LeaveNoticeServiceImpl implements LeaveNoticeService{
	
	private final LeaveNoticeDAO leaveNoticeDao;
	private final ModelMapper modelMapper;

	@Override
	public ApiResponse submitLeaveNoticeForCurrentTenant(LeaveNoticeSubmitDTO submitLeaveNotice, Long tenantId) {
		LeaveNotice leaveNotice = modelMapper.map(submitLeaveNotice, LeaveNotice.class);		
		leaveNoticeDao.save(leaveNotice);
		return null;
	}

	@Override
	public LeaveNoticeResponseTenantDTO getLeaveNoticeForCurrentTenant(Long tenantId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ApiResponse cancelLeaveNoticeForCurrentTenant(Long tenantId) {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
//	public List<LeaveNoticeResponseAdminDTO> getLeaveNotices() {
//		// TODO Auto-generated method stub
//		return null;
//	}
	
	
}
