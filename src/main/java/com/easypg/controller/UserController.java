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

import com.easypg.dto.AddTenantDTO;
import com.easypg.dto.LoginRequestDTO;
import com.easypg.dto.LoginResponseDTO;
import com.easypg.entities.BaseUser;
import com.easypg.security.JwtUtil;
import com.easypg.service.TenantService;

import com.easypg.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
@Validated
public class UserController {	
	private final AuthenticationManager authManager;
	private final TenantService tenantService;
	
	private final JwtUtil jwtUtil;
	
	/*
	 * Request handling method (REST API end point) 
	 * - desc - Add new tenant
	 * URL -http://host:port/user
	 * Method - POST 
	 * Payload -JSON representation of tenant
	 * Resp - in case failure (dup email Id) - ApiResp DTO
	 *  - containing err mesg + SC 409(CONFLICT)
	 *  success - SC 201 + ApiResp - success mesg
	 */
	@PostMapping("/register")
	@Operation(description = "Add new Tenant")
	public ResponseEntity<?> addNewTenant(@RequestBody AddTenantDTO dto){
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(tenantService.addNewTenant(dto));
	}
	
	@PostMapping("/login")
	public ResponseEntity<?> authenticate(@RequestBody LoginRequestDTO cr) {
		// authenticate user with authentication manager		
		Authentication auth = new UsernamePasswordAuthenticationToken(cr.getIdentifier(), cr.getPassword());
		System.out.println("BEFORE AUTH: " + auth);
		auth = authManager.authenticate(auth);
		System.out.println("AFTER AUTH: " + auth);
		// after authentication, create JWT token and return.
		String token = jwtUtil.createToken(auth);
		//get username, userid and userrole
		BaseUser user = (BaseUser)auth.getPrincipal();
		LoginResponseDTO response = new LoginResponseDTO( user.getName(), user.getId(), user.getUserRole().toString());
		response.setToken(token);
		return ResponseEntity.ok(response);
	}
	
	
}
