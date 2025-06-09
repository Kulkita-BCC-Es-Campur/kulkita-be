package com.example.kulkita.service;

import com.example.kulkita.constants.BusinessConstants;
import com.example.kulkita.constants.StorageConstants;
import com.example.kulkita.dto.request.BatchCreateRequest;
import com.example.kulkita.dto.request.BatchStatusUpdateRequest;
import com.example.kulkita.dto.request.ManualUsageRequest;
import com.example.kulkita.dto.request.MenuUsageRequest;
import com.example.kulkita.dto.response.*;
import com.example.kulkita.entity.*;
import com.example.kulkita.exception.ResourceNotFoundException;
import com.example.kulkita.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BatchService {

    private static final Logger log = LoggerFactory.getLogger(BatchService.class);

    private final BatchRepository batchRepository;
    private final MenuRepository menuRepository;
    private final BatchUsageHistoryRepository usageHistoryRepository;
    private final UserRepository userRepository;
    private final SpoilagePredictionService predictionService;
    private final NotificationService notificationService;
    private final FifoService fifoService;
    private final UserLookupService userLookupService;


    public BatchService(BatchRepository batchRepository,
                        MenuRepository menuRepository,
                        BatchUsageHistoryRepository usageHistoryRepository,
                        UserRepository userRepository,
                        SpoilagePredictionService predictionService,
                        NotificationService notificationService,
                        FifoService fifoService, UserLookupService userLookupService) {
        this.batchRepository = batchRepository;
        this.menuRepository = menuRepository;
        this.usageHistoryRepository = usageHistoryRepository;
        this.userRepository = userRepository;
        this.predictionService = predictionService;
        this.notificationService = notificationService;
        this.fifoService = fifoService;
        this.userLookupService = userLookupService;
    }

    public List<IngredientSummaryResponse> getAllIngredientsSummary() {
        log.info("Getting ingredients summary with accumulated quantities");

        List<Object[]> ingredientData = batchRepository.getIngredientsSummary();

        return ingredientData.stream()
                .map(data -> {
                    String ingredientName = (String) data[0];
                    Long totalBatches = (Long) data[1];
                    BigDecimal totalWeight = (BigDecimal) data[2];
                    String unit = (String) data[3];
                    String category = (String) data[4];

                    List<Batch> batches = batchRepository.findBatchesByIngredientName(ingredientName);

                    long greenBatches = batches.stream()
                            .mapToLong(b -> b.getFreshnessStatus() == Batch.FreshnessStatus.GREEN ? 1 : 0)
                            .sum();

                    long yellowBatches = batches.stream()
                            .mapToLong(b -> b.getFreshnessStatus() == Batch.FreshnessStatus.YELLOW ? 1 : 0)
                            .sum();

                    long redBatches = batches.stream()
                            .mapToLong(b -> b.getFreshnessStatus() == Batch.FreshnessStatus.RED ? 1 : 0)
                            .sum();

                    return new IngredientSummaryResponse(
                            ingredientName, totalBatches, totalWeight, unit, category,
                            greenBatches, yellowBatches, redBatches
                    );
                })
                .collect(Collectors.toList());
    }

    public List<FreshnessStatusSummaryResponse> getFreshnessStatusSummary() {
        log.info("Getting freshness status summary");

        List<Object[]> freshnessData = batchRepository.getFreshnessStatusSummaryData();
        long totalBatches = batchRepository.countActiveBatches();

        return freshnessData.stream()
                .map(data -> {
                    String status = data[0].toString();
                    Long count = (Long) data[1];
                    Double weight = (Double) data[2];
                    double percentage = totalBatches > 0 ? (double) count / totalBatches * 100 : 0;

                    String statusName = switch (status) {
                        case "GREEN" -> "Aman";
                        case "YELLOW" -> "Waspada";
                        case "RED" -> "Krisis";
                        default -> status;
                    };

                    return new FreshnessStatusSummaryResponse(
                            status, statusName, count, weight, Math.round(percentage * 100.0) / 100.0);
                })
                .collect(Collectors.toList());
    }

    public List<StorageLocationSummaryResponse> getStorageLocationSummary() {
        log.info("Getting storage location summary");

        List<Object[]> locationData = batchRepository.getStorageLocationSummaryData();

        return locationData.stream()
                .map(data -> {
                    String storageLocation = (String) data[0];
                    Long batchCount = (Long) data[1];
                    Double totalWeight = (Double) data[2];

                    List<Batch> batches = batchRepository.findBatchesByStorageLocationDetailed(storageLocation);

                    long greenBatches = batches.stream()
                            .mapToLong(b -> b.getFreshnessStatus() == Batch.FreshnessStatus.GREEN ? 1 : 0)
                            .sum();

                    long yellowBatches = batches.stream()
                            .mapToLong(b -> b.getFreshnessStatus() == Batch.FreshnessStatus.YELLOW ? 1 : 0)
                            .sum();

                    long redBatches = batches.stream()
                            .mapToLong(b -> b.getFreshnessStatus() == Batch.FreshnessStatus.RED ? 1 : 0)
                            .sum();

                    return new StorageLocationSummaryResponse(
                            storageLocation, batchCount, totalWeight,
                            greenBatches, yellowBatches, redBatches
                    );
                })
                .collect(Collectors.toList());
    }

    public List<BatchResponse> getBatchesByFreshnessStatus(String freshnessStatus) {
        log.info("Getting batches with freshness status: {}", freshnessStatus);

        try {
            Batch.FreshnessStatus status = Batch.FreshnessStatus.valueOf(freshnessStatus.toUpperCase());

            List<Batch> batches = batchRepository.findByFreshnessStatusAndActiveTrue(status);

            return batches.stream()
                    .map(this::convertToBatchResponse)
                    .collect(Collectors.toList());

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid freshness status: " + freshnessStatus +
                    ". Valid values are: GREEN, YELLOW, RED");
        }
    }

    public List<BatchResponse> getBatchesByStorageLocation(String storageLocation) {
        log.info("Getting batches for storage location: {}", storageLocation);

        List<Batch> batches = batchRepository.findByStorageLocationAndActiveTrue(storageLocation);

        return batches.stream()
                .map(this::convertToBatchResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public BatchResponse createBatch(BatchCreateRequest request) {
        log.info("Creating new batch for ingredient: {}", request.getIngredientName());

        String batchCode = generateBatchCode(request.getIngredientName(), request.getStorageLocation());

        Batch batch = new Batch();
        batch.setBatchCode(batchCode);
        batch.setIngredientName(request.getIngredientName());
        batch.setCategory(Batch.Category.valueOf(determineCategoryIfEmpty(request.getCategory(), request.getIngredientName()).toUpperCase()));
        batch.setWeight(request.getWeight());
        batch.setUnit(request.getUnit());
        batch.setSource(request.getSource());
        batch.setStorageLocation(request.getStorageLocation());
        batch.setNotes(request.getNotes());
        batch.setActive(true);

        SpoilagePredictionService.PredictionResult prediction = predictionService.predictSpoilage(
                request.getIngredientName(),
                request.getStorageLocation(),
                Instant.now(),
                null
        );

        batch.setExpiryDate(prediction.getExpiryDate());
        batch.setFreshnessStatus(prediction.getFreshnessStatus());

        batch = batchRepository.save(batch);

        notificationService.sendBatchCreatedNotification(batch);

        log.info("Batch created successfully with code: {}", batchCode);
        return convertToBatchResponse(batch);
    }

    public List<String> getAllCategories() {
        List<String> categories = batchRepository.findAllCategories();

        if (categories.isEmpty()) {
            return Arrays.asList("Sayuran", "Buah", "Protein", "Karbohidrat", "Bumbu", "Lainnya");
        }

        return categories;
    }

    public List<String> getAllStorageLocations() {
        return batchRepository.findAllStorageLocations();
    }

    public List<String> getIngredientNamesForAutocomplete(String query) {
        if (query == null || query.trim().isEmpty()) {
            return batchRepository.findAllIngredientNames().stream().limit(10).collect(Collectors.toList());
        }
        return batchRepository.findIngredientNamesForAutocomplete(query.trim());
    }

    public ResponseEntity<byte[]> exportBatchData(String format, String ingredientName, String storageLocation) {
        log.info("Exporting batch data in format: {}", format);

        List<Batch> batches;
        if (ingredientName != null && !ingredientName.isEmpty()) {
            batches = batchRepository.findByIngredientNameAndActiveTrueOrderByEntryDateAsc(ingredientName);
        } else if (storageLocation != null && !storageLocation.isEmpty()) {
            batches = batchRepository.findByStorageLocationAndActiveTrueOrderByEntryDateAsc(storageLocation);
        } else {
            batches = batchRepository.findByActiveTrueOrderByEntryDateAsc();
        }

        if ("csv".equalsIgnoreCase(format)) {
            return exportToCsv(batches);
        } else if ("excel".equalsIgnoreCase(format)) {
            return exportToExcel(batches);
        } else {
            throw new IllegalArgumentException("Unsupported export format: " + format);
        }
    }

    private ResponseEntity<byte[]> exportToCsv(List<Batch> batches) {
        StringBuilder csv = new StringBuilder();
        csv.append("Batch Code,Ingredient Name,Weight,Unit,Source,Entry Date,Expiry Date,Storage Location,Freshness Status,Active,Notes\n");

        for (Batch batch : batches) {
            csv.append(String.format("%s,%s,%.3f,%s,%s,%s,%s,%s,%s,%s,\"%s\"\n",
                    batch.getBatchCode(),
                    batch.getIngredientName(),
                    batch.getWeight(),
                    batch.getUnit(),
                    batch.getSource(),
                    batch.getEntryDate(),
                    batch.getExpiryDate() != null ? batch.getExpiryDate() : "",
                    batch.getStorageLocation(),
                    batch.getFreshnessStatus(),
                    batch.isActive(),
                    batch.getNotes() != null ? batch.getNotes().replace("\"", "\"\"") : ""
            ));
        }

        byte[] data = csv.toString().getBytes(StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "batch_data.csv");

        return ResponseEntity.ok()
                .headers(headers)
                .body(data);
    }

    private ResponseEntity<byte[]> exportToExcel(List<Batch> batches) {
        log.info("Excel export requested for {} batches - implementing simple format", batches.size());

        StringBuilder excel = new StringBuilder();
        excel.append("Batch Data Export\n");
        excel.append("Generated on: ").append(Instant.now()).append("\n\n");
        excel.append("Batch Code\tIngredient Name\tWeight\tUnit\tSource\tEntry Date\tExpiry Date\tStorage Location\tFreshness Status\tActive\tNotes\n");

        for (Batch batch : batches) {
            excel.append(String.format("%s\t%s\t%.3f\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\n",
                    batch.getBatchCode(),
                    batch.getIngredientName(),
                    batch.getWeight(),
                    batch.getUnit(),
                    batch.getSource(),
                    batch.getEntryDate(),
                    batch.getExpiryDate() != null ? batch.getExpiryDate() : "",
                    batch.getStorageLocation(),
                    batch.getFreshnessStatus(),
                    batch.isActive(),
                    batch.getNotes() != null ? batch.getNotes() : ""
            ));
        }

        byte[] data = excel.toString().getBytes(StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "batch_data.xls");

        return ResponseEntity.ok()
                .headers(headers)
                .body(data);
    }

    @Transactional
    public MessageResponse useIngredientForMenu(MenuUsageRequest request, String userEmail) {
        log.info("Processing menu usage for menu ID: {} with {} portions", request.getMenuId(), request.getPortionCount());

        User user = userLookupService.findByEmail(userEmail);

        Menu menu = menuRepository.findById(UUID.fromString(request.getMenuId()))
                .orElseThrow(() -> new ResourceNotFoundException("Menu not found"));

        for (MenuIngredient menuIngredient : menu.getIngredients()) {
            BigDecimal totalNeeded = menuIngredient.getWeightPerPortion()
                    .multiply(BigDecimal.valueOf(request.getPortionCount()));

            processIngredientUsage(menuIngredient.getIngredientName(), totalNeeded, user, menu.getName(), request.getPortionCount(), request.getNotes());
        }

        return new MessageResponse("Menu ingredients used successfully following FIFO principle");
    }

    @Transactional
    public MessageResponse useIngredientManually(ManualUsageRequest request, String userEmail) {
        log.info("Processing manual usage for batch: {}", request.getBatchCode());

        User user = userLookupService.findByEmail(userEmail);

        Batch batch = batchRepository.findByBatchCode(request.getBatchCode())
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found with code: " + request.getBatchCode()));

        if (!batch.isActive()) {
            throw new IllegalArgumentException("Batch is not active");
        }

        if (batch.getWeight().compareTo(request.getUsedWeight()) < 0) {
            throw new IllegalArgumentException("Insufficient stock. Available: " + batch.getWeight() + " " + batch.getUnit());
        }

        BigDecimal newWeight = batch.getWeight().subtract(request.getUsedWeight());
        batch.setWeight(newWeight);

        if (newWeight.compareTo(BigDecimal.ZERO) == 0) {
            batch.setActive(false);
        }

        batchRepository.save(batch);

        BatchUsageHistory usage = new BatchUsageHistory();
        usage.setBatch(batch);
        usage.setUser(user);
        usage.setUsedWeight(request.getUsedWeight());
        usage.setUsageType(BatchUsageHistory.UsageType.MANUAL_USE);
        usage.setNotes(request.getNotes());
        usageHistoryRepository.save(usage);

        log.info("Manual usage recorded successfully for batch: {}", request.getBatchCode());
        return new MessageResponse("Ingredient used successfully");
    }

    @Transactional
    public MessageResponse updateBatchStatus(BatchStatusUpdateRequest request, String userEmail) {
        log.info("Updating batch status for: {} to {}", request.getBatchCode(), request.getStatus());

        User user = userLookupService.findByEmail(userEmail);

        Batch batch = batchRepository.findByBatchCode(request.getBatchCode())
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found with code: " + request.getBatchCode()));

        Batch.FreshnessStatus oldStatus = batch.getFreshnessStatus();

        batch.setActive(false);

        if ("EXPIRED".equalsIgnoreCase(request.getStatus())) {
            batch.setFreshnessStatus(Batch.FreshnessStatus.RED);
        } else if ("DAMAGED".equalsIgnoreCase(request.getStatus()) || "WASTE".equalsIgnoreCase(request.getStatus())) {
            batch.setFreshnessStatus(Batch.FreshnessStatus.RED);
        }

        batchRepository.save(batch);

        BatchUsageHistory usage = new BatchUsageHistory();
        usage.setBatch(batch);
        usage.setUser(user);
        usage.setUsedWeight(batch.getWeight());
        usage.setUsageType(mapStatusToUsageType(request.getStatus()));
        usage.setNotes(request.getNotes());
        usageHistoryRepository.save(usage);

        if (oldStatus != batch.getFreshnessStatus()) {
            notificationService.sendFreshnessStatusAlert(batch, oldStatus, batch.getFreshnessStatus());
        }

        log.info("Batch status updated successfully for: {}", request.getBatchCode());
        return new MessageResponse("Batch status updated successfully");
    }

    public List<BatchResponse> getActiveBatches() {
        return batchRepository.findByActiveTrueOrderByEntryDateAsc()
                .stream()
                .map(this::convertToBatchResponse)
                .collect(Collectors.toList());
    }

    public List<BatchResponse> getExpiringBatches(int days) {
        Instant now = Instant.now();
        Instant futureDate = now.plus(days, ChronoUnit.DAYS);

        return batchRepository.findExpiringBatches(now, futureDate)
                .stream()
                .map(this::convertToBatchResponse)
                .collect(Collectors.toList());
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

        ExpiryAlertResponse response = new ExpiryAlertResponse();
        response.setExpiringToday(expiringToday);
        response.setExpiringIn2Days(expiringIn2Days);
        response.setExpiringIn3Days(expiringIn3Days);
        return response;
    }

    public BatchLabelResponse generateBatchLabel(String batchCode) {
        Batch batch = batchRepository.findByBatchCode(batchCode)
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found with code: " + batchCode));

        BatchLabelResponse label = new BatchLabelResponse();
        label.setBatchCode(batch.getBatchCode());
        label.setIngredientName(batch.getIngredientName());
        label.setWeight(batch.getWeight());
        label.setUnit(batch.getUnit());
        label.setEntryDate(batch.getEntryDate());
        label.setExpiryDate(batch.getExpiryDate());
        label.setStorageLocation(batch.getStorageLocation());
        label.setFreshnessStatus(batch.getFreshnessStatus());
        label.setQrCode("");

        return label;
    }

    private void processIngredientUsage(String ingredientName, BigDecimal totalNeeded, User user, String menuName, Integer portionCount, String notes) {
        log.info("Processing ingredient usage with FIFO optimization for: {} ({})", ingredientName, totalNeeded);

        FifoPickingRecommendationResponse fifoRecommendation = fifoService.getPickingRecommendation(
                ingredientName, totalNeeded, "kg");

        if (!fifoRecommendation.isCanFulfill()) {
            log.error("Cannot fulfill requirement for {}: {}", ingredientName, fifoRecommendation.getMessage());
            throw new IllegalArgumentException(fifoRecommendation.getMessage());
        }

        for (PickingInstructionDto instruction : fifoRecommendation.getPickingInstructions()) {
            Batch batch = batchRepository.findByBatchCode(instruction.getBatchCode())
                    .orElseThrow(() -> new ResourceNotFoundException("Batch not found: " + instruction.getBatchCode()));

            BigDecimal toUse = instruction.getAmountToUse();

            BigDecimal newWeight = batch.getWeight().subtract(toUse);
            batch.setWeight(newWeight);

            if (newWeight.compareTo(BigDecimal.ZERO) == 0) {
                batch.setActive(false);
                log.info("Batch {} exhausted and marked inactive", batch.getBatchCode());
            }

            batchRepository.save(batch);

            BatchUsageHistory usage = new BatchUsageHistory();
            usage.setBatch(batch);
            usage.setUser(user);
            usage.setUsedWeight(toUse);
            usage.setUsageType(BatchUsageHistory.UsageType.MENU_COOKING);
            usage.setMenuName(menuName);
            usage.setPortionCount(portionCount);
            usage.setNotes(notes + " | FIFO Priority: " + instruction.getUrgencyLevel());
            usageHistoryRepository.save(usage);

            log.info("Used {} {} from batch {} (FIFO: {})",
                    toUse, batch.getUnit(), batch.getBatchCode(), instruction.getUrgencyLevel());
        }

        checkLowStock(ingredientName);
    }

    private void checkLowStock(String ingredientName) {
        List<Batch> activeBatches = batchRepository.findByIngredientNameAndActiveTrueOrderByEntryDateAsc(ingredientName);

        double totalWeight = activeBatches.stream()
                .mapToDouble(batch -> batch.getWeight().doubleValue())
                .sum();

        if (totalWeight < BusinessConstants.StockThresholds.LOW_STOCK_THRESHOLD_KG && totalWeight > 0) {
            notificationService.sendLowStockNotification(ingredientName, totalWeight);
        }
    }

    private String generateBatchCode(String ingredientName, String storageLocation) {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String locationCode = getLocationCode(storageLocation);
        String ingredientCode = ingredientName.substring(0,
                Math.min(BusinessConstants.ValidationConstraints.BATCH_CODE_MIN_INGREDIENT_LENGTH,
                        ingredientName.length())).toUpperCase();

        String prefix = ingredientCode + "-" + today + "-" + locationCode + "-";

        Integer lastSerial = batchRepository.getLastSerialNumber(prefix);
        int newSerial = (lastSerial != null ? lastSerial : 0) + 1;

        return prefix + String.format("%0" + BusinessConstants.ValidationConstraints.BATCH_CODE_SERIAL_LENGTH + "d", newSerial);
    }

    private String getLocationCode(String storageLocation) {
        return switch (storageLocation.toLowerCase()) {
            case StorageConstants.LocationNames.REFRIGERATOR,
                 StorageConstants.LocationNames.REFRIGERATOR_EN ->
                    StorageConstants.LocationCodes.REFRIGERATOR_CODE;
            case StorageConstants.LocationNames.FREEZER ->
                    StorageConstants.LocationCodes.FREEZER_CODE;
            case StorageConstants.LocationNames.PANTRY,
                 StorageConstants.LocationNames.PANTRY_EN ->
                    StorageConstants.LocationCodes.PANTRY_CODE;
            default -> StorageConstants.LocationCodes.OTHER_CODE;
        };
    }

    private BatchUsageHistory.UsageType mapStatusToUsageType(String status) {
        return switch (status.toUpperCase()) {
            case "WASTE", "DAMAGED" -> BatchUsageHistory.UsageType.WASTE;
            case "EXPIRED" -> BatchUsageHistory.UsageType.EXPIRED;
            default -> BatchUsageHistory.UsageType.WASTE;
        };
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

    private String determineCategoryIfEmpty(String providedCategory, String ingredientName) {
        if (providedCategory != null && !providedCategory.trim().isEmpty()) {
            return providedCategory.trim();
        }

        String lowerName = ingredientName.toLowerCase();

        if (lowerName.matches(".*(bayam|kangkung|sawi|selada|tomat|timun|wortel|kentang|bawang).*")) {
            return "Sayuran";
        } else if (lowerName.matches(".*(pisang|apel|jeruk|mangga|pepaya|semangka|anggur|strawberry).*")) {
            return "Buah";
        } else if (lowerName.matches(".*(ayam|daging|ikan|udang|telur|tahu|tempe).*")) {
            return "Protein";
        } else if (lowerName.matches(".*(beras|tepung|gula|garam|minyak|roti|pasta).*")) {
            return "Karbohidrat";
        } else if (lowerName.matches(".*(garam|merica|kunyit|jahe|bawang putih|cabai|bumbu).*")) {
            return "Bumbu";
        } else {
            return "Lainnya";
        }
    }

    public List<BatchResponse> getBatchesByCategory(String category) {
        log.info("Getting batches for category: {}", category);

        List<Batch> batches = batchRepository.findByCategoryAndActiveTrueOrderByEntryDateAsc(category);

        return batches.stream()
                .map(this::convertToBatchResponse)
                .collect(Collectors.toList());
    }

    public List<CategorySummaryResponse> getCategorySummary() {
        log.info("Getting category summary");

        List<Object[]> categoryData = batchRepository.getCategorySummary();

        return categoryData.stream()
                .map(data -> new CategorySummaryResponse(
                        (String) data[0],
                        (Long) data[1],
                        (BigDecimal) data[2],
                        "kg"
                ))
                .collect(Collectors.toList());
    }

    public List<BatchResponse> getBatchesWithAdvancedFilter(String ingredientName, String category,
                                                            String storageLocation, String freshnessStatus,
                                                            String startDate, String endDate, String sortBy) {
        log.info("Getting batches with advanced filter - ingredient: {}, category: {}, location: {}",
                ingredientName, category, storageLocation);

        Batch.FreshnessStatus status = null;
        if (freshnessStatus != null && !freshnessStatus.isEmpty()) {
            try {
                status = Batch.FreshnessStatus.valueOf(freshnessStatus.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid freshness status: {}", freshnessStatus);
            }
        }

        if (sortBy == null || sortBy.isEmpty()) {
            sortBy = "entryDate";
        }

        List<Batch> batches = batchRepository.findBatchesWithAdvancedFilter(
                ingredientName, category, storageLocation, status, sortBy);

        return batches.stream()
                .map(this::convertToBatchResponse)
                .collect(Collectors.toList());
    }

    public List<FifoBatchResponse> getActiveBatchesWithFifo() {
        return fifoService.getBatchesByFifoPriority(null, null);
    }

    public FifoPickingRecommendationResponse getPickingRecommendationForMenu(String menuId, Integer portionCount) {
        Menu menu = menuRepository.findById(UUID.fromString(menuId))
                .orElseThrow(() -> new ResourceNotFoundException("Menu not found"));

        List<FifoPickingRecommendationResponse> recommendations = new ArrayList<>();

        for (MenuIngredient ingredient : menu.getIngredients()) {
            BigDecimal totalNeeded = ingredient.getWeightPerPortion().multiply(BigDecimal.valueOf(portionCount));

            FifoPickingRecommendationResponse recommendation = fifoService.getPickingRecommendation(
                    ingredient.getIngredientName(), totalNeeded, ingredient.getUnit());

            recommendations.add(recommendation);
        }

        return recommendations.isEmpty() ? null : recommendations.get(0);
    }


}