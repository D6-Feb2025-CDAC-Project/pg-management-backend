package com.easypg.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddTenantDTO {

	    // user fields
	    private String username;
	    private String email;
	    private String password;

	    // tenant fields
	    private String gender;
	    private String contactNumber;
	    private LocalDate moveInDate;
	    private Long roomId;
	

}
