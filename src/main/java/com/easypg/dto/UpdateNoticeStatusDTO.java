package com.easypg.dto;

import com.easypg.enums.NoticeResponseStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateNoticeStatusDTO {
    @NotNull
    private NoticeResponseStatus newStatus;
    private String reviewNotes;
}
