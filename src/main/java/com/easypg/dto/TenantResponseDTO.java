package com.easypg.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TenantResponseDTO {
	    private String username;           
	    private String roomNumber;
	    private String roomType;  
	    private int tenureInMonths;
	    private int totalComplaints;
	    private int activeComplaints;
	    private String contactNumber;
	    private String email;
}