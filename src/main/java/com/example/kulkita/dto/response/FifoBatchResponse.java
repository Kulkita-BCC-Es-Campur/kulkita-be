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
public class FifoBatchResponse {
    private String id;
    private String batchCode;
    private String ingredientName;
    private BigDecimal weight;
    private String unit;
    private Instant entryDate;
    private Instant expiryDate;
    private String storageLocation;
    private Batch.FreshnessStatus freshnessStatus;
    private long daysUntilExpiry;
    private String urgencyLevel;
    private String notes;
}