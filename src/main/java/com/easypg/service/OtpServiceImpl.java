package com.easypg.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easypg.entities.OtpDetails;

import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
@AllArgsConstructor
public class OtpServiceImpl implements OtpService{

    private final ConcurrentHashMap<String, OtpDetails> otpCache = new ConcurrentHashMap<>();
    private final Random random = new Random();

    private static final int OTP_EXPIRY_MINUTES = 5;

    @Override
    public boolean generateOtp(String email) {
        String otp = String.format("%04d", random.nextInt(10000)); // 0000 to 9999

        OtpDetails otpDetails = new OtpDetails(otp, LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES));
        otpCache.put(email, otpDetails);

        // TODO: send OTP via SMS or email here (call SMS/email service)

        System.out.println("Generated OTP for " + email + " : " + otp);  // for debugging

       if(otp != null) {
    	   return true;
       } else {
    	   return false;
       }
    }

    @Override
    public boolean verifyOtp(String email, String otp) {
        OtpDetails otpDetails = otpCache.get(email);
        otpCache.forEach((key, value) -> {
            System.out.println("OTP : "+otp+"Key: " + key + ", Value: " + value + value.getExpiryTime() + value.getOtp());
        });
        if (otpDetails == null) {
            return false; // No OTP generated for this phone
        }
        if (otpDetails.getExpiryTime().isBefore(LocalDateTime.now())) {
            otpCache.remove(email); // remove expired otp
            return false; // OTP expired already
        }
        if (otpDetails.getOtp().equals(otp)) {
        	System.out.println("Corrct otp!!");
            otpCache.remove(email); // OTP used, remove after using otp
            return true; // sending true -> otp verified successfully
        } 
        return false;
    }

    
}

