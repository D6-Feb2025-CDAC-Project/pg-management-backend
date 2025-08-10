package com.easypg.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import com.easypg.entities.Tenant;
import com.easypg.service.FacilityService;
import com.easypg.service.OtpService;
import com.easypg.service.PaymentService;
import com.easypg.service.RoomService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/guest")
@Slf4j
public class GuestController {
	
	@Autowired
    private RoomService roomService;
	@Autowired
    private FacilityService facilityService;
	@Autowired
    private OtpService otpService;
	@Autowired
    private PaymentService paymentService;
    
    @Value("${razorpay.key.id}")
    private String razorpayKeyId;
	
	@GetMapping("rooms/facilities")
    @Operation(description = "Get rooms with facilties")
    public ResponseEntity<?> getRoomWithFacilties(){
    	return ResponseEntity.ok(roomService.findRoomWithFacilties());
    }
	
	@GetMapping("/facility")
	@Operation(description = "Get available facilities")
	public ResponseEntity<?> getAvailableFacilities(){
		List<FacilityDTO> facilities = facilityService.getAvailableFacilities();
		if(facilities.isEmpty()) {
			 return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}
		return ResponseEntity.ok(facilities);
	}
	
	@PostMapping("/otp/generate")
    @Operation(description = "Generate OTP for email verification")
    public ResponseEntity<?> generateOtp(@RequestBody EmailRequestDTO request) {
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email is required"));
        }
        
        // Check if user already exists
        if (paymentService.hasUserAlreadyRegistered(request.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("error", "User with this email already exists"));
        }
        
        boolean generated = otpService.generateOtp(request.getEmail());
    
        if(generated) {
        	return ResponseEntity.ok(Map.of(
        	    "success", true,
        	    "message", "OTP sent successfully. Valid for 5 minutes only"
        	)); 
        } else {
        	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                     .body(Map.of("error", "Failed to send OTP. Please try again."));
        }
    }
	 
	@PostMapping("/otp/verify")
    @Operation(description = "Verify OTP")
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpRequestDTO request) {
        boolean valid = otpService.verifyOtp(request.getEmail(), request.getOtp());
        if (valid) {
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "OTP verified successfully"
            ));
        } else {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Invalid or expired OTP"
            ));
        }
    }
    
    @PostMapping("/create-order")
    @Operation(description = "Create Razorpay order for registration payment")
    public ResponseEntity<?> createPaymentOrder(@RequestBody Map<String, Object> request) {
        try {
            Double amount = Double.valueOf(request.get("amount").toString());
            String receipt = "reg_" + System.currentTimeMillis();
            
            String orderId = paymentService.createOrder(amount, receipt);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("orderId", orderId);
            response.put("amount", amount);
            response.put("currency", "INR");
            response.put("keyId", razorpayKeyId);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Order creation failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Order creation failed: " + e.getMessage()));
        }
    }
    
    @PostMapping("/verify-payment")
    @Operation(description = "Verify payment and complete registration")
    public ResponseEntity<?> verifyPaymentAndCompleteRegistration(@RequestBody Map<String, Object> request) {
        try {
            // Extract payment details
            String orderId = request.get("orderId").toString();
            String paymentId = request.get("paymentId").toString();
            String signature = request.get("signature").toString();
            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            
            // Check if this payment is already processed (prevent duplicate)
            if (paymentService.isPaymentAlreadyProcessed(paymentId)) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Payment already processed"
                ));
            }
            
            // Verify payment signature with Razorpay
            boolean isValid = paymentService.verifyPayment(orderId, paymentId, signature);
            if (!isValid) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Payment verification failed"
                ));
            }
            
            // Extract registration data
            @SuppressWarnings("unchecked")
            Map<String, Object> registrationData = (Map<String, Object>) request.get("registrationData");
            
            String username = registrationData.get("username").toString();
            String email = registrationData.get("email").toString();
            String password = registrationData.get("password").toString();
            String gender = registrationData.get("gender").toString();
            String contactNumber = registrationData.get("contactNumber").toString();
            LocalDate moveInDate = LocalDate.parse(registrationData.get("moveInDate").toString());
            Long roomId = Long.valueOf(registrationData.get("roomId").toString());
            
            // Check if user already exists (double check)
            if (paymentService.hasUserAlreadyRegistered(email)) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "User with this email already exists"
                ));
            }
            
            // Create tenant with payment in single transaction
            Tenant tenant = paymentService.createTenantWithPayment(
                username, email, password, gender, contactNumber, moveInDate,
                roomId, orderId, paymentId, signature, amount
            );
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Registration completed successfully",
                "tenantId", tenant.getId(),
                "email", email
            ));
            
        } catch (Exception e) {
            log.error("Registration completion failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Registration failed: " + e.getMessage()));
        }
    }
}