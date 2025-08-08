package com.easypg.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public  class VerifyOtpRequestDTO {
    private String email;
    private String otp;

}