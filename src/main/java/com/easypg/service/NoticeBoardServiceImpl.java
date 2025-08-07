package com.easypg.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.easypg.custom_exceptions.ResourceNotFoundException;
import com.easypg.dao.NoticeBoardDao;
import com.easypg.dto.AddNoticeBoardDTO;
import com.easypg.dto.ApiResponse;
import com.easypg.dto.NoticeBoardRespDTO;

import com.easypg.entities.NoticeBoard;
import com.easypg.enums.From;
import com.easypg.enums.PriorityLevel;


import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class NoticeBoardServiceImpl implements NoticeBoardService {
    
    private final NoticeBoardDao noticeBoardDao;
    private final ModelMapper modelMapper;

    /**
     * Get current authenticated user name, fallback to "SYSTEM" if not authenticated
     */
    private String getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && 
                !"anonymousUser".equals(authentication.getPrincipal())) {
                return authentication.getName();
            }
        } catch (Exception e) {
            // Fallback in case of any security context issues
        }
        return "SYSTEM"; // Default fallback
    }

    @Override
    public List<NoticeBoardRespDTO> getAllNotices() {
        return noticeBoardDao.findByIsDeletedFalseOrderByCreatedAtDesc()
                .stream()
                .map(notice -> modelMapper.map(notice, NoticeBoardRespDTO.class))
                .toList();
    }

    @Override
    public ApiResponse addNewNotice(AddNoticeBoardDTO dto) {
        // Map dto -> entity
        NoticeBoard entity = modelMapper.map(dto, NoticeBoard.class);
        
        // Set audit fields
        entity.setDeleted(false);
        String currentUser = getCurrentUser();
        entity.setCreatedBy(currentUser);
        entity.setUpdatedBy(currentUser);
        
        // Save the notice
        NoticeBoard persistentNotice = noticeBoardDao.save(entity);
        return new ApiResponse("Added new notice with ID=" + persistentNotice.getId());
    }

    @Override
    public ApiResponse deleteNotice(Long noticeId) {
        // Get notice from its id
        NoticeBoard notice = noticeBoardDao.findById(noticeId)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid notice id: " + noticeId));
        
        // Check if already deleted
        if (notice.isDeleted()) {
            throw new ResourceNotFoundException("Notice with ID " + noticeId + " is already deleted");
        }
        
        // Soft delete - mark as deleted
        notice.setDeleted(true);
        notice.setUpdatedBy(getCurrentUser());
        
        return new ApiResponse("Soft deleted notice with ID=" + noticeId);
    }

    @Override
    public NoticeBoardRespDTO getNoticeDetails(Long noticeId) {
        NoticeBoard entity = noticeBoardDao.findByIdAndIsDeletedFalse(noticeId)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid Notice ID: " + noticeId));
        
        return modelMapper.map(entity, NoticeBoardRespDTO.class);
    }

    @Override
    public ApiResponse updateNoticeDetails(Long noticeId, AddNoticeBoardDTO dto) {
        // Get notice from its id
        NoticeBoard entity = noticeBoardDao.findByIdAndIsDeletedFalse(noticeId)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid Notice ID: Update failed"));
        
        // Store original values for comparison
        String originalTitle = entity.getTitle();
        String originalMessage = entity.getMessage();
        From originalFrom = entity.getFrom();
        PriorityLevel originalPriority = entity.getPriorityLevel();
        
        // Update fields only if they are provided and different
        if (dto.getTitle() != null && !dto.getTitle().equals(originalTitle)) {
            entity.setTitle(dto.getTitle());
        }
        if (dto.getMessage() != null && !dto.getMessage().equals(originalMessage)) {
            entity.setMessage(dto.getMessage());
        }
        if (dto.getFrom() != null && !dto.getFrom().equals(originalFrom)) {
            entity.setFrom(dto.getFrom());
        }
        if (dto.getPriorityLevel() != null && !dto.getPriorityLevel().equals(originalPriority)) {
            entity.setPriorityLevel(dto.getPriorityLevel());
        }
        
        // Always update the updatedBy field
        entity.setUpdatedBy(getCurrentUser());
        
        return new ApiResponse("Notice details updated for ID=" + noticeId);
    }

 

 
  
}