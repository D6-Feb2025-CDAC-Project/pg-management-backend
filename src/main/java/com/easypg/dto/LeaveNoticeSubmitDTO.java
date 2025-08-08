package com.easypg.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeaveNoticeSubmitDTO {

    @NotNull(message = "Move-out date is required")
    @Future(message = "Move-out date must be in the future")
    private LocalDate moveOutDate;

    @Size(max = 100, message = "Reason can't exceed 100 characters")
    private String reasonOfLeave;

    @Size(max = 100, message = "Additional notes can't exceed 100 characters")
    private String additionalTenantNotes;
}
