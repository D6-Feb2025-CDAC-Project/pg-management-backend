package com.easypg.entities;

import java.util.List;

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

    
//  Changed from @ManyToOne to @ManyToMany
    @ManyToMany(mappedBy = "facilities", fetch = FetchType.LAZY)
    @JsonBackReference
    private List<Room> rooms; 
    
    
    

    
 // AllArgsConstructor manually excluding room and calling super()
    public Facility(String name, String category) {
        super(); // Calls BaseEntity constructor
        this.name = name;
        this.category = category;
    }
    
    

    // Explicit getter and setter for rooms 
    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

}