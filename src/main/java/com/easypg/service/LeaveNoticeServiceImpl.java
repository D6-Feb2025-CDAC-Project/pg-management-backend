package com.easypg.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.easypg.dao.LeaveNoticeDAO;
import com.easypg.dao.TenantDao;
import com.easypg.dto.ApiResponse;
import com.easypg.dto.LeaveNoticeResponseAdminDTO;
import com.easypg.dto.LeaveNoticeResponseTenantDTO;
import com.easypg.dto.LeaveNoticeSubmitDTO;
import com.easypg.entities.LeaveNotice;
import com.easypg.entities.Tenant;
import com.easypg.enums.NoticeResponseStatus;
import com.easypg.custom_exceptions.ApiException;
import com.easypg.custom_exceptions.ResourceNotFoundException;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
@Transactional
public class LeaveNoticeServiceImpl implements LeaveNoticeService{
	
	private final LeaveNoticeDAO leaveNoticeDao;
	private final TenantDao tenantDao;
	private final ModelMapper modelMapper;

	@Override
	public ApiResponse submitLeaveNoticeForCurrentTenant(LeaveNoticeSubmitDTO submitLeaveNotice, Long tenantId) {
		LeaveNotice leaveNotice = modelMapper.map(submitLeaveNotice, LeaveNotice.class);		
		Tenant tenant =  tenantDao.findByIdAndIsDeletedFalse(tenantId)
				.orElseThrow(() -> new ResourceNotFoundException("Tenant with given id not found"));
		leaveNotice.setTenant(tenant);
		leaveNotice.setNoticeResponseStatus(NoticeResponseStatus.PENDING_REVIEW);
		leaveNoticeDao.save(leaveNotice);
		return new ApiResponse("Leave Request submitted successfully");
	}

	@Override
	public LeaveNoticeResponseTenantDTO getLeaveNoticeForCurrentTenant(Long tenantId) {
		LeaveNotice leaveNotice = leaveNoticeDao.findByTenantIdAndIsDeletedFalse(tenantId).orElseThrow(()-> new ResourceNotFoundException("Tenant with given id not found"));
		return modelMapper.map(leaveNotice, LeaveNoticeResponseTenantDTO.class);
	}

	@Override
	public ApiResponse cancelLeaveNoticeForCurrentTenant(Long tenantId) {
		Tenant tenant = tenantDao.findById(tenantId).orElseThrow(()-> new ResourceNotFoundException("Tenant with given id not found"));
		LeaveNotice leaveNotice = tenant.getLeaveNotice();
		if(leaveNotice != null) leaveNotice.setDeleted(true);
		else throw new ApiException("No Request to be cancelled");
		tenant.setLeaveNotice(null);
		return new ApiResponse("");
	}

	@Override
	public List<LeaveNoticeResponseAdminDTO> getLeaveNotices() {
		List<LeaveNotice> noticeLayer = leaveNoticeDao.findByIsDeletedFalse();
	    
	    return noticeLayer.stream()
	        .map(leaveNotice -> {
	            LeaveNoticeResponseAdminDTO dto = modelMapper.map(leaveNotice, LeaveNoticeResponseAdminDTO.class);
	            
	            // Set tenant-related fields
	            if (leaveNotice.getTenant() != null) {
	                dto.setTenantId(leaveNotice.getTenant().getId());
	                if (leaveNotice.getTenant().getUser() != null) {
	                    dto.setTenantName(leaveNotice.getTenant().getUser().getUsername()); // Assuming User has getName() method
	                }
	            }

	            dto.setSettlementGenerated(leaveNotice.getSettlement() != null);
	            
	            return dto;
	        })
	        .collect(Collectors.toList());
	}
	
}
