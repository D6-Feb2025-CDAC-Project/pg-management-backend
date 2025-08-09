package com.easypg.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.easypg.dto.AddTenantDTO;
import com.easypg.dto.ApiResponse;
import com.easypg.dto.TenantResponseDTO;
import com.easypg.dto.UpdateTenantDTO;
import com.easypg.entities.BaseUser;
import com.easypg.service.TenantService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/tenant")
@AllArgsConstructor
@Validated
public class TenantController {
	
	private final TenantService tenantService;
	
	/*
	 * Request handling method (REST API end point) 
	 * - desc - Add new tenant
	 * URL -http://host:port/tenant
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
	 * http://host:port/tenant 
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
	 * http://host:port/tenant/ 
	 * Method - PATCH
	 * Payload - Json representation of user 
	 * Resp - ApiResponse
	 */
	@PatchMapping
	public ResponseEntity<?> updateTenant(
			@AuthenticationPrincipal BaseUser userDetails,
	        @RequestBody UpdateTenantDTO requestDTO
	) {
		Long tenantId = userDetails.getId();
	    return ResponseEntity.ok(tenantService.updateTenant(tenantId, requestDTO));
	}
	
	/*
	 * Request handling method (REST API end point) URL -
	 * http://host:port/tenant/{tenantId} 
	 * Method - DELETE
	 * Payload - none
	 * Resp - ApiResponse
	 */
	@DeleteMapping
    public ResponseEntity<?> softDeleteTenant(@AuthenticationPrincipal BaseUser userDetails) {
		Long tenantId = userDetails.getId();
        ApiResponse response = tenantService.deleteTenant(tenantId);
        return ResponseEntity.ok(response);
    }
	
	
}
