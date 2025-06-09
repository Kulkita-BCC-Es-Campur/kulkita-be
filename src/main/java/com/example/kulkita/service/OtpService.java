package com.example.kulkita.service;

public interface OtpService {
    String generateOtp();
    boolean canSendOtp(String email, String type);
    void recordOtpAttempt(String email, String type);
    void validateOtpRateLimit(String email, String type);
}