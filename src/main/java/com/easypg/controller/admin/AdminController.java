package com.easypg.controller.admin;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.easypg.dto.TenantResponseDTO;
import com.easypg.service.TenantService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/admin")
@AllArgsConstructor
@Validated
public class AdminController {

	private final TenantService tenantService;
	/*
	 * Request handling method (REST API end point) URL -
	 * http://host:port/admin
	 * Method - GET 
	 * Payload - none 
	 * Resp - in case of empty list - SC204 (NO_CONTENT) 
	 * o.w SC 200 + list of restaurants -> JSON []
	 */
	@GetMapping
	@Operation(description = "Get all Tenants")
	public  ResponseEntity<?> getAllTenants() {
		List<TenantResponseDTO> tenants = tenantService.getAllTenants();
		System.out.println("HI TANVI");
		if(tenants.isEmpty())
			 return ResponseEntity
					 .status(HttpStatus.NO_CONTENT).build();
		//=> list non empty		
		return ResponseEntity.ok(tenants);
	}
}
