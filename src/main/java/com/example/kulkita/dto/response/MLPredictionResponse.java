package com.example.kulkita.dto.response;

import com.example.kulkita.entity.Batch;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MLPredictionResponse {
    private boolean success;
    private String message;
    private Instant predictedExpiryDate;
    private Batch.FreshnessStatus freshnessStatus;
    private int estimatedShelfLifeDays;
    private double confidence;
    private String modelVersion;
}