package com.easypg.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easypg.dao.UserDao;
import com.easypg.dto.LoginResponseDTO;
import com.easypg.entities.BaseUser;


import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserDao userDao;
    //private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public LoginResponseDTO authenticateUser(String identifier, String password) {
        Optional<BaseUser> userOptional = userDao.findByEmail(identifier);

        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        BaseUser user = userOptional.get();

        // Use passwordEncoder.matches to compare raw password (input) and encoded password (stored)
        // if (!passwordEncoder.matches(password, user.getPassword())) {
        //     throw new IllegalArgumentException("Incorrect password");
        // }

        return new LoginResponseDTO(user.getUsername(), user.getId(), user.getUserRole().toString());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    	 System.out.println("=== loadUserByUsername called with email: " + email + " ===");
    	    Thread.dumpStack(); // This will show you the call stack
        
        BaseUser user = userDao.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        
        return user;
    }

    @Override
    @Transactional(readOnly = true)
    public BaseUser loadUserById(Long id) {
        return userDao.findById(id)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
    }
}
