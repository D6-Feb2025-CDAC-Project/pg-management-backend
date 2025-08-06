package com.easypg.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "facilities")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "room")
public class Facility extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    @JsonBackReference
    private Room room;
    
 // AllArgsConstructor manually excluding room and calling super()
    public Facility(String name, String category) {
        super(); // Calls BaseEntity constructor
        this.name = name;
        this.category = category;
    }

}