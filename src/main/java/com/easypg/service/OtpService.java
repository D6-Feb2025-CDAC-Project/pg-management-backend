package com.easypg.service;


public interface OtpService {
	public boolean generateOtp(String email);
	public boolean verifyOtp(String email, String otp);
}
