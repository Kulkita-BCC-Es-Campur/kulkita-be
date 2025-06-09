package com.example.kulkita.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FreshnessStatusSummaryResponse {
    private String freshnessStatus;  // "GREEN", "YELLOW", "RED"
    private String statusName;       // "Aman", "Waspada", "Krisis"
    private long batchCount;
    private Double totalWeight;
    private Double percentage;       // Percentage of total batches
}