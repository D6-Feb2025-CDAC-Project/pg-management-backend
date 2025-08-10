package com.easypg.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.easypg.dao.UserDao;
import com.easypg.dto.LoginResponseDTO;
import com.easypg.entities.BaseUser;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class UserServiceImpl implements UserService , UserDetailsService {
	private final UserDao userDao;
	//private final PasswordEncoder passwordEncoder;
	
	@Override
	public LoginResponseDTO authenticateUser(String identifier, String password) {
	    Optional<BaseUser> userOptional = userDao.findByEmail(identifier);

	    if (userOptional.isEmpty()) {
	        throw new IllegalArgumentException("User not found");
	    }

	    BaseUser user = userOptional.get();

	    // Use passwordEncoder.matches to compare raw password (input) and encoded password (stored)
//	    if (!passwordEncoder.matches(password, user.getPassword())) {
//	        throw new IllegalArgumentException("Incorrect password");
//	    }

    return new LoginResponseDTO(user.getUsername(), user.getId(), user.getUserRole().toString());
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		BaseUser user = userDao.findByEmail(username).orElseThrow(()-> new UsernameNotFoundException("User not found"));
		return user;

	}

	 // New method: load user by ID (used in JwtUtil)
    public BaseUser loadUserById(Long id) {
        return userDao.findById(id)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
    }
}
