package com.example.kulkita.service;

import com.example.kulkita.constants.BusinessConstants;
import com.example.kulkita.dto.response.*;
import com.example.kulkita.entity.Batch;
import com.example.kulkita.repository.BatchRepository;
import com.example.kulkita.repository.BatchUsageHistoryRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private static final Logger log = LoggerFactory.getLogger(DashboardService.class);

    private final BatchRepository batchRepository;
    private final BatchUsageHistoryRepository usageHistoryRepository;
    private final FirebaseMessaging firebaseMessaging;

    public DashboardService(BatchRepository batchRepository,
                            BatchUsageHistoryRepository usageHistoryRepository,
                            @Autowired(required = false) FirebaseMessaging firebaseMessaging) {
        this.batchRepository = batchRepository;
        this.usageHistoryRepository = usageHistoryRepository;
        this.firebaseMessaging = firebaseMessaging;
    }

    public DashboardSummaryResponse getDashboardSummary() {
        long totalActiveBatches = batchRepository.countActiveBatches();
        Double totalActiveWeight = batchRepository.getTotalActiveWeight();

        List<Object[]> ingredientData = batchRepository.getIngredientSummary();
        List<IngredientSummaryDto> ingredientSummaries = ingredientData.stream()
                .map(data -> new IngredientSummaryDto(
                        (String) data[0],
                        (Long) data[1],
                        (BigDecimal) data[2]
                ))
                .collect(Collectors.toList());

        List<Object[]> locationData = batchRepository.getStorageLocationSummary();
        List<StorageLocationSummaryDto> locationSummaries = locationData.stream()
                .map(data -> new StorageLocationSummaryDto(
                        (String) data[0],
                        (Long) data[1]
                ))
                .collect(Collectors.toList());

        List<Object[]> freshnessData = batchRepository.getFreshnessStatusSummary();
        List<FreshnessStatusSummaryDto> freshnessSummaries = freshnessData.stream()
                .map(data -> new FreshnessStatusSummaryDto(
                        (Batch.FreshnessStatus) data[0],
                        (Long) data[1]
                ))
                .collect(Collectors.toList());

        return new DashboardSummaryResponse(
                totalActiveBatches,
                totalActiveWeight != null ? totalActiveWeight : 0.0,
                ingredientSummaries,
                locationSummaries,
                freshnessSummaries
        );
    }

    public ExpiryAlertResponse getExpiryAlerts() {
        Instant now = Instant.now();
        Instant tomorrow = now.plus(1, ChronoUnit.DAYS);
        Instant in2Days = now.plus(2, ChronoUnit.DAYS);
        Instant in3Days = now.plus(3, ChronoUnit.DAYS);

        List<BatchResponse> expiringToday = batchRepository.findExpiringBatches(now, tomorrow)
                .stream().map(this::convertToBatchResponse).collect(Collectors.toList());

        List<BatchResponse> expiringIn2Days = batchRepository.findExpiringBatches(tomorrow, in2Days)
                .stream().map(this::convertToBatchResponse).collect(Collectors.toList());

        List<BatchResponse> expiringIn3Days = batchRepository.findExpiringBatches(in2Days, in3Days)
                .stream().map(this::convertToBatchResponse).collect(Collectors.toList());

        return new ExpiryAlertResponse(expiringToday, expiringIn2Days, expiringIn3Days);
    }

    public UsageTrendResponse getUsageTrends(int days) {
        Instant endDate = Instant.now();
        Instant startDate = endDate.minus(days, ChronoUnit.DAYS);

        List<Object[]> dailyUsageData = usageHistoryRepository.getDailyUsageTrend(startDate, endDate);
        List<DailyUsageDto> dailyUsage = dailyUsageData.stream()
                .map(data -> new DailyUsageDto(
                        (String) data[0],
                        (Double) data[1]
                ))
                .collect(Collectors.toList());

        List<Object[]> popularMenuData = usageHistoryRepository.getPopularMenus(startDate, endDate);
        List<PopularMenuDto> popularMenus = popularMenuData.stream()
                .map(data -> new PopularMenuDto(
                        (String) data[0],
                        (Long) data[1],
                        (Long) data[2]
                ))
                .collect(Collectors.toList());

        return new UsageTrendResponse(dailyUsage, popularMenus);
    }

    public WasteAnalysisResponse getWasteAnalysis(int days) {
        Instant endDate = Instant.now();
        Instant startDate = endDate.minus(days, ChronoUnit.DAYS);

        List<Object[]> wasteData = usageHistoryRepository.getUsageByType(startDate, endDate);

        Map<String, Double> wasteByType = wasteData.stream()
                .collect(Collectors.toMap(
                        data -> (String) data[0],
                        data -> (Double) data[1]
                ));

        double totalWasteWeight = wasteByType.getOrDefault("WASTE", 0.0) +
                wasteByType.getOrDefault("EXPIRED", 0.0);

        double economicLoss = calculateEconomicLoss(totalWasteWeight);

        List<WasteCategoryDto> wasteCategories = wasteByType.entrySet().stream()
                .map(entry -> new WasteCategoryDto(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        return new WasteAnalysisResponse(totalWasteWeight, economicLoss, wasteCategories);
    }

    public void subscribeDeviceToNotifications(String deviceToken) {
        if (firebaseMessaging == null) {
            log.warn("Firebase not configured, cannot subscribe device");
            return;
        }

        try {
            firebaseMessaging.subscribeToTopic(List.of(deviceToken), "spoilage_alerts");
            log.info("Device subscribed to notifications: {}", deviceToken);
        } catch (FirebaseMessagingException e) {
            log.error("Failed to subscribe device to notifications: {}", e.getMessage());
            throw new RuntimeException("Failed to subscribe to notifications");
        }
    }

    public void unsubscribeDeviceFromNotifications(String deviceToken) {
        if (firebaseMessaging == null) {
            log.warn("Firebase not configured, cannot unsubscribe device");
            return;
        }

        try {
            firebaseMessaging.unsubscribeFromTopic(List.of(deviceToken), "spoilage_alerts");
            log.info("Device unsubscribed from notifications: {}", deviceToken);
        } catch (FirebaseMessagingException e) {
            log.error("Failed to unsubscribe device from notifications: {}", e.getMessage());
            throw new RuntimeException("Failed to unsubscribe from notifications");
        }
    }

    private double calculateEconomicLoss(double wasteWeight) {
        return wasteWeight * BusinessConstants.EconomicFactors.AVERAGE_FOOD_COST_PER_KG;
    }

    private BatchResponse convertToBatchResponse(Batch batch) {
        BatchResponse response = new BatchResponse();
        response.setId(batch.getId().toString());
        response.setBatchCode(batch.getBatchCode());
        response.setIngredientName(batch.getIngredientName());
        response.setCategory(batch.getCategory().name());
        response.setWeight(batch.getWeight());
        response.setUnit(batch.getUnit());
        response.setSource(batch.getSource());
        response.setEntryDate(batch.getEntryDate());
        response.setExpiryDate(batch.getExpiryDate());
        response.setStorageLocation(batch.getStorageLocation());
        response.setFreshnessStatus(batch.getFreshnessStatus());
        response.setActive(batch.isActive());
        response.setNotes(batch.getNotes());
        response.setCreatedAt(batch.getCreatedAt());
        response.setUpdatedAt(batch.getUpdatedAt());
        return response;
    }
    }


