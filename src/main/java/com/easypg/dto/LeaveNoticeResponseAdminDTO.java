package com.easypg.dto;

import java.time.LocalDate;

import com.easypg.enums.NoticeResponseStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeaveNoticeResponseAdminDTO {

    private Long id;

    private LocalDate moveOutDate;

    private String reasonOfLeave;

    private String additionalTenantNotes;

    private NoticeResponseStatus noticeResponseStatus;

    private String reviewNotes;

    private Long tenantId;

    private String tenantName;

    private boolean settlementGenerated;
}