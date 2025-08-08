package com.easypg.dto;

import lombok.*;

import java.util.List;
import com.easypg.enums.RoomType;
import com.easypg.enums.TenantType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomDTO {
	private Long id;
    private String roomNo;
    private RoomType roomType;
    private TenantType tenantType;
    private String floor;
    private double rentAmount;
    private int maxOccupancy;
    private int currentOccupancy;
    private List<FacilityDTO> facilities; 
    private String photoUrl;
    private boolean isAvailable;
}
