package com.example.kulkita.repository;

import com.example.kulkita.entity.OtpAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OtpAttemptRepository extends JpaRepository<OtpAttempt, UUID> {
    Optional<OtpAttempt> findByEmailAndOtpType(String email, String otpType);

    @Modifying
    @Query("DELETE FROM OtpAttempt oa WHERE oa.createdAt < :cutoffTime")
    void deleteOldAttempts(@Param("cutoffTime") Instant cutoffTime);

    @Query("SELECT COUNT(oa) FROM OtpAttempt oa WHERE oa.email = :email AND oa.otpType = :otpType AND oa.createdAt > :since")
    long countRecentAttempts(@Param("email") String email, @Param("otpType") String otpType, @Param("since") Instant since);
}