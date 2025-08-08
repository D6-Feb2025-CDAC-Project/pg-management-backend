package com.easypg.dto;

import com.easypg.enums.RoomType;
import com.easypg.enums.TenantType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoomWithFacilitiesDTO{
	
	private Long id;
    private String roomNo;
    
    private RoomType roomType;

    private String floor;

    private double size; 

    private double rentAmount;

    private int currentOccupancy;

    private double maintenanceCharges;

    private double electricityCharges;

    private double deposit;
    
    private TenantType tenantType; 

    private String photoUrl;
    
    private List<FacilityDTO> facilties;
} 

