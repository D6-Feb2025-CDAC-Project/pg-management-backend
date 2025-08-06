package com.easypg.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easypg.custom_exceptions.ApiException;
import com.easypg.custom_exceptions.InvalidInputException;
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
        entity.setDeleted(false); // Ensure it's not marked as deleted
        
        // Save the notice
        NoticeBoard persistentNotice = noticeBoardDao.save(entity);
        return new ApiResponse("Added new notice with ID=" + persistentNotice.getId());
    }

    @Override
    public ApiResponse deleteNotice(Long noticeId) {
        // Get notice from its id
        NoticeBoard notice = noticeBoardDao.findById(noticeId)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid notice id: " + noticeId));
        
        // Soft delete - mark as deleted
        notice.setDeleted(true);
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
        
        // Map dto -> entity (only non-null fields will be transferred)
        modelMapper.map(dto, entity); // modifying state of the persistent entity
        
        return new ApiResponse("Notice details updated for ID=" + noticeId);
    }

    @Override
    public List<NoticeBoardRespDTO> getNoticesByPriority(String priorityLevel) {
        try {
            PriorityLevel priority = PriorityLevel.valueOf(priorityLevel.toUpperCase());
            return noticeBoardDao.findByPriorityLevelAndIsDeletedFalseOrderByCreatedAtDesc(priority)
                    .stream()
                    .map(notice -> modelMapper.map(notice, NoticeBoardRespDTO.class))
                    .toList();
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException("Invalid priority level: " + priorityLevel);
        }
    }

    @Override
    public List<NoticeBoardRespDTO> getNoticesByFrom(String from) {
        try {
            From noticeFrom = From.valueOf(from.toUpperCase());
            return noticeBoardDao.findByFromAndIsDeletedFalseOrderByCreatedAtDesc(noticeFrom)
                    .stream()
                    .map(notice -> modelMapper.map(notice, NoticeBoardRespDTO.class))
                    .toList();
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException("Invalid notice source: " + from);
        }
    }
}