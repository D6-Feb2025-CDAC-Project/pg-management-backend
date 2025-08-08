package com.easypg.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.easypg.dao.LeaveNoticeDao;
import com.easypg.dao.PaymentDao;
import com.easypg.dao.SettlementDao;
import com.easypg.dao.TenantDao;
import com.easypg.dto.ApiResponse;
import com.easypg.dto.DepositSettlementDTO;
import com.easypg.dto.LeaveNoticeResponseAdminDTO;
import com.easypg.dto.LeaveNoticeResponseTenantDTO;
import com.easypg.dto.LeaveNoticeSubmitDTO;
import com.easypg.dto.UpdateNoticeStatusDTO;
import com.easypg.dto.UpdateReviewNotesDTO;
import com.easypg.entities.LeaveNotice;
import com.easypg.entities.Payment;
import com.easypg.entities.Settlement;
import com.easypg.entities.Tenant;
import com.easypg.enums.NoticeResponseStatus;
import com.easypg.enums.PaymentStatus;
import com.easypg.enums.PaymentType;
import com.easypg.enums.SettlementPaymentStatus;
import com.easypg.custom_exceptions.ApiException;
import com.easypg.custom_exceptions.ResourceNotFoundException;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
@Transactional
public class LeaveNoticeServiceImpl implements LeaveNoticeService{
	
	private final LeaveNoticeDao leaveNoticeDao;
	private final TenantDao tenantDao;
	private final SettlementDao settlementDAO;
    private final PaymentDao paymentDAO; 
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
		//Tenant tenant = tenantDao.findById(tenantId).orElseThrow(()-> new ResourceNotFoundException("Tenant with given id not found"));
		LeaveNotice leaveNotice = leaveNoticeDao.findByTenantIdAndIsDeletedFalse(tenantId).orElseThrow(()-> new ApiException("No Request to be cancelled"));
		leaveNotice.setDeleted(true);
		return new ApiResponse("Leave Notice Cancelled successfully");
	}

	@Override
	public List<LeaveNoticeResponseAdminDTO> getLeaveNotices() {
		List<LeaveNotice> notices = leaveNoticeDao.findByIsDeletedFalseOrderByCreatedAtDesc();
	    
		return notices.stream()
                .map(this::mapToAdminDTO)
                .collect(Collectors.toList());
	}

	@Override
    public LeaveNoticeResponseAdminDTO getLeaveNoticeById(Long noticeId) {
        LeaveNotice leaveNotice = leaveNoticeDao.findByIdAndIsDeletedFalse(noticeId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave notice not found"));
        return mapToAdminDTO(leaveNotice);
    }

	@Override
	public ApiResponse updateNoticeStatus(Long noticeId, UpdateNoticeStatusDTO statusUpdateDTO) {
		LeaveNotice leaveNotice = leaveNoticeDao.findByIdAndIsDeletedFalse(noticeId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave notice not found"));

        // Validate status transition
        validateStatusTransition(leaveNotice.getNoticeResponseStatus(), statusUpdateDTO.getNewStatus());

        // Update status and notes
        leaveNotice.setNoticeResponseStatus(statusUpdateDTO.getNewStatus());
        
        if (statusUpdateDTO.getReviewNotes() != null) {
            leaveNotice.setReviewNotes(statusUpdateDTO.getReviewNotes());
        }

        // ONLY create settlement when status becomes APPROVED
        if (statusUpdateDTO.getNewStatus() == NoticeResponseStatus.APPROVED) {
            createSettlementForNotice(leaveNotice);
        }

        leaveNoticeDao.save(leaveNotice);
        
        String message = getStatusUpdateMessage(statusUpdateDTO.getNewStatus());
        return new ApiResponse(message);
    }

	@Override
    public ApiResponse updateReviewNotes(Long noticeId, UpdateReviewNotesDTO reviewNotesDTO) {
        LeaveNotice leaveNotice = leaveNoticeDao.findByIdAndIsDeletedFalse(noticeId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave notice not found"));

        leaveNotice.setReviewNotes(reviewNotesDTO.getReviewNotes());
        leaveNotice.setNoticeResponseStatus(NoticeResponseStatus.UNDER_REVIEW);
        
        leaveNoticeDao.save(leaveNotice);
        return new ApiResponse("Review notes updated successfully");
    }
	
	@Override
    public ApiResponse processDepositSettlement(Long noticeId, DepositSettlementDTO settlementDTO) {
		LeaveNotice leaveNotice = leaveNoticeDao.findByIdAndIsDeletedFalse(noticeId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave notice not found"));

        // Validate that notice is approved and settlement exists
        if (leaveNotice.getNoticeResponseStatus() != NoticeResponseStatus.APPROVED) {
            throw new ApiException("Can only process settlement for approved notices");
        }

        Settlement settlement = leaveNotice.getSettlement();
        if (settlement == null) {
            throw new ApiException("No settlement found for this notice. Settlement should be created when notice is approved.");
        }

        if (settlement.getSettlementStatus() != SettlementPaymentStatus.NOT_INITIATED) {
            throw new ApiException("Settlement has already been processed or is in progress");
        }

        // Get security deposit from original payment
        BigDecimal securityDeposit = getSecurityDepositForTenant(leaveNotice.getTenant());
        
        // Validate deduction amount
        if (settlementDTO.getDeductionAmount().compareTo(securityDeposit) > 0) {
            throw new ApiException("Deduction amount cannot exceed security deposit");
        }

        // Update settlement details
        settlement.setDeductionAmount(settlementDTO.getDeductionAmount());
        settlement.setSettlementAmount(securityDeposit.subtract(settlementDTO.getDeductionAmount()));
        settlement.setAdditionalComments(settlementDTO.getDeductionReason());
        settlement.setSettlementStatus(SettlementPaymentStatus.PENDING);

        // Update leave notice status to PAYMENT_PROCESSING
        leaveNotice.setNoticeResponseStatus(NoticeResponseStatus.PAYMENT_PROCESSING);

        settlementDAO.save(settlement);
        leaveNoticeDao.save(leaveNotice);

        return new ApiResponse("Deposit settlement initiated successfully. Processing refund...");
    }

	private String getStatusUpdateMessage(NoticeResponseStatus status) {
        switch (status) {
            case UNDER_REVIEW:
                return "Leave notice moved to under review";
            case APPROVED:
                return "Leave notice approved and settlement created";
            case REJECTED:
                return "Leave notice rejected";
            case PAYMENT_PROCESSING:
                return "Settlement processing initiated";
            case COMPLETED:
                return "Leave notice process completed";
            default:
                return "Leave notice status updated";
        }
    }

	
	private LeaveNoticeResponseAdminDTO mapToAdminDTO(LeaveNotice leaveNotice) {
		LeaveNoticeResponseAdminDTO dto = modelMapper.map(leaveNotice, LeaveNoticeResponseAdminDTO.class);

        // Set tenant information
        if (leaveNotice.getTenant() != null) {
            Tenant tenant = leaveNotice.getTenant();
            dto.setTenantId(tenant.getId());
            dto.setContactNumber(tenant.getContactNumber());
            
            if (tenant.getUser() != null) {
                dto.setTenantName(tenant.getUser().getUsername());
                dto.setTenantEmail(tenant.getUser().getEmail());
            }
            
            if (tenant.getRoom() != null) {
                dto.setRoomNumber(tenant.getRoom().getRoomNo());
                dto.setSecurityDeposit(tenant.getRoom().getDeposit());
            }
        }

        // Generate reference ID
        dto.setReferenceId("LN" + String.format("%03d", leaveNotice.getId()));

        // Set settlement information
        Settlement settlement = leaveNotice.getSettlement();
        dto.setSettlementGenerated(settlement != null);
        
        if (settlement != null) {
            LeaveNoticeResponseAdminDTO.SettlementDetailsDTO settlementDetails = 
                new LeaveNoticeResponseAdminDTO.SettlementDetailsDTO();
            settlementDetails.setSettlementId(settlement.getId());
            settlementDetails.setSettlementAmount(settlement.getSettlementAmount());
            settlementDetails.setDeductionAmount(settlement.getDeductionAmount());
            settlementDetails.setDeductionReason(settlement.getAdditionalComments());
            settlementDetails.setSettlementStatus(settlement.getSettlementStatus());
            settlementDetails.setSettlementProcessedDate(settlement.getSettlementProcessedDate());
            settlementDetails.setRazorpayRefundId(settlement.getRazorpayRefundId());
            
            dto.setSettlementDetails(settlementDetails);
        }

        return dto;
    }
	
	private void validateStatusTransition(NoticeResponseStatus currentStatus, NoticeResponseStatus newStatus) {
		switch (currentStatus) {
	        case PENDING_REVIEW:
	            if (newStatus != NoticeResponseStatus.UNDER_REVIEW) {
	                throw new ApiException("Can only move from PENDING_REVIEW to UNDER_REVIEW");
	            }
	            break;
	        case UNDER_REVIEW:
	            if (newStatus != NoticeResponseStatus.APPROVED && newStatus != NoticeResponseStatus.REJECTED) {
	                throw new ApiException("Can only move from UNDER_REVIEW to APPROVED or REJECTED");
	            }
	            break;
	        case APPROVED:
	            if (newStatus != NoticeResponseStatus.PAYMENT_PROCESSING) {
	                throw new ApiException("Can only move from APPROVED to PAYMENT_PROCESSING through settlement processing");
	            }
	            break;
	        case PAYMENT_PROCESSING:
	            if (newStatus != NoticeResponseStatus.COMPLETED) {
	                throw new ApiException("Can only move from PAYMENT_PROCESSING to COMPLETED");
	            }
	            break;
	        case REJECTED:
	        case COMPLETED:
	            throw new ApiException("Cannot change status from " + currentStatus);
	        default:
	            throw new ApiException("Invalid status transition from " + currentStatus + " to " + newStatus);
		}
    }
	
	private void createSettlementForNotice(LeaveNotice leaveNotice) {
		// Check if settlement already exists
//        if (leaveNotice.getSettlement() != null) {
//            log.warn("Settlement already exists for leave notice ID: {}", leaveNotice.getId());
//            return;
//        }

        try {
            // Get the original security deposit payment
            Payment originalPayment = getOriginalSecurityDepositPayment(leaveNotice.getTenant());

            Settlement settlement = new Settlement();
            settlement.setLeaveNotice(leaveNotice);
            settlement.setOriginalPayment(originalPayment);
            settlement.setSettlementAmount(BigDecimal.ZERO); // Will be calculated when processing
            settlement.setDeductionAmount(BigDecimal.ZERO);
            settlement.setSettlementStatus(SettlementPaymentStatus.NOT_INITIATED);
            settlement.setAdditionalComments("Settlement created upon leave notice approval");

            settlement = settlementDAO.save(settlement);
            leaveNotice.setSettlement(settlement);
            
        } catch (Exception e) {
            //log.error("Failed to create settlement for leave notice ID: {}", leaveNotice.getId(), e);
            throw new ApiException("Failed to create settlement: " + e.getMessage());
        }
    }
	
	private Payment getOriginalSecurityDepositPayment(Tenant tenant) {
		return paymentDAO.findByTenantAndPaymentTypeAndPaymentStatus(
                tenant, 
                PaymentType.SECURITY_DEPOSIT, 
                PaymentStatus.SUCCESS
        ).orElseThrow(() -> new ResourceNotFoundException(
                "Original security deposit payment not found for tenant ID: " + tenant.getId()));
    }
	
	private BigDecimal getSecurityDepositForTenant(Tenant tenant) {
        Payment securityDepositPayment = getOriginalSecurityDepositPayment(tenant);
        return securityDepositPayment.getPaymentAmount();
    }
	
	@Override
    public boolean canProcessSettlement(Long noticeId) {
        try {
            LeaveNotice leaveNotice = leaveNoticeDao.findByIdAndIsDeletedFalse(noticeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Leave notice not found"));
            
            return leaveNotice.getNoticeResponseStatus() == NoticeResponseStatus.APPROVED
                    && leaveNotice.getSettlement() != null
                    && leaveNotice.getSettlement().getSettlementStatus() == SettlementPaymentStatus.NOT_INITIATED;
        } catch (Exception e) {
            return false;
        }
    }
}
