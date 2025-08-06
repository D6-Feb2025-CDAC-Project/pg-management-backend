package com.easypg.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.easypg.enums.SettlementPaymentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name="settlements")
@ToString(callSuper = true, exclude = {"leaveNotice", "originalPayment"})
public class Settlement extends BaseEntity{
	
	@OneToOne(mappedBy = "settlement")
	private LeaveNotice leaveNotice;
	
	@OneToOne
	@JoinColumn(name="payment_id")
	private Payment originalPayment;
	
	@Column(name="settlement_amount", nullable=false)
	private BigDecimal settlementAmount;
	
	@Column(name="deduction_amount", nullable=false)
	private BigDecimal deductionAmount;
	
	@Column(name="settlement_status", nullable=false)
	@Enumerated(EnumType.STRING)
	private SettlementPaymentStatus settlementStatus;
	
	@Column(name="settlement_processedDate")
	private LocalDate settlementProcessedDate;
	
	@Column(name="razorpay_refund_id")
	private String razorpayRefundId;
	
	@Column(name="additional_comments")
	private String additionalComments;
	
}
