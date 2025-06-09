package com.example.kulkita.service.impl;

import com.example.kulkita.constants.BusinessConstants;
import com.example.kulkita.dto.request.LoginRequest;
import com.example.kulkita.dto.request.RegistrationRequest;
import com.example.kulkita.dto.request.PasswordResetRequest;
import com.example.kulkita.dto.request.OtpVerificationRequest;
import com.example.kulkita.dto.response.AuthResponse;
import com.example.kulkita.entity.User;
import com.example.kulkita.entity.VerificationToken;
import com.example.kulkita.entity.PasswordResetToken;
import com.example.kulkita.exception.ResourceNotFoundException;
import com.example.kulkita.exception.TokenExpiredException;
import com.example.kulkita.exception.UserAlreadyExistsException;
import com.example.kulkita.repository.UserRepository;
import com.example.kulkita.repository.VerificationTokenRepository;
import com.example.kulkita.repository.PasswordResetTokenRepository;
import com.example.kulkita.security.jwt.JwtUtils;
import com.example.kulkita.service.AuthService;
import com.example.kulkita.service.EmailService;
import com.example.kulkita.service.OtpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    public static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final EmailService emailService;
    private final OtpService otpService;

    public AuthServiceImpl(UserRepository userRepository,
                           VerificationTokenRepository verificationTokenRepository,
                           PasswordResetTokenRepository passwordResetTokenRepository,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager,
                           JwtUtils jwtUtils,
                           EmailService emailService,
                           OtpService otpService) {
        this.userRepository = userRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.emailService = emailService;
        this.otpService = otpService;
    }

    @Override
    @Transactional
    public void register(RegistrationRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email is already in use: " + request.getEmail());
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username is already taken: " + request.getUsername());
        }

        otpService.validateOtpRateLimit(request.getEmail(), "verification");

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(false);

        userRepository.save(user);
        log.info("User created with ID: {}", user.getId());

        String otp = otpService.generateOtp();
        saveVerificationToken(user, otp);

        otpService.recordOtpAttempt(request.getEmail(), "verification");

        emailService.sendVerificationEmail(user.getEmail(), otp);
        log.info("Verification email sent to: {}", user.getEmail());
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        log.info("User login attempt for email: {}", request.getEmail());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.getEmail()));

            log.info("User logged in successfully: {}", user.getId());
            return new AuthResponse(jwt, user.getId().toString(), user.getUsername(), user.getEmail());

        } catch (DisabledException e) {
            throw new RuntimeException("Account is not verified. Please check your email for verification instructions.");
        } catch (BadCredentialsException e) {
            throw new RuntimeException("Invalid email or password.");
        } catch (AuthenticationException e) {
            throw new RuntimeException("Authentication failed: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void sendPasswordResetOtp(String email) {
        log.info("Password reset OTP requested for email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        otpService.validateOtpRateLimit(email, "reset");

        passwordResetTokenRepository.findByUser(user).ifPresent(passwordResetTokenRepository::delete);

        String otp = otpService.generateOtp();
        savePasswordResetToken(user, otp);

        otpService.recordOtpAttempt(email, "reset");

        emailService.sendPasswordResetEmail(email, otp);
        log.info("Password reset OTP sent to: {}", email);
    }

    @Override
    @Transactional
    public void resetPassword(PasswordResetRequest request) {
        log.info("Password reset attempt with OTP: {}", request.getOtp());

        PasswordResetToken resetToken = passwordResetTokenRepository.findByOtp(request.getOtp())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid or expired OTP"));

        if (resetToken.getExpiryDate().isBefore(Instant.now())) {
            passwordResetTokenRepository.delete(resetToken);
            throw new TokenExpiredException("OTP has expired. Please request a new one.");
        }

        User user = resetToken.getUser();

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        user.setTokenValidAfter(Instant.now());

        userRepository.save(user);

        passwordResetTokenRepository.delete(resetToken);

        log.info("Password reset successfully for user: {}", user.getId());
    }

    @Override
    @Transactional
    public void verifyEmail(OtpVerificationRequest request) {
        log.info("Email verification attempt with OTP: {}", request.getOtp());

        VerificationToken verificationToken = verificationTokenRepository.findByOtp(request.getOtp())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid or expired OTP"));

        if (verificationToken.getExpiryDate().isBefore(Instant.now())) {
            verificationTokenRepository.delete(verificationToken);
            throw new TokenExpiredException("OTP has expired. Please request a new one.");
        }

        User user = verificationToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);

        verificationTokenRepository.delete(verificationToken);

        emailService.sendWelcomeEmail(user.getEmail(), user.getUsername());

        log.info("Email verified successfully for user: {}", user.getId());
    }

    @Override
    @Transactional
    public void resendOtp(String email, String type) {
        log.info("OTP resend requested for email: {} and type: {}", email, type);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        otpService.validateOtpRateLimit(email, type);

        String otp = otpService.generateOtp();

        if ("verification".equals(type)) {
            verificationTokenRepository.findByUser(user).ifPresent(verificationTokenRepository::delete);

            saveVerificationToken(user, otp);

            emailService.sendVerificationEmail(email, otp);

        } else if ("reset".equals(type)) {
            passwordResetTokenRepository.findByUser(user).ifPresent(passwordResetTokenRepository::delete);

            savePasswordResetToken(user, otp);

            emailService.sendPasswordResetEmail(email, otp);
        } else {
            throw new IllegalArgumentException("Invalid OTP type: " + type);
        }

        otpService.recordOtpAttempt(email, type);

        log.info("OTP resent successfully for email: {} and type: {}", email, type);
    }

    @Override
    @Transactional
    public void logoutAllDevices(String userId) {
        log.info("Logout all devices requested for user: {}", userId);

        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        user.setTokenValidAfter(Instant.now());
        userRepository.save(user);

        log.info("All devices logged out for user: {}", userId);
    }

    @Override
    @Transactional
    public void cleanupExpiredTokens() {
        log.info("Cleaning up expired tokens");

        Instant now = Instant.now();
        verificationTokenRepository.deleteExpiredTokens(now);
        passwordResetTokenRepository.deleteExpiredTokens(now);

        log.info("Expired tokens cleanup completed");
    }

    private void saveVerificationToken(User user, String otp) {
        VerificationToken token = new VerificationToken();
        token.setUser(user);
        token.setOtp(otp);
        token.setExpiryDate(Instant.now().plusSeconds(BusinessConstants.NotificationSettings.OTP_EXPIRY_SECONDS));
        verificationTokenRepository.save(token);
    }

    private void savePasswordResetToken(User user, String otp) {
        PasswordResetToken token = new PasswordResetToken();
        token.setUser(user);
        token.setOtp(otp);
        token.setExpiryDate(Instant.now().plusSeconds(BusinessConstants.NotificationSettings.OTP_EXPIRY_SECONDS));
        passwordResetTokenRepository.save(token);
    }
}