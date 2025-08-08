package com.easypg.entities;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
// no setter required , initialize using constructor
public class OtpDetails {
    private final String otp;
    private final LocalDateTime expiryTime;
}

