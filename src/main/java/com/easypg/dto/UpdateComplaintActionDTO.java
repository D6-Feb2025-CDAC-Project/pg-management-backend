package com.easypg.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
public class UpdateComplaintActionDTO {
    
    @NotBlank(message = "Action taken description is required")
    @Size(max = 1000, message = "Action taken cannot exceed 1000 characters")
    private String actionTaken;
}