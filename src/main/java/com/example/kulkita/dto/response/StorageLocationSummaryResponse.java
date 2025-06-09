package com.example.kulkita.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StorageLocationSummaryResponse {
    private String storageLocation;
    private long batchCount;
    private Double totalWeight;
    private long greenBatches;
    private long yellowBatches;
    private long redBatches;
}