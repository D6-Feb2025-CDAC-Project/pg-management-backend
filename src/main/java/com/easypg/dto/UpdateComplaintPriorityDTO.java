package com.easypg.dto;

import com.easypg.enums.PriorityLevel;
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
public class UpdateComplaintPriorityDTO {
    
    @NotNull(message = "Priority level is required")
    private PriorityLevel priorityLevel;
}
