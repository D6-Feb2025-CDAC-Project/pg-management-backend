package com.easypg.service;

import java.util.List;

import com.easypg.dto.AddNoticeBoardDTO;
import com.easypg.dto.ApiResponse;
import com.easypg.dto.NoticeBoardRespDTO;

public interface NoticeBoardService {
    List<NoticeBoardRespDTO> getAllNotices();
    ApiResponse addNewNotice(AddNoticeBoardDTO dto);
    ApiResponse deleteNotice(Long noticeId);
    NoticeBoardRespDTO getNoticeDetails(Long noticeId);
    ApiResponse updateNoticeDetails(Long noticeId, AddNoticeBoardDTO dto);
    List<NoticeBoardRespDTO> getNoticesByPriority(String priorityLevel);
    List<NoticeBoardRespDTO> getNoticesByFrom(String from);
}