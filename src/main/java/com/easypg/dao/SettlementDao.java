package com.easypg.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.easypg.entities.Settlement;
import com.easypg.enums.SettlementPaymentStatus;

public interface SettlementDao extends JpaRepository<Settlement, Long> {
    Optional<Settlement> findByLeaveNoticeId(Long leaveNoticeId);
    List<Settlement> findBySettlementStatus(SettlementPaymentStatus status);
    Optional<Settlement> findByIdAndIsDeletedFalse(Long id);
}
