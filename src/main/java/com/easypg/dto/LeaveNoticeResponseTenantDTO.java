package com.easypg.dto;

import java.time.LocalDate;

import com.easypg.enums.NoticeResponseStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeaveNoticeResponseTenantDTO {

    private Long id;

    private LocalDate moveOutDate;

    private String reasonOfLeave;

    private String additionalTenantNotes;

    private NoticeResponseStatus noticeResponseStatus;

    private boolean settlementGenerated;
}

