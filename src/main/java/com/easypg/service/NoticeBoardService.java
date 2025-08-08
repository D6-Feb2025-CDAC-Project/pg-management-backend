package com.easypg.service;

import java.util.List;

import com.easypg.dto.AddNoticeBoardDTO;
import com.easypg.dto.ApiResponse;
import com.easypg.dto.NoticeBoardRespDTO;


public interface NoticeBoardService {
    
    // Get all active notices
     
    List<NoticeBoardRespDTO> getAllNotices();
    
    /**
     * Add a new notice
     */
    ApiResponse addNewNotice(AddNoticeBoardDTO dto);
    
    /**
     * Soft delete a notice
     */
    ApiResponse deleteNotice(Long noticeId);
    
    /**
     * Get notice details by ID
     */
    NoticeBoardRespDTO getNoticeDetails(Long noticeId);
    
    /**
     * Update notice details
     */
    ApiResponse updateNoticeDetails(Long noticeId, AddNoticeBoardDTO dto);
    
   
  
    
}