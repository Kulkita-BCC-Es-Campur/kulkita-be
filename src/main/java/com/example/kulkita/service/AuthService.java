package com.example.kulkita.service;

import com.example.kulkita.dto.request.LoginRequest;
import com.example.kulkita.dto.request.RegistrationRequest;
import com.example.kulkita.dto.request.PasswordResetRequest;
import com.example.kulkita.dto.request.OtpVerificationRequest;
import com.example.kulkita.dto.response.AuthResponse;

public interface AuthService {
    void register(RegistrationRequest request);
    AuthResponse login(LoginRequest request);
    void sendPasswordResetOtp(String email);
    void resetPassword(PasswordResetRequest request);
    void verifyEmail(OtpVerificationRequest request);
    void resendOtp(String email, String type);
    void logoutAllDevices(String userId);
    void cleanupExpiredTokens();
}