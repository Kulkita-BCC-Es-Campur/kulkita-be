package com.example.kulkita.service;

import com.example.kulkita.constants.BusinessConstants;
import com.example.kulkita.constants.IngredientConstants;
import com.example.kulkita.constants.StorageConstants;
import com.example.kulkita.dto.request.AzureMLPredictionRequest;
import com.example.kulkita.dto.response.AzureMLPredictionResponse;
import com.example.kulkita.entity.Batch;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class SpoilagePredictionService {

    private static final Logger log = LoggerFactory.getLogger(SpoilagePredictionService.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${azure.ml.endpoint}")
    private String azureMLEndpoint;

    @Value("${azure.ml.api.key}")
    private String azureMLApiKey;

    @Value("${azure.ml.enabled:true}")
    private boolean azureMLEnabled;

    private static final Map<String, Integer> FALLBACK_SHELF_LIFE = new HashMap<>();

    static {
        FALLBACK_SHELF_LIFE.put("bayam", IngredientConstants.DefaultShelfLife.BAYAM_DAYS);
        FALLBACK_SHELF_LIFE.put("kangkung", IngredientConstants.DefaultShelfLife.KANGKUNG_DAYS);
        FALLBACK_SHELF_LIFE.put("sawi",    IngredientConstants.DefaultShelfLife.SAWI_DAYS);
        FALLBACK_SHELF_LIFE.put("tomat", IngredientConstants.DefaultShelfLife.TOMAT_DAYS);
        FALLBACK_SHELF_LIFE.put("wortel", IngredientConstants.DefaultShelfLife.WORTEL_DAYS);
        FALLBACK_SHELF_LIFE.put("kentang", IngredientConstants.DefaultShelfLife.KENTANG_DAYS);
        FALLBACK_SHELF_LIFE.put("bawang merah", IngredientConstants.DefaultShelfLife.BAWANG_MERAH_DAYS);
        FALLBACK_SHELF_LIFE.put("pisang", IngredientConstants.DefaultShelfLife.PISANG_DAYS);
        FALLBACK_SHELF_LIFE.put("apel", IngredientConstants.DefaultShelfLife.APEL_DAYS);
        FALLBACK_SHELF_LIFE.put("jeruk", IngredientConstants.DefaultShelfLife.JERUK_DAYS);
        FALLBACK_SHELF_LIFE.put("mangga", IngredientConstants.DefaultShelfLife.MANGGA_DAYS);
        FALLBACK_SHELF_LIFE.put("pepaya", IngredientConstants.DefaultShelfLife.PEPAYA_DAYS);
        FALLBACK_SHELF_LIFE.put("semangka", IngredientConstants.DefaultShelfLife.SEMANGKA_DAYS);
        FALLBACK_SHELF_LIFE.put("ayam", IngredientConstants.DefaultShelfLife.AYAM_DAYS);
        FALLBACK_SHELF_LIFE.put("daging sapi", IngredientConstants.DefaultShelfLife.DAGING_SAPI_DAYS);
        FALLBACK_SHELF_LIFE.put("ikan", IngredientConstants.DefaultShelfLife.IKAN_DAYS);
        FALLBACK_SHELF_LIFE.put("telur", IngredientConstants.DefaultShelfLife.TELUR_DAYS);
        FALLBACK_SHELF_LIFE.put("tahu", IngredientConstants.DefaultShelfLife.TAHU_DAYS);
        FALLBACK_SHELF_LIFE.put("tempe", IngredientConstants.DefaultShelfLife.TEMPE_DAYS);
        FALLBACK_SHELF_LIFE.put("default", IngredientConstants.DefaultShelfLife.DEFAULT_DAYS);
    }

    public SpoilagePredictionService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
    }

    public PredictionResult predictSpoilage(String ingredientName, String storageLocation,
                                            Instant entryDate, Double temperature) {
        log.info("🤖 Starting Azure ML spoilage prediction for: {} in {}", ingredientName, storageLocation);

        if (!azureMLEnabled) {
            log.warn("Azure ML is disabled, using fallback prediction");
            return getFallbackPrediction(ingredientName, storageLocation, entryDate);
        }

        if (azureMLApiKey == null || azureMLApiKey.trim().isEmpty()) {
            log.error("Azure ML API key not configured, using fallback");
            return getFallbackPrediction(ingredientName, storageLocation, entryDate);
        }

        try {
            List<Double> predictions = callAzureMLEndpointFixed(ingredientName, storageLocation, entryDate, temperature);

            if (predictions != null && !predictions.isEmpty()) {
                Double predictedDays = predictions.get(0);

                if (predictedDays != null && predictedDays > 0) {
                    Instant expiryDate = entryDate.plus(predictedDays.longValue(), ChronoUnit.DAYS);
                    Batch.FreshnessStatus status = calculateFreshnessStatus(entryDate, expiryDate);

                    log.info("Azure ML prediction successful: {} days until expiry", predictedDays);
                    return new PredictionResult(expiryDate, status, predictedDays.intValue());
                }
            }

            log.warn("Azure ML returned invalid response, using fallback");
            return getFallbackPrediction(ingredientName, storageLocation, entryDate);

        } catch (Exception e) {
            log.error("Error calling Azure ML endpoint: {}", e.getMessage());
            log.info("Falling back to rule-based prediction");
            return getFallbackPrediction(ingredientName, storageLocation, entryDate);
        }
    }

    private List<Double> callAzureMLEndpointFixed(String ingredientName, String storageLocation,
                                                  Instant entryDate, Double temperature) throws JsonProcessingException {
        try {
            log.info("Calling Azure ML endpoint: {}", azureMLEndpoint);

            AzureMLPredictionRequest request = createCorrectFormatRequest(ingredientName, storageLocation, temperature);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + azureMLApiKey.trim());
            headers.set("accept", "application/json");

            HttpEntity<AzureMLPredictionRequest> entity = new HttpEntity<>(request, headers);

            log.info("Sending request to Azure ML...");
            log.debug("Request payload: {}", request);

            ResponseEntity<String> response;

            try {
                response = restTemplate.postForEntity(azureMLEndpoint, entity, String.class);
            } catch (HttpClientErrorException.Unauthorized e) {
                log.error("AUTHENTICATION FAILED (401 Unauthorized)");
                log.error("API Key might be invalid or expired");
                log.error("Response: {}", e.getResponseBodyAsString());
                throw new RuntimeException("Azure ML authentication failed", e);
            } catch (HttpClientErrorException e) {
                log.error("HTTP Error: {} {}", e.getStatusCode(), e.getStatusText());
                log.error("Response: {}", e.getResponseBodyAsString());
                throw new RuntimeException("Azure ML request failed: " + e.getMessage(), e);
            }

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String responseBody = response.getBody();
                log.info("Successfully received response from Azure ML");
                log.info("Raw response: {}", responseBody);

                try {
                    List<Double> predictions = objectMapper.readValue(responseBody, new TypeReference<List<Double>>() {});
                    log.info("🔍 Parsed predictions: {}", predictions);
                    return predictions;

                } catch (Exception parseException) {
                    log.error("Failed to parse Azure ML response: {}", parseException.getMessage());
                    log.error("Response body: {}", responseBody);

                    try {
                        Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);
                        log.info("Response as map: {}", responseMap);

                        if (responseMap.containsKey("result")) {
                            @SuppressWarnings("unchecked")
                            List<Double> result = (List<Double>) responseMap.get("result");
                            return result;
                        }

                        if (responseMap.containsKey("predictions")) {
                            @SuppressWarnings("unchecked")
                            List<Double> result = (List<Double>) responseMap.get("predictions");
                            return result;
                        }

                    } catch (Exception altParseException) {
                        log.error("Alternative parsing also failed: {}", altParseException.getMessage());
                    }

                    throw parseException;
                }
            } else {
                log.warn("Azure ML returned non-success status: {}", response.getStatusCode());
                return null;
            }

        } catch (Exception e) {
            log.error("Exception calling Azure ML: {}", e.getMessage(), e);
            throw e;
        }
    }

    private AzureMLPredictionRequest createCorrectFormatRequest(String ingredientName, String storageLocation, Double temperature) {
        AzureMLPredictionRequest request = new AzureMLPredictionRequest();
        AzureMLPredictionRequest.InputData inputData = new AzureMLPredictionRequest.InputData();

        List<String> columns = Arrays.asList(
                "kuantitas_masuk",
                "suhu_penyimpanan_celsius_simulasi",
                "kategori_bahan"
        );

        double kuantitasMasuk = 1.0;
        double suhuPenyimpanan = temperature != null ? temperature : getDefaultTemperature(storageLocation);
        String kategoriBahan = mapToKategoriBahan(ingredientName);

        List<Object> dataRow = Arrays.asList(kuantitasMasuk, suhuPenyimpanan, kategoriBahan);

        inputData.setColumns(columns);
        inputData.setIndex(Arrays.asList(0));
        inputData.setData(Arrays.asList(dataRow));

        request.setInputData(inputData);

        log.info("Azure ML request created:");
        log.info("Kuantitas Masuk: {}", kuantitasMasuk);
        log.info("Suhu Penyimpanan: {}°C", suhuPenyimpanan);
        log.info("Kategori Bahan: {}", kategoriBahan);

        return request;
    }

    private String mapToKategoriBahan(String ingredientName) {
        String lower = ingredientName.toLowerCase();

        if (lower.matches(".*(tomat|bayam|kangkung|sawi|wortel|kentang|bawang|timun|kubis|terong).*")) {
            return "Sayuran";
        } else if (lower.matches(".*(pisang|apel|jeruk|mangga|pepaya|semangka|melon|anggur).*")) {
            return "Buah";
        } else if (lower.matches(".*(ayam|daging|ikan|udang|telur).*")) {
            return "Protein Hewani";
        } else if (lower.matches(".*(tahu|tempe|kacang).*")) {
            return "Protein Nabati";
        } else if (lower.matches(".*(beras|tepung|mie|roti).*")) {
            return "Karbohidrat";
        } else {
            return "Sayuran";
        }
    }

    private Double getDefaultTemperature(String storageLocation) {
        return switch (storageLocation.toLowerCase()) {
            case StorageConstants.LocationNames.REFRIGERATOR,
                 StorageConstants.LocationNames.REFRIGERATOR_EN ->
                    BusinessConstants.StorageTemperatures.REFRIGERATOR_TEMPERATURE;
            case StorageConstants.LocationNames.FREEZER ->
                    BusinessConstants.StorageTemperatures.FREEZER_TEMPERATURE;
            case StorageConstants.LocationNames.PANTRY,
                 StorageConstants.LocationNames.PANTRY_EN ->
                    BusinessConstants.StorageTemperatures.PANTRY_TEMPERATURE;
            default -> BusinessConstants.StorageTemperatures.ROOM_TEMPERATURE;
        };
    }

    private PredictionResult getFallbackPrediction(String ingredientName, String storageLocation, Instant entryDate) {
        log.info("Using fallback prediction for: {}", ingredientName);

        String normalizedName = ingredientName.toLowerCase().trim();
        int baseDays = FALLBACK_SHELF_LIFE.getOrDefault(normalizedName, FALLBACK_SHELF_LIFE.get("default"));

        double locationMultiplier = getStorageLocationMultiplier(storageLocation);
        int adjustedDays = (int) Math.round(baseDays * locationMultiplier);

        Instant expiryDate = entryDate.plus(adjustedDays, ChronoUnit.DAYS);
        Batch.FreshnessStatus status = calculateFreshnessStatus(entryDate, expiryDate);

        log.info("Fallback prediction: {} days (base: {}, multiplier: {})",
                adjustedDays, baseDays, locationMultiplier);

        return new PredictionResult(expiryDate, status, adjustedDays);
    }

    private double getStorageLocationMultiplier(String storageLocation) {
        return switch (storageLocation.toLowerCase()) {
            case StorageConstants.LocationNames.FREEZER ->
                    BusinessConstants.EconomicFactors.FREEZER_SHELF_LIFE_MULTIPLIER;
            case StorageConstants.LocationNames.REFRIGERATOR,
                 StorageConstants.LocationNames.REFRIGERATOR_EN ->
                    BusinessConstants.EconomicFactors.REFRIGERATOR_SHELF_LIFE_MULTIPLIER;
            case StorageConstants.LocationNames.PANTRY,
                 StorageConstants.LocationNames.PANTRY_EN ->
                    BusinessConstants.EconomicFactors.PANTRY_SHELF_LIFE_MULTIPLIER;
            default -> BusinessConstants.EconomicFactors.PANTRY_SHELF_LIFE_MULTIPLIER;
        };
    }

    public Batch.FreshnessStatus calculateFreshnessStatus(Instant entryDate, Instant expiryDate) {
        Instant now = Instant.now();

        if (expiryDate.isBefore(now)) {
            return Batch.FreshnessStatus.RED;
        }

        long totalShelfLife = ChronoUnit.DAYS.between(entryDate, expiryDate);
        long remainingDays = ChronoUnit.DAYS.between(now, expiryDate);

        if (totalShelfLife <= 0) {
            return Batch.FreshnessStatus.RED;
        }

        double remainingPercentage = (double) remainingDays / totalShelfLife;

        if (remainingPercentage > BusinessConstants.FreshnessPeriods.GREEN_FRESHNESS_THRESHOLD) {
            return Batch.FreshnessStatus.GREEN;
        } else if (remainingPercentage > BusinessConstants.FreshnessPeriods.YELLOW_FRESHNESS_THRESHOLD) {
            return Batch.FreshnessStatus.YELLOW;
        } else {
            return Batch.FreshnessStatus.RED;
        }
    }

    public void updateFreshnessStatus(Batch batch) {
        if (batch.getExpiryDate() != null) {
            Batch.FreshnessStatus newStatus = calculateFreshnessStatus(batch.getEntryDate(), batch.getExpiryDate());
            batch.setFreshnessStatus(newStatus);
        }
    }

    public boolean testAzureMLConnection() {
        if (!azureMLEnabled) {
            log.warn("Azure ML is disabled in configuration");
            return false;
        }

        try {
            log.info("Testing Azure ML connection...");

            List<Double> predictions = callAzureMLEndpointFixed("tomat", "kulkas", Instant.now(), 4.0);

            boolean isHealthy = predictions != null && !predictions.isEmpty();

            if (isHealthy) {
                log.info("Azure ML connection test: SUCCESS");
                log.info("Sample prediction result: {}", predictions);
            } else {
                log.error("Azure ML connection test: FAILED - Invalid response");
            }

            return isHealthy;

        } catch (Exception e) {
            log.error("Azure ML connection test failed: {}", e.getMessage());
            return false;
        }
    }

    public Map<String, Object> getServiceStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("enabled", azureMLEnabled);
        status.put("endpoint", azureMLEndpoint);
        status.put("apiKeyConfigured", azureMLApiKey != null && !azureMLApiKey.trim().isEmpty());
        status.put("lastTestTime", Instant.now());

        try {
            boolean isHealthy = testAzureMLConnection();
            status.put("connectionHealthy", isHealthy);
            status.put("authenticationWorking", isHealthy);
            if (isHealthy) {
                status.put("message", "Azure ML service is working correctly");
            }
        } catch (Exception e) {
            status.put("connectionHealthy", false);
            status.put("authenticationWorking", false);
            status.put("error", e.getMessage());
        }

        return status;
    }

    public static class PredictionResult {
        private final Instant expiryDate;
        private final Batch.FreshnessStatus freshnessStatus;
        private final int estimatedShelfLifeDays;

        public PredictionResult(Instant expiryDate, Batch.FreshnessStatus freshnessStatus, int estimatedShelfLifeDays) {
            this.expiryDate = expiryDate;
            this.freshnessStatus = freshnessStatus;
            this.estimatedShelfLifeDays = estimatedShelfLifeDays;
        }

        public Instant getExpiryDate() {
            return expiryDate;
        }

        public Batch.FreshnessStatus getFreshnessStatus() {
            return freshnessStatus;
        }

        public int getEstimatedShelfLifeDays() {
            return estimatedShelfLifeDays;
        }
    }
}
