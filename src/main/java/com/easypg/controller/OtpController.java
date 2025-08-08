package com.easypg.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.easypg.dto.EmailRequestDTO;
import com.easypg.dto.VerifyOtpRequestDTO;
import com.easypg.service.OtpService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/otp")
@AllArgsConstructor
public class OtpController {

    private OtpService otpService;

    @PostMapping("/generate")
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

    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpRequestDTO request) {
        boolean valid = otpService.verifyOtp(request.getEmail(), request.getOtp());
        if (valid) {
            return ResponseEntity.ok("OTP verified successfully");
        } else {
            return ResponseEntity.badRequest().body("Invalid or expired OTP");
        }
    }

    

    
}
