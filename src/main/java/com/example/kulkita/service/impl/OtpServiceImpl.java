package com.example.kulkita.service.impl;

import com.example.kulkita.constants.BusinessConstants;
import com.example.kulkita.entity.OtpAttempt;
import com.example.kulkita.exception.OtpLimitExceededException;
import com.example.kulkita.repository.OtpAttemptRepository;
import com.example.kulkita.service.OtpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Optional;

@Service
public class OtpServiceImpl implements OtpService {

    private static final Logger log = LoggerFactory.getLogger(OtpServiceImpl.class);
    private static final int OTP_LENGTH = 6;
    private static final int MAX_ATTEMPTS_PER_HOUR = 3;
    private static final int RATE_LIMIT_WINDOW_HOURS = 1;

    private final OtpAttemptRepository otpAttemptRepository;
    private final SecureRandom random = new SecureRandom();

    public OtpServiceImpl(OtpAttemptRepository otpAttemptRepository) {
        this.otpAttemptRepository = otpAttemptRepository;
    }

    @Override
    public String generateOtp() {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < BusinessConstants.NotificationSettings.OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }

    @Override
    public boolean canSendOtp(String email, String type) {
        Instant oneHourAgo = Instant.now().minusSeconds(
                3600 * BusinessConstants.NotificationSettings.OTP_RATE_LIMIT_WINDOW_HOURS);
        long recentAttempts = otpAttemptRepository.countRecentAttempts(email, type, oneHourAgo);
        return recentAttempts < BusinessConstants.NotificationSettings.MAX_OTP_ATTEMPTS_PER_HOUR;
    }

    @Override
    @Transactional
    public void recordOtpAttempt(String email, String type) {
        Optional<OtpAttempt> existingAttempt = otpAttemptRepository.findByEmailAndOtpType(email, type);

        if (existingAttempt.isPresent()) {
            OtpAttempt attempt = existingAttempt.get();

            if (attempt.getCreatedAt().isBefore(Instant.now().minusSeconds(3600))) {
                attempt.setAttemptCount(1);
                attempt.setCreatedAt(Instant.now());
            } else {
                attempt.setAttemptCount(attempt.getAttemptCount() + 1);
            }

            otpAttemptRepository.save(attempt);
        } else {
            OtpAttempt newAttempt = new OtpAttempt();
            newAttempt.setEmail(email);
            newAttempt.setOtpType(type);
            newAttempt.setAttemptCount(1);
            otpAttemptRepository.save(newAttempt);
        }
    }

    @Override
    public void validateOtpRateLimit(String email, String type) {
        if (!canSendOtp(email, type)) {
            throw new OtpLimitExceededException(
                    String.format("Too many OTP requests. You can request a new %s OTP after %d hour(s).",
                            type, BusinessConstants.NotificationSettings.OTP_RATE_LIMIT_WINDOW_HOURS)
            );
        }
    }
}