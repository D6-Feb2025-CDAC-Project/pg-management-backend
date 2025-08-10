package com.easypg.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponseDTO {
	private Long id;
    private String username;

    private Long userid;
    private String userrole;
    private String token;
    
    public LoginResponseDTO(String username, Long userid, String userrole) {
    	this.username = username;
    	this.userid = userid;
    	this.userrole = userrole;
    }


}