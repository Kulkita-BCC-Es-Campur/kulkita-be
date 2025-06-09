package com.example.kulkita.controller;

import com.example.kulkita.dto.response.*;
import com.example.kulkita.service.FifoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/fifo")
public class FifoController {

    private final FifoService fifoService;

    public FifoController(FifoService fifoService) {
        this.fifoService = fifoService;
    }

    /**
     * 🆕 NEW: Get ALL batches ordered by FIFO priority (First In, First Out)
     * This is the main endpoint for getting all batches with priority ordering
     */
    @GetMapping("/all-batches-by-priority")
    public ResponseEntity<List<FifoBatchResponse>> getAllBatchesByPriority() {
        List<FifoBatchResponse> batches = fifoService.getAllBatchesByFifoPriority();
        return ResponseEntity.ok(batches);
    }

    /**
     * 🆕 NEW: Get batches by priority with detailed priority information
     */
    @GetMapping("/batches-with-priority-details")
    public ResponseEntity<List<FifoBatchResponse>> getBatchesWithPriorityDetails(
            @RequestParam(required = false) String ingredientName,
            @RequestParam(required = false) String storageLocation,
            @RequestParam(required = false) String urgencyLevel) {

        List<FifoBatchResponse> batches = fifoService.getBatchesWithPriorityDetails(
                ingredientName, storageLocation, urgencyLevel);
        return ResponseEntity.ok(batches);
    }

    /**
     * 🆕 NEW: Get batches by expiry urgency (CRITICAL, HIGH, MEDIUM, LOW)
     */
    @GetMapping("/batches-by-urgency/{urgencyLevel}")
    public ResponseEntity<List<FifoBatchResponse>> getBatchesByUrgency(@PathVariable String urgencyLevel) {
        List<FifoBatchResponse> batches = fifoService.getBatchesByUrgencyLevel(urgencyLevel);
        return ResponseEntity.ok(batches);
    }

    /**
     * 🆕 NEW: Get FIFO priority dashboard with statistics
     */
    @GetMapping("/priority-dashboard")
    public ResponseEntity<FifoPriorityDashboardResponse> getFifoPriorityDashboard() {
        FifoPriorityDashboardResponse dashboard = fifoService.getFifoPriorityDashboard();
        return ResponseEntity.ok(dashboard);
    }

    /**
     * 3.1 - Get batches ordered by FIFO priority (existing endpoint)
     */
    @GetMapping("/batches")
    public ResponseEntity<List<FifoBatchResponse>> getBatchesByFifoPriority(
            @RequestParam(required = false) String ingredientName,
            @RequestParam(required = false) String storageLocation) {

        List<FifoBatchResponse> batches = fifoService.getBatchesByFifoPriority(ingredientName, storageLocation);
        return ResponseEntity.ok(batches);
    }

    /**
     * 3.1 - Get FIFO ordered batches for specific ingredient
     */
    @GetMapping("/batches/ingredient/{ingredientName}")
    public ResponseEntity<List<FifoBatchResponse>> getBatchesForIngredient(@PathVariable String ingredientName) {
        List<FifoBatchResponse> batches = fifoService.getBatchesByFifoPriority(ingredientName, null);
        return ResponseEntity.ok(batches);
    }

    /**
     * 3.1 - Get FIFO ordered batches for specific storage location
     */
    @GetMapping("/batches/location/{storageLocation}")
    public ResponseEntity<List<FifoBatchResponse>> getBatchesForLocation(@PathVariable String storageLocation) {
        List<FifoBatchResponse> batches = fifoService.getBatchesByFifoPriority(null, storageLocation);
        return ResponseEntity.ok(batches);
    }

    /**
     * 3.2 - Get picking recommendation for ingredient usage
     */
    @GetMapping("/picking-recommendation")
    public ResponseEntity<FifoPickingRecommendationResponse> getPickingRecommendation(
            @RequestParam String ingredientName,
            @RequestParam BigDecimal requiredWeight,
            @RequestParam String unit) {

        FifoPickingRecommendationResponse recommendation = fifoService.getPickingRecommendation(
                ingredientName, requiredWeight, unit);
        return ResponseEntity.ok(recommendation);
    }

    /**
     * 3.2 - Get picking recommendation with POST for complex requests
     */
    @PostMapping("/picking-recommendation")
    public ResponseEntity<FifoPickingRecommendationResponse> getPickingRecommendationPost(
            @RequestBody PickingRequestDto request) {

        FifoPickingRecommendationResponse recommendation = fifoService.getPickingRecommendation(
                request.getIngredientName(), request.getRequiredWeight(), request.getUnit());
        return ResponseEntity.ok(recommendation);
    }

    /**
     * 3.3 - Get storage layout recommendation
     */
    @GetMapping("/storage-layout")
    public ResponseEntity<List<StorageLayoutRecommendationResponse>> getStorageLayoutRecommendation(
            @RequestParam(required = false) String storageLocation) {

        List<StorageLayoutRecommendationResponse> layout = fifoService.getStorageLayoutRecommendation(storageLocation);
        return ResponseEntity.ok(layout);
    }

    /**
     * 3.3 - Get storage layout for specific location
     */
    @GetMapping("/storage-layout/{storageLocation}")
    public ResponseEntity<List<StorageLayoutRecommendationResponse>> getStorageLayoutForLocation(
            @PathVariable String storageLocation) {

        List<StorageLayoutRecommendationResponse> layout = fifoService.getStorageLayoutRecommendation(storageLocation);
        return ResponseEntity.ok(layout);
    }

    /**
     * Get FIFO summary for an ingredient
     */
    @GetMapping("/summary/{ingredientName}")
    public ResponseEntity<FifoSummaryResponse> getFifoSummary(@PathVariable String ingredientName) {
        FifoSummaryResponse summary = fifoService.getFifoSummary(ingredientName);
        return ResponseEntity.ok(summary);
    }

    /**
     * Get comprehensive FIFO analysis
     */
    @GetMapping("/analysis")
    public ResponseEntity<FifoAnalysisResponse> getFifoAnalysis() {
        FifoAnalysisResponse analysis = fifoService.getFifoAnalysis();
        return ResponseEntity.ok(analysis);
    }
}

// Supporting DTOs
class PickingRequestDto {
    private String ingredientName;
    private BigDecimal requiredWeight;
    private String unit;
    private String notes;

    public PickingRequestDto() {}

    public PickingRequestDto(String ingredientName, BigDecimal requiredWeight, String unit, String notes) {
        this.ingredientName = ingredientName;
        this.requiredWeight = requiredWeight;
        this.unit = unit;
        this.notes = notes;
    }

    public String getIngredientName() { return ingredientName; }
    public void setIngredientName(String ingredientName) { this.ingredientName = ingredientName; }

    public BigDecimal getRequiredWeight() { return requiredWeight; }
    public void setRequiredWeight(BigDecimal requiredWeight) { this.requiredWeight = requiredWeight; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}

