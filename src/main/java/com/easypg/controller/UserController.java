package com.easypg.controller;

import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.easypg.dto.LoginRequestDTO;
import com.easypg.dto.LoginResponseDTO;
import com.easypg.security.JwtUtil;
import com.easypg.service.TenantService;
import com.easypg.service.UserService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
@Validated
public class UserController {
	private final UserService userService;
	
	private final AuthenticationManager authManager;
	
	private final JwtUtil jwtUtil;
	
	/*
	 * Request handling method (REST API end point) URL -
	 * http://host:port/tenant/login
	 * Method - POST
	 * Payload - LoginRequestDTO
	 * Resp - ApiResponse
	 */
//	 @PostMapping("/login")
//	    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
//	        try {
//	            LoginResponseDTO response = userService.authenticateUser(request.getIdentifier(), request.getPassword());
//	            return ResponseEntity.ok(response);
//	        } catch (IllegalArgumentException e) {
//	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
//	        }
//	    }
	
	@PostMapping("/login")
	public ResponseEntity<?> authenticate(@RequestBody LoginRequestDTO cr) {
		// authenticate user with authentication manager		
		Authentication auth = new UsernamePasswordAuthenticationToken(cr.getIdentifier(), cr.getPassword());
		System.out.println("BEFORE AUTH: " + auth);
		auth = authManager.authenticate(auth);
		System.out.println("AFTER AUTH: " + auth);
		// after authentication, create JWT token and return.
		String token = jwtUtil.createToken(auth);
		return ResponseEntity.ok(token);
	}
}
