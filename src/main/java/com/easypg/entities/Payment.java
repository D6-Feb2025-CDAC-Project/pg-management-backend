package com.easypg.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.easypg.enums.PaymentMethod;
import com.easypg.enums.PaymentStatus;
import com.easypg.enums.PaymentType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true, exclude = "tenant")
@Entity
@Table(name="payments")
public class Payment extends BaseEntity{

	@ManyToOne
	@JoinColumn(name="tenant_id", nullable=false)
	private Tenant tenant;
	
	@Column(name="payment_amount", nullable=false)
	private BigDecimal paymentAmount;
	
	@Column(name="payment_type", nullable=false)
	@Enumerated(EnumType.STRING)
	private PaymentType paymentType;
	
	@Column(name="payment_method", nullable=false)
	@Enumerated(EnumType.STRING)
	private PaymentMethod paymentMethod;
	
	@Column(name="payment_status", nullable=false)
	@Enumerated(EnumType.STRING)
	private PaymentStatus paymentStatus;
	
	@Column(name="payment_date", nullable=false)
	private LocalDateTime paymentDate;
	
	@Column(name="additional_comments", length=500)
	private String additionalComments;
	
	@Column(name="razorpay_paymentid")
	private String razorpayPaymentId;
	
	@Column(name="razorpay_orderid")
	private String razorpayOrderId;
	
	@Column(name="razorpay_signature")
	private String razorpaySignature;

}
