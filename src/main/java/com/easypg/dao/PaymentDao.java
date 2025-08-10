package com.easypg.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.easypg.entities.Payment;
import com.easypg.entities.Tenant;
import com.easypg.enums.PaymentStatus;
import com.easypg.enums.PaymentType;

public interface PaymentDao extends JpaRepository<Payment, Long> {
    Optional<Payment> findByTenantAndPaymentType(Tenant tenant, PaymentType paymentType);
    Optional<Payment> findByTenantAndPaymentTypeAndPaymentStatus(Tenant tenant, PaymentType paymentType, PaymentStatus status);
    Optional<Payment> findByRazorpayPaymentId(String razorpayPaymentId);
    
    Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);
    boolean existsByRazorpayPaymentId(String razorpayPaymentId);
    List<Payment> findByTenantAndIsDeletedFalse(Tenant tenant);

}
