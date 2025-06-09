package com.example.kulkita.service;

import com.example.kulkita.entity.User;
import com.example.kulkita.exception.ResourceNotFoundException;
import com.example.kulkita.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserLookupService {

    private static final Logger log = LoggerFactory.getLogger(UserLookupService.class);

    private final UserRepository userRepository;

    public UserLookupService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findByIdentifier(String identifier) {
        log.debug("Looking for user with identifier: {}", identifier);

        if (identifier == null || identifier.trim().isEmpty()) {
            throw new IllegalArgumentException("User identifier cannot be null or empty");
        }

        String trimmedIdentifier = identifier.trim();

        Optional<User> userByEmail = userRepository.findByEmail(trimmedIdentifier);
        if (userByEmail.isPresent()) {
            log.debug("User found by email: {}", trimmedIdentifier);
            return userByEmail.get();
        }

        Optional<User> userByUsername = userRepository.findByUsername(trimmedIdentifier);
        if (userByUsername.isPresent()) {
            log.debug("User found by username: {}", trimmedIdentifier);
            return userByUsername.get();
        }

        log.error("User not found with identifier: {}", trimmedIdentifier);
        throw new ResourceNotFoundException("User not found with identifier: " + trimmedIdentifier);
    }

    public Optional<User> findByIdentifierOptional(String identifier) {
        try {
            return Optional.of(findByIdentifier(identifier));
        } catch (ResourceNotFoundException | IllegalArgumentException e) {
            log.debug("User not found with identifier: {} - {}", identifier, e.getMessage());
            return Optional.empty();
        }
    }

    public boolean existsByIdentifier(String identifier) {
        return findByIdentifierOptional(identifier).isPresent();
    }

    public User findByEmail(String email) {
        log.debug("Looking for user with email: {}", email);

        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", email);
                    return new ResourceNotFoundException("User not found with email: " + email);
                });
    }

    public User findByUsername(String username) {
        log.debug("Looking for user with username: {}", username);

        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found with username: {}", username);
                    return new ResourceNotFoundException("User not found with username: " + username);
                });
    }
}