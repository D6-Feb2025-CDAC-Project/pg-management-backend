package com.easypg.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.easypg.entities.NoticeBoard;
import com.easypg.enums.From;
import com.easypg.enums.PriorityLevel;

public interface NoticeBoardDao extends JpaRepository<NoticeBoard, Long> {
    
    // Get all non-deleted notices ordered by creation date (newest first)
    List<NoticeBoard> findByIsDeletedFalseOrderByCreatedAtDesc();
    
    // Get notice by ID if not deleted
    Optional<NoticeBoard> findByIdAndIsDeletedFalse(Long id);
    
    // Get notices by priority level (non-deleted only)
    List<NoticeBoard> findByPriorityLevelAndIsDeletedFalseOrderByCreatedAtDesc(PriorityLevel priorityLevel);
    
    // Get notices by source (non-deleted only)
    List<NoticeBoard> findByFromAndIsDeletedFalseOrderByCreatedAtDesc(From from);
    
    // Get notices by priority and source (non-deleted only)
    List<NoticeBoard> findByPriorityLevelAndFromAndIsDeletedFalseOrderByCreatedAtDesc(
            PriorityLevel priorityLevel, From from);
    
    // Check if notice exists with specific title (for uniqueness validation if needed)
    boolean existsByTitleAndIsDeletedFalse(String title);
}