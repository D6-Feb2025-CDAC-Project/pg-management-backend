package com.easypg.entities;

import com.easypg.enums.From;
import com.easypg.enums.PriorityLevel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "notice_board")
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class NoticeBoard extends BaseEntity {

    @Column(length = 100, nullable = false)
    private String title;

    @Column(length = 1000, nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "notice_from", length = 30, nullable = false)
    private From from;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority_level", length = 30, nullable = false)
    private PriorityLevel priorityLevel;

    // AllArgsConstructor manually
    public NoticeBoard(String title, String message, From from, PriorityLevel priorityLevel) {
        super();
        this.title = title;
        this.message = message;
        this.from = from;
        this.priorityLevel = priorityLevel;
    }
}
