package com.easypg.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.easypg.enums.NoticeResponseStatus;
import com.easypg.enums.SettlementPaymentStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeaveNoticeResponseAdminDTO {
    private Long id;
    private LocalDate moveOutDate;
    private String reasonOfLeave;
    private String additionalTenantNotes;
    private NoticeResponseStatus noticeResponseStatus;
    private String reviewNotes;
    
    // Tenant Information
    private Long tenantId;
    private String tenantName;
    private String tenantEmail;
    private String contactNumber;
    private String roomNumber;
    
    // Additional fields to match frontend
    private String referenceId; // Generated reference ID for the notice
    private LocalDateTime createdDate; // Submission date from BaseEntity
    private LocalDateTime updatedDate; // Last update from BaseEntity
    
    // Settlement Information
    private boolean settlementGenerated;
    private SettlementDetailsDTO settlementDetails;
    
    // Room and deposit information
    private Double securityDeposit; // From room/payment records
    
    @Getter
    @Setter
    public static class SettlementDetailsDTO {
        private Long settlementId;
        private BigDecimal settlementAmount;
        private BigDecimal deductionAmount;
        private String deductionReason; // additionalComments from Settlement
        private SettlementPaymentStatus settlementStatus;
        private LocalDate settlementProcessedDate;
        private String razorpayRefundId;
    }
}