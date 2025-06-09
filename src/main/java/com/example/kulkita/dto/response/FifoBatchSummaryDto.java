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
public class FifoBatchSummaryDto {
    private String batchCode;
    private BigDecimal weight;
    private String unit;
    private Instant entryDate;
    private Instant expiryDate;
    private Batch.FreshnessStatus freshnessStatus;
    private long daysUntilExpiry;
    private int fifoRank;
}
