package com.example.kulkita.service;

import com.example.kulkita.constants.BusinessConstants;
import com.example.kulkita.controller.FifoAnalysisResponse;
import com.example.kulkita.dto.response.*;
import com.example.kulkita.entity.Batch;
import com.example.kulkita.repository.BatchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FifoService {

    private static final Logger log = LoggerFactory.getLogger(FifoService.class);

    private final BatchRepository batchRepository;

    public FifoService(BatchRepository batchRepository) {
        this.batchRepository = batchRepository;
    }

    public List<FifoBatchResponse> getAllBatchesByFifoPriority() {
        log.info("Getting ALL batches ordered by FIFO priority");

        List<Batch> batches = batchRepository.findActiveBatchesOrderedByFifoPriority();

        return batches.stream()
                .map(this::convertToFifoBatchResponse)
                .collect(Collectors.toList());
    }

    public List<FifoBatchResponse> getBatchesWithPriorityDetails(String ingredientName,
                                                                 String storageLocation,
                                                                 String urgencyLevel) {
        log.info("Getting batches with priority details - ingredient: {}, location: {}, urgency: {}",
                ingredientName, storageLocation, urgencyLevel);

        List<Batch> batches;

        if (ingredientName != null && storageLocation != null) {
            batches = batchRepository.findByIngredientAndLocationOrderedByFifoPriority(ingredientName, storageLocation);
        } else if (ingredientName != null) {
            batches = batchRepository.findByIngredientNameOrderedByFifoPriority(ingredientName);
        } else if (storageLocation != null) {
            batches = batchRepository.findByStorageLocationOrderedByFifoPriority(storageLocation);
        } else {
            batches = batchRepository.findActiveBatchesOrderedByFifoPriority();
        }

        List<FifoBatchResponse> responses = batches.stream()
                .map(this::convertToFifoBatchResponse)
                .collect(Collectors.toList());

        if (urgencyLevel != null && !urgencyLevel.isEmpty()) {
            responses = responses.stream()
                    .filter(batch -> urgencyLevel.equalsIgnoreCase(batch.getUrgencyLevel()))
                    .collect(Collectors.toList());
        }

        return responses;
    }

    public List<FifoBatchResponse> getBatchesByUrgencyLevel(String urgencyLevel) {
        log.info("Getting batches by urgency level: {}", urgencyLevel);

        List<Batch> allBatches = batchRepository.findActiveBatchesOrderedByFifoPriority();

        return allBatches.stream()
                .map(this::convertToFifoBatchResponse)
                .filter(batch -> urgencyLevel.equalsIgnoreCase(batch.getUrgencyLevel()))
                .collect(Collectors.toList());
    }

    public FifoPriorityDashboardResponse getFifoPriorityDashboard() {
        log.info("Generating FIFO priority dashboard");

        List<Batch> allBatches = batchRepository.findActiveBatchesOrderedByFifoPriority();
        List<FifoBatchResponse> batchResponses = allBatches.stream()
                .map(this::convertToFifoBatchResponse)
                .collect(Collectors.toList());

        FifoPriorityDashboardResponse.PriorityStatistics stats = calculatePriorityStatistics(batchResponses);

        List<FifoBatchResponse> criticalBatches = batchResponses.stream()
                .filter(batch -> "CRITICAL".equals(batch.getUrgencyLevel()))
                .limit(10)
                .collect(Collectors.toList());

        List<FifoBatchResponse> highPriorityBatches = batchResponses.stream()
                .filter(batch -> "HIGH".equals(batch.getUrgencyLevel()))
                .limit(10)
                .collect(Collectors.toList());

        FifoPriorityDashboardResponse.FifoComplianceMetrics compliance = calculateComplianceMetrics(allBatches);

        List<String> recommendations = generatePriorityRecommendations(stats, compliance);

        FifoPriorityDashboardResponse dashboard = new FifoPriorityDashboardResponse();
        dashboard.setTotalActiveBatches(allBatches.size());
        dashboard.setPriorityStatistics(stats);
        dashboard.setCriticalBatches(criticalBatches);
        dashboard.setHighPriorityBatches(highPriorityBatches);
        dashboard.setComplianceMetrics(compliance);
        dashboard.setRecommendations(recommendations);

        return dashboard;
    }

    public FifoAnalysisResponse getFifoAnalysis() {
        log.info("Performing comprehensive FIFO analysis");

        List<Batch> allBatches = batchRepository.findActiveBatchesOrderedByFifoPriority();
        List<FifoBatchResponse> batchResponses = allBatches.stream()
                .map(this::convertToFifoBatchResponse)
                .collect(Collectors.toList());

        int criticalCount = (int) batchResponses.stream().filter(b -> "CRITICAL".equals(b.getUrgencyLevel())).count();
        int highCount = (int) batchResponses.stream().filter(b -> "HIGH".equals(b.getUrgencyLevel())).count();
        int mediumCount = (int) batchResponses.stream().filter(b -> "MEDIUM".equals(b.getUrgencyLevel())).count();
        int lowCount = (int) batchResponses.stream().filter(b -> "LOW".equals(b.getUrgencyLevel())).count();

        double complianceScore = calculateFifoComplianceScore(allBatches);

        List<String> recommendations = generateAnalysisRecommendations(criticalCount, highCount, complianceScore);

        FifoAnalysisResponse analysis = new FifoAnalysisResponse();
        analysis.setMessage("FIFO analysis completed successfully");
        analysis.setFifoComplianceScore(Math.round(complianceScore * 100.0) / 100.0);
        analysis.setTotalBatches(allBatches.size());
        analysis.setCriticalBatches(criticalCount);
        analysis.setHighPriorityBatches(highCount);
        analysis.setMediumPriorityBatches(mediumCount);
        analysis.setLowPriorityBatches(lowCount);
        analysis.setRecommendations(recommendations);

        return analysis;
    }

    public FifoPickingRecommendationResponse getPickingRecommendation(String ingredientName, BigDecimal requestedAmount, String unit) {
        log.info("Getting FIFO picking recommendation for {} {} of {}", requestedAmount, unit, ingredientName);

        List<Batch> availableBatches = batchRepository.findByIngredientNameAndActiveTrueOrderByEntryDateAsc(ingredientName);

        if (availableBatches.isEmpty()) {
            return createUnavailableResponse(ingredientName, requestedAmount, unit, "No active batches available");
        }

        BigDecimal totalAvailable = availableBatches.stream()
                .map(Batch::getWeight)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalAvailable.compareTo(requestedAmount) < 0) {
            return createShortageResponse(ingredientName, requestedAmount, unit, totalAvailable, availableBatches);
        }

        List<PickingInstructionDto> instructions = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        BigDecimal remaining = requestedAmount;

        for (Batch batch : availableBatches) {
            if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }

            BigDecimal toUse = remaining.min(batch.getWeight());
            String urgencyLevel = calculateUrgencyLevel(batch);

            PickingInstructionDto instruction = new PickingInstructionDto();
            instruction.setBatchCode(batch.getBatchCode());
            instruction.setAmountToUse(toUse);
            instruction.setUnit(batch.getUnit());
            instruction.setExpiryDate(batch.getExpiryDate());
            instruction.setUrgencyLevel(urgencyLevel);
            instruction.setSequence(instructions.size() + 1);
            instruction.setStorageLocation(batch.getStorageLocation());
            instruction.setPickingNote(generatePickingNote(batch, urgencyLevel));

            instructions.add(instruction);

            if ("HIGH".equals(urgencyLevel) || "CRITICAL".equals(urgencyLevel)) {
                warnings.add("Batch " + batch.getBatchCode() + " expires soon - use immediately");
            }

            remaining = remaining.subtract(toUse);
        }

        FifoPickingRecommendationResponse response = new FifoPickingRecommendationResponse();
        response.setIngredientName(ingredientName);
        response.setRequestedAmount(requestedAmount);
        response.setUnit(unit);
        response.setCanFulfill(true);
        response.setTotalAvailable(totalAvailable);
        response.setShortage(BigDecimal.ZERO);
        response.setMessage("FIFO picking instructions generated successfully");
        response.setPickingInstructions(instructions);
        response.setWarnings(warnings);

        return response;
    }

    public List<FifoBatchResponse> getBatchesByFifoPriority(String ingredientName, String storageLocation) {
        List<Batch> batches;

        if (ingredientName != null && storageLocation != null) {
            batches = batchRepository.findByIngredientNameAndStorageLocationAndActiveTrueOrderByEntryDateAsc(ingredientName, storageLocation);
        } else if (ingredientName != null) {
            batches = batchRepository.findByIngredientNameAndActiveTrueOrderByEntryDateAsc(ingredientName);
        } else if (storageLocation != null) {
            batches = batchRepository.findByStorageLocationAndActiveTrueOrderByEntryDateAsc(storageLocation);
        } else {
            batches = batchRepository.findByActiveTrueOrderByEntryDateAsc();
        }

        return batches.stream()
                .map(this::convertToFifoBatchResponse)
                .collect(Collectors.toList());
    }

    public List<StorageLayoutRecommendationResponse> getStorageLayoutRecommendation(String storageLocation) {
        log.info("Getting storage layout recommendation for location: {}", storageLocation);

        List<Batch> batches;
        if (storageLocation != null && !storageLocation.isEmpty()) {
            batches = batchRepository.findByStorageLocationAndActiveTrueOrderByEntryDateAsc(storageLocation);
        } else {
            batches = batchRepository.findByActiveTrueOrderByEntryDateAsc();
        }

        Map<String, Map<String, List<Batch>>> batchesByLocationAndIngredient = batches.stream()
                .collect(Collectors.groupingBy(
                        Batch::getStorageLocation,
                        Collectors.groupingBy(Batch::getIngredientName)
                ));

        return batchesByLocationAndIngredient.entrySet().stream()
                .map(locationEntry -> {
                    String location = locationEntry.getKey();
                    Map<String, List<Batch>> ingredientBatches = locationEntry.getValue();

                    List<IngredientLayoutDto> ingredientLayouts = ingredientBatches.entrySet().stream()
                            .map(ingredientEntry -> {
                                String ingredient = ingredientEntry.getKey();
                                List<Batch> batchList = ingredientEntry.getValue();

                                batchList.sort(Comparator.comparing(Batch::getEntryDate));

                                List<BatchPositionDto> positions = new ArrayList<>();
                                for (int i = 0; i < batchList.size(); i++) {
                                    Batch batch = batchList.get(i);
                                    BatchPositionDto position = new BatchPositionDto();
                                    position.setBatchCode(batch.getBatchCode());
                                    position.setRecommendedPosition(i + 1);
                                    position.setEntryDate(batch.getEntryDate());
                                    position.setExpiryDate(batch.getExpiryDate());
                                    position.setFreshnessStatus(batch.getFreshnessStatus());
                                    position.setWeight(batch.getWeight());
                                    position.setUnit(batch.getUnit());
                                    position.setPositionRecommendation(generatePositionRecommendation(i + 1, batchList.size()));
                                    position.setAccessibilityScore(calculateAccessibilityScore(i + 1, batchList.size()));

                                    positions.add(position);
                                }

                                IngredientLayoutDto layout = new IngredientLayoutDto();
                                layout.setIngredientName(ingredient);
                                layout.setBatchPositions(positions);
                                return layout;
                            })
                            .collect(Collectors.toList());

                    StorageLayoutRecommendationResponse layoutResponse = new StorageLayoutRecommendationResponse();
                    layoutResponse.setStorageLocation(location);
                    layoutResponse.setIngredientLayouts(ingredientLayouts);
                    return layoutResponse;
                })
                .collect(Collectors.toList());
    }

    public FifoSummaryResponse getFifoSummary(String ingredientName) {
        log.info("Getting FIFO summary for ingredient: {}", ingredientName);

        List<Batch> batches = batchRepository.findByIngredientNameAndActiveTrueOrderByEntryDateAsc(ingredientName);

        if (batches.isEmpty()) {
            return new FifoSummaryResponse(ingredientName, 0, BigDecimal.ZERO, null, null, Collections.emptyList());
        }

        batches.sort(Comparator.comparing(Batch::getEntryDate));

        Batch oldestBatch = batches.get(0);
        Batch newestBatch = batches.get(batches.size() - 1);
        BigDecimal totalWeight = batches.stream()
                .map(Batch::getWeight)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<FifoBatchSummaryDto> batchSummaries = batches.stream()
                .map(batch -> new FifoBatchSummaryDto(
                        batch.getBatchCode(),
                        batch.getWeight(),
                        batch.getUnit(),
                        batch.getEntryDate(),
                        batch.getExpiryDate(),
                        batch.getFreshnessStatus(),
                        calculateDaysUntilExpiry(batch.getExpiryDate()),
                        calculateFifoRank(batch, batches)
                ))
                .collect(Collectors.toList());

        return new FifoSummaryResponse(
                ingredientName,
                batches.size(),
                totalWeight,
                oldestBatch.getEntryDate(),
                newestBatch.getEntryDate(),
                batchSummaries
        );
    }

    private FifoPriorityDashboardResponse.PriorityStatistics calculatePriorityStatistics(List<FifoBatchResponse> batches) {
        int total = batches.size();
        int critical = (int) batches.stream().filter(b -> "CRITICAL".equals(b.getUrgencyLevel())).count();
        int high = (int) batches.stream().filter(b -> "HIGH".equals(b.getUrgencyLevel())).count();
        int medium = (int) batches.stream().filter(b -> "MEDIUM".equals(b.getUrgencyLevel())).count();
        int low = (int) batches.stream().filter(b -> "LOW".equals(b.getUrgencyLevel())).count();

        double criticalPct = total > 0 ? (double) critical / total * 100 : 0;
        double highPct = total > 0 ? (double) high / total * 100 : 0;
        double mediumPct = total > 0 ? (double) medium / total * 100 : 0;
        double lowPct = total > 0 ? (double) low / total * 100 : 0;

        return new FifoPriorityDashboardResponse.PriorityStatistics(
                critical, high, medium, low,
                Math.round(criticalPct * 100.0) / 100.0,
                Math.round(highPct * 100.0) / 100.0,
                Math.round(mediumPct * 100.0) / 100.0,
                Math.round(lowPct * 100.0) / 100.0
        );
    }

    private FifoPriorityDashboardResponse.FifoComplianceMetrics calculateComplianceMetrics(List<Batch> batches) {
        int totalBatches = batches.size();
        int correctOrder = 0;
        int outOfOrder = 0;
        BigDecimal weightAtRisk = BigDecimal.ZERO;
        List<String> issues = new ArrayList<>();

        Map<String, List<Batch>> batchesByIngredient = batches.stream()
                .collect(Collectors.groupingBy(Batch::getIngredientName));

        for (Map.Entry<String, List<Batch>> entry : batchesByIngredient.entrySet()) {
            List<Batch> ingredientBatches = entry.getValue();

            ingredientBatches.sort(Comparator.comparing(Batch::getEntryDate));

            for (int i = 0; i < ingredientBatches.size() - 1; i++) {
                Batch current = ingredientBatches.get(i);
                Batch next = ingredientBatches.get(i + 1);

                if (current.getExpiryDate() != null && next.getExpiryDate() != null) {
                    if (current.getExpiryDate().isAfter(next.getExpiryDate())) {
                        outOfOrder++;
                        weightAtRisk = weightAtRisk.add(current.getWeight());
                        issues.add("Batch " + current.getBatchCode() + " expires after " + next.getBatchCode() + " but was stored first");
                    } else {
                        correctOrder++;
                    }
                }
            }
        }

        double complianceScore = totalBatches > 0 ? (double) correctOrder / (correctOrder + outOfOrder) * 100 : 100;

        return new FifoPriorityDashboardResponse.FifoComplianceMetrics(
                Math.round(complianceScore * 100.0) / 100.0,
                correctOrder,
                outOfOrder,
                weightAtRisk,
                issues
        );
    }

    private List<String> generatePriorityRecommendations(FifoPriorityDashboardResponse.PriorityStatistics stats,
                                                         FifoPriorityDashboardResponse.FifoComplianceMetrics compliance) {
        List<String> recommendations = new ArrayList<>();

        if (stats.getCriticalCount() > 0) {
            recommendations.add("🚨 " + stats.getCriticalCount() + " critical batches need immediate attention");
        }

        if (stats.getHighCount() > 0) {
            recommendations.add("⚡ " + stats.getHighCount() + " high priority batches should be used within 3 days");
        }

        if (compliance.getOverallComplianceScore() < 80) {
            recommendations.add("📊 FIFO compliance is low (" + compliance.getOverallComplianceScore() + "%). Review storage organization");
        }

        if (compliance.getBatchesOutOfOrder() > 0) {
            recommendations.add("🔄 " + compliance.getBatchesOutOfOrder() + " batches are out of FIFO order");
        }

        if (stats.getCriticalPercentage() > 10) {
            recommendations.add("⚠️ High percentage of critical batches (" + stats.getCriticalPercentage() + "%). Consider increasing inventory turnover");
        }

        if (recommendations.isEmpty()) {
            recommendations.add("✅ FIFO system is working well. Continue current practices");
        }

        return recommendations;
    }

    private double calculateFifoComplianceScore(List<Batch> batches) {
        if (batches.size() < BusinessConstants.ComplianceThresholds.MINIMUM_BATCHES_FOR_COMPLIANCE) {
            return BusinessConstants.ComplianceThresholds.PERFECT_COMPLIANCE_SCORE;
        }

        int compliantPairs = 0;
        int totalPairs = 0;

        Map<String, List<Batch>> batchesByIngredient = batches.stream()
                .collect(Collectors.groupingBy(Batch::getIngredientName));

        for (List<Batch> ingredientBatches : batchesByIngredient.values()) {
            if (ingredientBatches.size() < 2) continue;

            ingredientBatches.sort(Comparator.comparing(Batch::getEntryDate));

            for (int i = 0; i < ingredientBatches.size() - 1; i++) {
                Batch current = ingredientBatches.get(i);
                Batch next = ingredientBatches.get(i + 1);

                totalPairs++;

                if (current.getExpiryDate() != null && next.getExpiryDate() != null) {
                    if (!current.getExpiryDate().isAfter(next.getExpiryDate())) {
                        compliantPairs++;
                    }
                } else {
                    compliantPairs++;
                }
            }
        }

        return totalPairs > 0 ? (double) compliantPairs / totalPairs * 100 : 100.0;
    }

    private List<String> generateAnalysisRecommendations(int criticalCount, int highCount, double complianceScore) {
        List<String> recommendations = new ArrayList<>();

        if (criticalCount > 0) {
            recommendations.add("Immediate action required: " + criticalCount + " batches expiring within 24-48 hours");
        }

        if (highCount > 0) {
            recommendations.add("Plan usage: " + highCount + " batches need to be used within 3 days");
        }

        if (complianceScore < BusinessConstants.ComplianceThresholds.POOR_COMPLIANCE_THRESHOLD) {
            recommendations.add("Poor FIFO compliance (" + Math.round(complianceScore) + "%). Reorganize storage to improve order");
        } else if (complianceScore < BusinessConstants.ComplianceThresholds.MODERATE_COMPLIANCE_THRESHOLD) {
            recommendations.add("Moderate FIFO compliance (" + Math.round(complianceScore) + "%). Some improvements needed");
        } else {
            recommendations.add("Good FIFO compliance (" + Math.round(complianceScore) + "%). Continue current practices");
        }

        return recommendations;
    }

    private FifoPickingRecommendationResponse createUnavailableResponse(String ingredientName, BigDecimal requestedAmount, String unit, String message) {
        FifoPickingRecommendationResponse response = new FifoPickingRecommendationResponse();
        response.setIngredientName(ingredientName);
        response.setRequestedAmount(requestedAmount);
        response.setUnit(unit);
        response.setCanFulfill(false);
        response.setTotalAvailable(BigDecimal.ZERO);
        response.setShortage(requestedAmount);
        response.setMessage(message);
        response.setPickingInstructions(new ArrayList<>());
        response.setWarnings(Arrays.asList("No stock available for " + ingredientName));
        return response;
    }

    private FifoPickingRecommendationResponse createShortageResponse(String ingredientName, BigDecimal requestedAmount, String unit, BigDecimal totalAvailable, List<Batch> availableBatches) {
        BigDecimal shortage = requestedAmount.subtract(totalAvailable);

        List<PickingInstructionDto> instructions = new ArrayList<>();
        for (Batch batch : availableBatches) {
            PickingInstructionDto instruction = new PickingInstructionDto();
            instruction.setBatchCode(batch.getBatchCode());
            instruction.setAmountToUse(batch.getWeight());
            instruction.setUnit(batch.getUnit());
            instruction.setExpiryDate(batch.getExpiryDate());
            instruction.setUrgencyLevel(calculateUrgencyLevel(batch));
            instruction.setSequence(instructions.size() + 1);
            instruction.setStorageLocation(batch.getStorageLocation());
            instruction.setPickingNote("Use all available stock");
            instructions.add(instruction);
        }

        FifoPickingRecommendationResponse response = new FifoPickingRecommendationResponse();
        response.setIngredientName(ingredientName);
        response.setRequestedAmount(requestedAmount);
        response.setUnit(unit);
        response.setCanFulfill(false);
        response.setTotalAvailable(totalAvailable);
        response.setShortage(shortage);
        response.setMessage("Insufficient stock. Need " + shortage + " " + unit + " more");
        response.setPickingInstructions(instructions);
        response.setWarnings(Arrays.asList("Stock shortage: " + shortage + " " + unit + " missing"));

        return response;
    }

    private FifoBatchResponse convertToFifoBatchResponse(Batch batch) {
        long daysUntilExpiry = batch.getExpiryDate() != null ?
                ChronoUnit.DAYS.between(Instant.now(), batch.getExpiryDate()) : Long.MAX_VALUE;

        FifoBatchResponse response = new FifoBatchResponse();
        response.setId(batch.getId().toString());
        response.setBatchCode(batch.getBatchCode());
        response.setIngredientName(batch.getIngredientName());
        response.setWeight(batch.getWeight());
        response.setUnit(batch.getUnit());
        response.setEntryDate(batch.getEntryDate());
        response.setExpiryDate(batch.getExpiryDate());
        response.setStorageLocation(batch.getStorageLocation());
        response.setFreshnessStatus(batch.getFreshnessStatus());
        response.setDaysUntilExpiry(daysUntilExpiry);
        response.setUrgencyLevel(calculateUrgencyLevel(batch));
        response.setNotes(batch.getNotes());

        return response;
    }

    private String calculateUrgencyLevel(Batch batch) {
        if (batch.getExpiryDate() == null) {
            return "MEDIUM";
        }

        long daysUntilExpiry = ChronoUnit.DAYS.between(Instant.now(), batch.getExpiryDate());

        if (daysUntilExpiry <= BusinessConstants.FreshnessPeriods.CRITICAL_DAYS_THRESHOLD) {
            return "CRITICAL";
        } else if (daysUntilExpiry <= BusinessConstants.FreshnessPeriods.HIGH_PRIORITY_DAYS_THRESHOLD) {
            return "HIGH";
        } else if (daysUntilExpiry <= BusinessConstants.FreshnessPeriods.MEDIUM_PRIORITY_DAYS_THRESHOLD) {
            return "MEDIUM";
        } else {
            return "LOW";
        }
    }

    private String generatePickingNote(Batch batch, String urgencyLevel) {
        StringBuilder note = new StringBuilder();

        switch (urgencyLevel) {
            case "CRITICAL":
                note.append("URGENT: Use immediately - expires today/tomorrow");
                break;
            case "HIGH":
                note.append("Priority: Use within 3 days");
                break;
            case "MEDIUM":
                note.append("Standard: Use within a week");
                break;
            default:
                note.append("Normal: Good shelf life remaining");
                break;
        }

        note.append(" | Location: ").append(batch.getStorageLocation());
        return note.toString();
    }

    private String generatePositionRecommendation(int position, int totalBatches) {
        if (position <= totalBatches * BusinessConstants.EconomicFactors.FRONT_POSITION_THRESHOLD) {
            return "Front position - easy access for FIFO";
        } else if (position <= totalBatches * BusinessConstants.EconomicFactors.MIDDLE_POSITION_THRESHOLD) {
            return "Middle position - moderate access";
        } else {
            return "Back position - newest items";
        }
    }

    private String calculateAccessibilityScore(int position, int totalBatches) {
        double score = (double) (totalBatches - position + 1) / totalBatches * 100;

        if (score >= BusinessConstants.EconomicFactors.EXCELLENT_ACCESSIBILITY_THRESHOLD) {
            return "Excellent";
        } else if (score >= BusinessConstants.EconomicFactors.GOOD_ACCESSIBILITY_THRESHOLD) {
            return "Good";
        } else if (score >= BusinessConstants.EconomicFactors.FAIR_ACCESSIBILITY_THRESHOLD) {
            return "Fair";
        } else {
            return "Poor";
        }
    }

    private long calculateDaysUntilExpiry(Instant expiryDate) {
        if (expiryDate == null) {
            return Long.MAX_VALUE;
        }
        return ChronoUnit.DAYS.between(Instant.now(), expiryDate);
    }

    private int calculateFifoRank(Batch batch, List<Batch> allBatches) {
        for (int i = 0; i < allBatches.size(); i++) {
            if (allBatches.get(i).getId().equals(batch.getId())) {
                return i + 1;
            }
        }
        return allBatches.size();
    }
}