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
public class BatchResponse {
    private String id;
    private String batchCode;
    private String ingredientName;
    private String category;

    private BigDecimal weight;
    private String unit;
    private String source;
    private Instant entryDate;
    private Instant expiryDate;
    private String storageLocation;
    private Batch.FreshnessStatus freshnessStatus;
    private boolean active;
    private String notes;
    private Instant createdAt;
    private Instant updatedAt;
}