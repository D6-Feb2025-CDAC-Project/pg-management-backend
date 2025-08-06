package com.easypg.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.easypg.entities.Complaint;

public interface ComplaintDao extends JpaRepository<Complaint, Long>{
	public List<Complaint> findByTenantId(Long id);
}
