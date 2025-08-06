// AddNoticeBoardDTO.java
package com.easypg.dto;

import com.easypg.enums.From;
import com.easypg.enums.PriorityLevel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class AddNoticeBoardDTO {
    
    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title cannot exceed 100 characters")
    private String title;
    
    @NotBlank(message = "Message is required")
    @Size(max = 1000, message = "Message cannot exceed 1000 characters")
    private String message;
    
    @NotNull(message = "Notice source is required")
    private From from;
    
    @NotNull(message = "Priority level is required")
    private PriorityLevel priorityLevel;
}