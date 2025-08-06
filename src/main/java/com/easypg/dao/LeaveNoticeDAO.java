package com.easypg.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.easypg.entities.LeaveNotice;

public interface LeaveNoticeDAO extends JpaRepository<LeaveNotice, Long>{

}
