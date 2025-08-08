package com.easypg.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.easypg.dao.UserDao;
import com.easypg.dto.LoginResponseDTO;
import com.easypg.entities.User;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class UserServiceImpl implements UserService{
	private final UserDao userDao;
	private final PasswordEncoder passwordEncoder;
	
	@Override
	public LoginResponseDTO authenticateUser(String identifier, String password) {
	    Optional<User> userOptional = userDao.findByEmail(identifier);

	    if (userOptional.isEmpty()) {
	        throw new IllegalArgumentException("User not found");
	    }

	    User user = userOptional.get();

	    // Use passwordEncoder.matches to compare raw password (input) and encoded password (stored)
	    if (!passwordEncoder.matches(password, user.getPassword())) {
	        throw new IllegalArgumentException("Incorrect password");
	    }

	    return new LoginResponseDTO(user.getUsername());
	}

}
