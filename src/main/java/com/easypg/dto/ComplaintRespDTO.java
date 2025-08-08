package com.easypg.dto;

import java.time.LocalDateTime;
import com.easypg.enums.ComplaintStatus;
import com.easypg.enums.PriorityLevel;
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
public class ComplaintRespDTO {
    
    private Long id;
    private String title;
    private String issue;
    private ComplaintStatus complaintStatus;
    private PriorityLevel priorityLevel;
    private LocalDateTime resolvedDate;
    private String actionTaken;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Tenant information
    private Long tenantId;
    private String tenantName;
    private String tenantContactNumber;
    private String tenantGender;
}