// NoticeBoardDao.java
package com.easypg.dao;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import com.easypg.entities.NoticeBoard;

@Repository
public interface NoticeBoardDao extends JpaRepository<NoticeBoard, Long> {
    
    // Find all non-deleted notices ordered by creation date (newest first)
    List<NoticeBoard> findByIsDeletedFalseOrderByCreatedAtDesc();
    
    // Find non-deleted notice by ID
    Optional<NoticeBoard> findByIdAndIsDeletedFalse(Long id);
   
    // Find notices by title containing keyword (case-insensitive, non-deleted only)
    List<NoticeBoard> findByTitleContainingIgnoreCaseAndIsDeletedFalseOrderByCreatedAtDesc(String keyword);
    
 
  
   
    
}