package com.easypg.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.easypg.dto.FacilityDTO;
import com.easypg.service.FacilityService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/facility")
@AllArgsConstructor
@Validated
public class FacilityController {
	private final FacilityService facilityService;
	
	/*
	 * Request handling method (REST API end point) 
	 * - desc - get available facilities
	 * URL -http://host:port/facility
	 * Method - GET
	 * Payload -none
	 * Resp - in case failure (dup email Id) - ApiResp DTO
     * in case of empty list - SC204 (NO_CONTENT) 
	 * o.w SC 200 + list of facilities -> JSON []
	 */
	@GetMapping
	@Operation(description = "Get available facilities")
	public ResponseEntity<?> getAvailableFacilities(){
		List<FacilityDTO> facilities
		= facilityService.getAvailableFacilities();
		if(facilities.isEmpty())
			 return ResponseEntity
					 .status(HttpStatus.NO_CONTENT).build();
		//=> list non empty		
		return ResponseEntity.ok(facilities);
	}
	
	/*
	 * Request handling method (REST API end point) 
	 * - desc - add new facility
	 * URL -http://host:port/facility
	 * Method - POST
	 * Payload - JSON representation of facility
	 * Resp - in case failure (dup facility name) - ApiResp DTO
	 *  - containing err mesg + SC 400(BAD_REQUEST)
	 *  success - SC 201 + ApiResp - success mesg
	 */
//	@PostMapping
//	@Operation(description = "Add new facility")
//	public ResponseEntity<?> addNewFacility(@RequestBody FacilityDTO dto){
//		
//	}
	
}
