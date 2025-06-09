package com.example.kulkita.service;

import com.example.kulkita.entity.Batch;
import com.example.kulkita.entity.User;
import com.example.kulkita.exception.BusinessValidationException;
import com.example.kulkita.exception.EntityNotFoundException;
import com.example.kulkita.repository.BatchRepository;
import com.example.kulkita.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

/**
 * Service for centralized validation and error handling
 */
@Service
public class ValidationService {

    private final BatchRepository batchRepository;
    private final UserRepository userRepository;

    public ValidationService(BatchRepository batchRepository, UserRepository userRepository) {
        this.batchRepository = batchRepository;
        this.userRepository = userRepository;
    }

    // =================================================================
    // ENTITY VALIDATION
    // =================================================================

    public Batch validateBatchExists(String batchCode) {
        return batchRepository.findByBatchCode(batchCode)
                .orElseThrow(() -> EntityNotFoundException.batch(batchCode));
    }

    public User validateUserExists(String identifier) {
        Optional<User> user = userRepository.findByEmail(identifier);
        if (user.isEmpty()) {
            user = userRepository.findByUsername(identifier);
        }
        return user.orElseThrow(() -> EntityNotFoundException.user(identifier));
    }

    public void validateBatchActive(Batch batch) {
        if (!batch.isActive()) {
            throw BusinessValidationException.inactiveBatch(batch.getBatchCode());
        }
    }

    public void validateBatchNotExpired(Batch batch) {
        if (batch.getExpiryDate() != null && batch.getExpiryDate().isBefore(Instant.now())) {
            throw BusinessValidationException.expiredBatch(batch.getBatchCode(), batch.getExpiryDate());
        }
    }

    public void validateSufficientStock(Batch batch, BigDecimal requiredWeight) {
        if (batch.getWeight().compareTo(requiredWeight) < 0) {
            throw BusinessValidationException.insufficientStock(
                    batch.getBatchCode(),
                    batch.getWeight(),
                    requiredWeight
            );
        }
    }

    public void validatePositiveWeight(BigDecimal weight) {
        if (weight == null || weight.compareTo(BigDecimal.ZERO) <= 0) {
            throw BusinessValidationException.invalidWeight(weight);
        }
    }

    // =================================================================
    // COMBINED VALIDATIONS
    // =================================================================

    public Batch validateBatchForUsage(String batchCode, BigDecimal requiredWeight) {
        Batch batch = validateBatchExists(batchCode);
        validateBatchActive(batch);
        validateBatchNotExpired(batch);
        validateSufficientStock(batch, requiredWeight);
        return batch;
    }

    public void validateBatchCreationRequest(String ingredientName, BigDecimal weight, String storageLocation) {
        if (ingredientName == null || ingredientName.trim().isEmpty()) {
            throw new IllegalArgumentException("Ingredient name is required");
        }

        validatePositiveWeight(weight);

        if (storageLocation == null || storageLocation.trim().isEmpty()) {
            throw new IllegalArgumentException("Storage location is required");
        }
    }
}