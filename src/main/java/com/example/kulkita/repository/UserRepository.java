package com.example.kulkita.repository;

import com.example.kulkita.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByProviderAndProviderId(String provider, String providerId);
    Boolean existsByEmail(String email);
    Boolean existsByUsername(String username);

    @Modifying
    @Query("UPDATE User u SET u.tokenValidAfter = :timestamp WHERE u.id = :userId")
    void invalidateTokensAfter(@Param("userId") UUID userId, @Param("timestamp") Instant timestamp);

    @Modifying
    @Query("UPDATE User u SET u.enabled = true WHERE u.id = :userId")
    void enableUser(@Param("userId") UUID userId);
}