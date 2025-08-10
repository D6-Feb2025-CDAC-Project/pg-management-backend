// NoticeBoardRespDTO.java
package com.easypg.dto;

import java.time.LocalDateTime;

import com.easypg.enums.From;
import com.easypg.enums.PriorityLevel;
import com.fasterxml.jackson.annotation.JsonFormat;

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
public class NoticeBoardRespDTO {
    
    private Long id;
    private String title;
    private String message;
    private From from;
    private PriorityLevel priorityLevel;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}