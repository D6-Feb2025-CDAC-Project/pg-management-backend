package com.easypg.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateTenantDTO {
	private String username;
    private String email;

    private String contactNumber;
    private String roomNumber;
}