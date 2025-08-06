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
    
    private long totalComplaints;
    private long pendingComplaints;
    private long inProgressComplaints;
    private long resolvedComplaints;
    
    // Priority-wise stats
    private long highPriorityComplaints;
    private long moderatePriorityComplaints;
    private long lowPriorityComplaints;
    private long urgentPriorityComplaints;
    private long importantPriorityComplaints;
    private long generalPriorityComplaints;
}
