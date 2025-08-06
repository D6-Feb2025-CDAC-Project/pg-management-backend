package com.easypg.entities;

import java.time.LocalDateTime;

import com.easypg.entities.ComplaintStatus;
import com.easypg.entities.PriorityLevel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "complaints")
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true, exclude = "tenant")
public class Complaint extends BaseEntity {

    @Column(length = 100)
    private String title;

    @Column(length = 1000)
    private String issue;

    @Enumerated(EnumType.STRING)
    @Column(name = "complaint_status", length = 20)
    private ComplaintStatus complaintStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority_level", length = 20)
    private PriorityLevel priorityLevel;

    private LocalDateTime resolvedDate;

    @Column(length = 1000)
    private String actionTaken;

    @ManyToOne
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    // AllArgsConstructor manually excluding tenant and calling super()
    public Complaint(String title, String issue, ComplaintStatus complaintStatus, PriorityLevel priorityLevel,
                     LocalDateTime resolvedDate, String actionTaken) {
        super(); // Calls BaseEntity constructor
        this.title = title;
        this.issue = issue;
        this.complaintStatus = complaintStatus;
        this.priorityLevel = priorityLevel;
        this.resolvedDate = resolvedDate;
        this.actionTaken = actionTaken;
    }
}
