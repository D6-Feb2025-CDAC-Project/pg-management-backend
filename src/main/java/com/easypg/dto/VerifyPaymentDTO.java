package com.easypg.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class VerifyPaymentDTO {
	private String orderId;
	private String paymentId;
	private String signature;
	private BigDecimal amount;
}
