package com.easypg.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.easypg.dto.EmailRequestDTO;
import com.easypg.dto.FacilityDTO;
import com.easypg.dto.VerifyOtpRequestDTO;
import com.easypg.service.FacilityService;
import com.easypg.service.OtpService;
import com.easypg.service.RoomService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/guest")
@AllArgsConstructor
public class GuestController {
	
    private final RoomService roomService;
    private final FacilityService facilityService;
    private final OtpService otpService;
	
	@GetMapping("rooms/facilities")
    @Operation(description = "Get rooms with facilties")
    public ResponseEntity<?> getRoomWithFacilties(){
    	return ResponseEntity.ok(roomService.findRoomWithFacilties());
    }
	
	@GetMapping("/facility")
	@Operation(description = "Get available facilities")
	public ResponseEntity<?> getAvailableFacilities(){
		List<FacilityDTO> facilities
		= facilityService.getAvailableFacilities();
		if(facilities.isEmpty()) // if no amenities are there
			 return ResponseEntity
					 .status(HttpStatus.NO_CONTENT).build();
		
		return ResponseEntity.ok(facilities);
	}
	
	 @PostMapping("/otp/generate")
	    public ResponseEntity<?> generateOtp(@RequestBody EmailRequestDTO request) {
	        if (request.getEmail() == null || request.getEmail().isEmpty()) {
	            return ResponseEntity.badRequest().body("Phone number is required");
	        }
	         boolean generated = otpService.generateOtp(request.getEmail());
	    
	        if(generated) {
	        	return ResponseEntity.ok("OTP sent successfully.....Valid for 5 Minutes only"); 
	        } else  {
	        	 return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                     .body("Failed to send OTP. Please try again.");
	        }
	   }
	 
	 @PostMapping("/otp/verify")
	    public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpRequestDTO request) {
	        boolean valid = otpService.verifyOtp(request.getEmail(), request.getOtp());
	        if (valid) {
	            return ResponseEntity.ok("OTP verified successfully");
	        } else {
	            return ResponseEntity.badRequest().body("Invalid or expired OTP");
	        }
	    }
}
