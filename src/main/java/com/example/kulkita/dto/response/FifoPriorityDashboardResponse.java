package com.example.kulkita.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FifoPriorityDashboardResponse {
    private int totalActiveBatches;
    private PriorityStatistics priorityStatistics;
    private List<FifoBatchResponse> criticalBatches;
    private List<FifoBatchResponse> highPriorityBatches;
    private FifoComplianceMetrics complianceMetrics;
    private List<String> recommendations;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PriorityStatistics {
        private int criticalCount;
        private int highCount;
        private int mediumCount;
        private int lowCount;
        private double criticalPercentage;
        private double highPercentage;
        private double mediumPercentage;
        private double lowPercentage;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FifoComplianceMetrics {
        private double overallComplianceScore;
        private int batchesInCorrectOrder;
        private int batchesOutOfOrder;
        private BigDecimal totalWeightAtRisk;
        private List<String> complianceIssues;
    }
}