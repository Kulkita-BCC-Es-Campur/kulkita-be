package com.example.kulkita.controller;

import com.example.kulkita.dto.request.BatchCreateRequest;
import com.example.kulkita.dto.request.BatchStatusUpdateRequest;
import com.example.kulkita.dto.request.ManualUsageRequest;
import com.example.kulkita.dto.request.MenuUsageRequest;
import com.example.kulkita.dto.response.*;
import com.example.kulkita.security.services.UserPrincipal;
import com.example.kulkita.service.BatchService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/batches")
public class BatchController {

    private final BatchService batchService;

    public BatchController(BatchService batchService) {
        this.batchService = batchService;
    }

    @PostMapping
    public ResponseEntity<BatchResponse> createBatch(@Valid @RequestBody BatchCreateRequest request) {
        BatchResponse response = batchService.createBatch(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<BatchResponse>> getAllActiveBatches() {
        List<BatchResponse> batches = batchService.getActiveBatches();
        return ResponseEntity.ok(batches);
    }

    @GetMapping("/advanced-filter")
    public ResponseEntity<List<BatchResponse>> getBatchesWithAdvancedFilter(
            @RequestParam(required = false) String ingredientName,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String storageLocation,
            @RequestParam(required = false) String freshnessStatus,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String sortBy) {
        List<BatchResponse> batches = batchService.getBatchesWithAdvancedFilter(
                ingredientName, category, storageLocation, freshnessStatus, startDate, endDate, sortBy);
        return ResponseEntity.ok(batches);
    }

    @GetMapping("/by-category/{category}")
    public ResponseEntity<List<BatchResponse>> getBatchesByCategory(@PathVariable String category) {
        List<BatchResponse> batches = batchService.getBatchesByCategory(category);
        return ResponseEntity.ok(batches);
    }

    @GetMapping("/category-summary")
    public ResponseEntity<List<CategorySummaryResponse>> getCategorySummary() {
        List<CategorySummaryResponse> summary = batchService.getCategorySummary();
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getAllCategories() {
        List<String> categories = batchService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/storage-locations")
    public ResponseEntity<List<String>> getAllStorageLocations() {
        List<String> locations = batchService.getAllStorageLocations();
        return ResponseEntity.ok(locations);
    }

    @GetMapping("/ingredients/autocomplete")
    public ResponseEntity<List<String>> getIngredientNamesForAutocomplete(@RequestParam(required = false) String query) {
        List<String> ingredients = batchService.getIngredientNamesForAutocomplete(query);
        return ResponseEntity.ok(ingredients);
    }

    @GetMapping("/expiring/{days}")
    public ResponseEntity<List<BatchResponse>> getExpiringBatches(@PathVariable int days) {
        List<BatchResponse> batches = batchService.getExpiringBatches(days);
        return ResponseEntity.ok(batches);
    }

    @GetMapping("/expiry-alerts")
    public ResponseEntity<ExpiryAlertResponse> getExpiryAlerts() {
        ExpiryAlertResponse alerts = batchService.getExpiryAlerts();
        return ResponseEntity.ok(alerts);
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportBatchData(
            @RequestParam(defaultValue = "csv") String format,
            @RequestParam(required = false) String ingredientName,
            @RequestParam(required = false) String storageLocation) {
        return batchService.exportBatchData(format, ingredientName, storageLocation);
    }

    @PostMapping("/use-for-menu")
    public ResponseEntity<MessageResponse> useForMenu(
            @Valid @RequestBody MenuUsageRequest request,
            Authentication authentication) {
        String userIdentifier = getUserIdentifierFromAuthentication(authentication);
        MessageResponse response = batchService.useIngredientForMenu(request, userIdentifier);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/use-manual")
    public ResponseEntity<MessageResponse> useManually(
            @Valid @RequestBody ManualUsageRequest request,
            Authentication authentication) {
        String userIdentifier = getUserIdentifierFromAuthentication(authentication);
        MessageResponse response = batchService.useIngredientManually(request, userIdentifier);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/status")
    public ResponseEntity<MessageResponse> updateBatchStatus(
            @Valid @RequestBody BatchStatusUpdateRequest request,
            Authentication authentication) {
        String userIdentifier = getUserIdentifierFromAuthentication(authentication);
        MessageResponse response = batchService.updateBatchStatus(request, userIdentifier);
        return ResponseEntity.ok(response);
    }

    private String getUserIdentifierFromAuthentication(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalArgumentException("Authentication is required");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserPrincipal) {
            UserPrincipal userPrincipal = (UserPrincipal) principal;
            String email = userPrincipal.getEmail();
            return email;
        }

        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            String username = userDetails.getUsername();
            return username;
        }

        String name = authentication.getName();
        return name;
    }

    @GetMapping("/{batchCode}/label")
    public ResponseEntity<BatchLabelResponse> generateLabel(@PathVariable String batchCode) {
        BatchLabelResponse label = batchService.generateBatchLabel(batchCode);
        return ResponseEntity.ok(label);
    }

    @GetMapping("/ingredients/summary")
    public ResponseEntity<List<IngredientSummaryResponse>> getAllIngredientsSummary() {
        List<IngredientSummaryResponse> ingredients = batchService.getAllIngredientsSummary();
        return ResponseEntity.ok(ingredients);
    }

    @GetMapping("/freshness-summary")
    public ResponseEntity<List<FreshnessStatusSummaryResponse>> getFreshnessStatusSummary() {
        List<FreshnessStatusSummaryResponse> summary = batchService.getFreshnessStatusSummary();
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/storage-summary")
    public ResponseEntity<List<StorageLocationSummaryResponse>> getStorageLocationSummary() {
        List<StorageLocationSummaryResponse> summary = batchService.getStorageLocationSummary();
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/freshness/green")
    public ResponseEntity<List<BatchResponse>> getGreenFreshnessBatches() {
        List<BatchResponse> batches = batchService.getBatchesByFreshnessStatus("GREEN");
        return ResponseEntity.ok(batches);
    }

    @GetMapping("/freshness/yellow")
    public ResponseEntity<List<BatchResponse>> getYellowFreshnessBatches() {
        List<BatchResponse> batches = batchService.getBatchesByFreshnessStatus("YELLOW");
        return ResponseEntity.ok(batches);
    }

    @GetMapping("/freshness/red")
    public ResponseEntity<List<BatchResponse>> getRedFreshnessBatches() {
        List<BatchResponse> batches = batchService.getBatchesByFreshnessStatus("RED");
        return ResponseEntity.ok(batches);
    }

    @GetMapping("/location/{storageLocation}")
    public ResponseEntity<List<BatchResponse>> getBatchesByStorageLocation(@PathVariable String storageLocation) {
        List<BatchResponse> batches = batchService.getBatchesByStorageLocation(storageLocation);
        return ResponseEntity.ok(batches);
    }
}