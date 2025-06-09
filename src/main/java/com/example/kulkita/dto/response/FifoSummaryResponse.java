package com.example.kulkita.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FifoSummaryResponse {
    private String ingredientName;
    private int totalBatches;
    private BigDecimal totalWeight;
    private Instant oldestBatchDate;
    private Instant newestBatchDate;
    private List<FifoBatchSummaryDto> batchSummaries;
}