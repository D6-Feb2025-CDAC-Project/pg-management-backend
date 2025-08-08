package com.easypg.dto;

import com.easypg.enums.ComplaintStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UpdateComplaintStatusDTO {
    
    @NotNull(message = "Complaint status is required")
    private ComplaintStatus complaintStatus;
}