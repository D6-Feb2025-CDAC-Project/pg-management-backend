package com.easypg.service;

import com.easypg.dto.LoginResponseDTO;
import com.easypg.entities.BaseUser;

public interface UserService {
	public LoginResponseDTO authenticateUser(String identifier, String password);

	BaseUser loadUserById(Long id);
}
