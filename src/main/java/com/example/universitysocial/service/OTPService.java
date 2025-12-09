package com.example.universitysocial.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class OTPService {

    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRATION_MINUTES = 10;
    private static final SecureRandom random = new SecureRandom();

    public String generateOTP() {
        StringBuilder otp = new StringBuilder(OTP_LENGTH);
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }

    public LocalDateTime getOtpExpirationTime() {
        return LocalDateTime.now().plusMinutes(OTP_EXPIRATION_MINUTES);
    }

    public boolean isOtpExpired(LocalDateTime expirationTime) {
        return expirationTime == null || LocalDateTime.now().isAfter(expirationTime);
    }

    public boolean validateOtp(String providedOtp, String storedOtp, LocalDateTime expirationTime) {
        if (providedOtp == null || storedOtp == null) {
            return false;
        }
        if (isOtpExpired(expirationTime)) {
            return false;
        }
        return providedOtp.equals(storedOtp);
    }
}

