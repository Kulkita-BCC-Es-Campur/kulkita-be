package com.example.kulkita.controller;

import com.example.kulkita.dto.request.LoginRequest;
import com.example.kulkita.dto.request.RegistrationRequest;
import com.example.kulkita.dto.request.PasswordResetRequest;
import com.example.kulkita.dto.request.OtpVerificationRequest;
import com.example.kulkita.dto.response.AuthResponse;
import com.example.kulkita.dto.response.MessageResponse;
import com.example.kulkita.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(@Valid @RequestBody RegistrationRequest request) {
        authService.register(request);
        return ResponseEntity.ok(new MessageResponse("User registered successfully. Please check your email for verification."));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(@RequestParam String email) {
        authService.sendPasswordResetOtp(email);
        return ResponseEntity.ok(new MessageResponse("OTP sent to your email. It is valid for 5 minutes."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody PasswordResetRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(new MessageResponse("Password has been reset successfully."));
    }

    @PostMapping("/verify-email")
    public ResponseEntity<MessageResponse> verifyEmail(@Valid @RequestBody OtpVerificationRequest request) {
        authService.verifyEmail(request);
        return ResponseEntity.ok(new MessageResponse("Email verified successfully."));
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<MessageResponse> resendOtp(@RequestParam String email, @RequestParam String type) {
        authService.resendOtp(email, type);
        return ResponseEntity.ok(new MessageResponse("OTP has been resent to your email."));
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout() {
        return ResponseEntity.ok(new MessageResponse("Logged out successfully."));
    }

    @PostMapping("/logout-all-devices")
    public ResponseEntity<MessageResponse> logoutAllDevices(@RequestParam String email) {
        authService.logoutAllDevices(email);
        return ResponseEntity.ok(new MessageResponse("Logged out from all devices successfully."));
    }
}