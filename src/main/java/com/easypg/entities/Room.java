package com.easypg.entities;

import com.easypg.enums.RoomType;
import com.easypg.enums.TenantType;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "facilities")
public class Room extends BaseEntity {
	
	    @Column(name = "room_no", nullable = false, unique = true)
	    private String roomNo;
         
	    @Enumerated(EnumType.STRING)
	    @Column(name = "room_type", nullable = false)
	    private RoomType roomType;

	    @Column(nullable = false)
	    private String floor;

	    @Column(nullable = false)
	    private double size; 

	    @Column(name = "rent_amount", nullable = false)
	    private double rentAmount;

	    @Column(name = "current_occupancy", nullable = false)
	    private int currentOccupancy;

	    @Column(name = "max_occupancy", nullable = false)
	    private int maxOccupancy;

	    @Column(name = "is_available", nullable = false)
	    private boolean isAvailable;

	    @Column(name = "maintenance_charges")
	    private double maintenanceCharges;

	    @Column(name = "electricity_charges")
	    private double electricityCharges;

	    @Column(nullable = false)
	    private double deposit;
	    
	    private boolean hidden = false;  
         
	    @JsonManagedReference
	    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	    private List<Facility> facilities;
	    
	    @Enumerated(EnumType.STRING)
	    @Column(name = "tenant_type", nullable = false)
	    private TenantType tenantType; 

	    @Column(name = "photo_url")
	    private String photoUrl;
	    
	 // AllArgsConstructor manually excluding facilities and calling super()
	    public Room(String roomNo, RoomType roomType, String floor, double size, double rentAmount,
	                int currentOccupancy, int maxOccupancy, boolean isAvailable, double maintenanceCharges,
	                double electricityCharges, double deposit, TenantType tenantType, String photoUrl) {
	        super(); // Calls BaseEntity constructor 
	        this.roomNo = roomNo;
	        this.roomType = roomType;
	        this.floor = floor;
	        this.size = size;
	        this.rentAmount = rentAmount;
	        this.currentOccupancy = currentOccupancy;
	        this.maxOccupancy = maxOccupancy;
	        this.isAvailable = isAvailable;
	        this.maintenanceCharges = maintenanceCharges;
	        this.electricityCharges = electricityCharges;
	        this.deposit = deposit;
	        this.tenantType = tenantType;
	        this.photoUrl = photoUrl;
	    }


}
