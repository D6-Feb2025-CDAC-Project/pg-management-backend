package com.easypg.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.easypg.dto.AddTenantDTO;
import com.easypg.dto.ApiResponse;
import com.easypg.dto.TenantResponseDTO;
import com.easypg.dto.UpdateTenantDTO;
import com.easypg.service.TenantService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/tenant")
@CrossOrigin(origins = "http://localhost:3000")
@AllArgsConstructor
@Validated
public class TenantController {
	
	private final TenantService tenantService;
	
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
	@PostMapping
	@Operation(description = "Add new Tenant")
	public ResponseEntity<?> addNewTenant(@RequestBody AddTenantDTO dto){
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(tenantService.addNewTenant(dto));
	}

	/*
	 * Request handling method (REST API end point) URL -
	 * http://host:port/user 
	 * Method - GET 
	 * Payload - none 
	 * Resp - in case of empty list - SC204 (NO_CONTENT) 
	 * o.w SC 200 + list of restaurants -> JSON []
	 */
	@GetMapping
	@Operation(description = "Get all Tenants")
	public  ResponseEntity<?> getAllTenants() {
		List<TenantResponseDTO> tenants = tenantService.getAllTenants();
		if(tenants.isEmpty())
			 return ResponseEntity
					 .status(HttpStatus.NO_CONTENT).build();
		//=> list non empty		
		return ResponseEntity.ok(tenants);
	}
	
	/*
	 * Request handling method (REST API end point) URL -
	 * http://host:port/user/{tenantId} 
	 * Method - PATCH
	 * Payload - Json representation of user 
	 * Resp - ApiResponse
	 */
	@PatchMapping("/{tenantId}")
	public ResponseEntity<?> updateTenant(
	        @PathVariable Long tenantId,
	        @RequestBody UpdateTenantDTO requestDTO
	) {
	    return ResponseEntity.ok(tenantService.updateTenant(tenantId, requestDTO));
	}
	
	/*
	 * Request handling method (REST API end point) URL -
	 * http://host:port/user/{tenantId} 
	 * Method - DELETE
	 * Payload - none
	 * Resp - ApiResponse
	 */
	@DeleteMapping("/{tenantId}")
    public ResponseEntity<?> softDeleteTenant(@PathVariable Long tenantId) {
        ApiResponse response = tenantService.deleteTenant(tenantId);
        return ResponseEntity.ok(response);
    }
}
