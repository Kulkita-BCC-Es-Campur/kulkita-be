package com.example.kulkita.dto.response;

import com.example.kulkita.entity.Batch;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BatchPositionDto {
    private String batchCode;
    private int recommendedPosition;
    private Instant entryDate;
    private Instant expiryDate;
    private Batch.FreshnessStatus freshnessStatus;
    private BigDecimal weight;
    private String unit;
    private String positionRecommendation;
    private String accessibilityScore;
}