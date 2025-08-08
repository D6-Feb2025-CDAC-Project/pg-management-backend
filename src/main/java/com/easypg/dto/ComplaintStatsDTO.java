package com.easypg.dto;

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
public class ComplaintStatsDTO {
    
    // Overall statistics
    private Long totalComplaints;
    
    // Status-wise counts
    private Long pendingCount;
    private Long inProgressCount;
    private Long resolvedCount;
    
    // Priority-wise counts
    private Long highPriorityCount;
    private Long urgentPriorityCount;
    private Long moderatePriorityCount;
    private Long lowPriorityCount;
    private Long generalPriorityCount;
    private Long importantPriorityCount;
}
