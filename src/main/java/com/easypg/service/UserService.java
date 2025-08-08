package com.easypg.service;

import com.easypg.dto.LoginResponseDTO;

public interface UserService {
	public LoginResponseDTO authenticateUser(String identifier, String password);
}
