package com.example.kulkita.service;

import com.example.kulkita.constants.BusinessConstants;
import com.example.kulkita.dto.request.BatchCreateRequest;
import com.example.kulkita.dto.request.WeightScaleDataRequest;
import com.example.kulkita.dto.response.BatchResponse;
import com.example.kulkita.dto.response.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class WeightScaleService {

    private static final Logger log = LoggerFactory.getLogger(WeightScaleService.class);

    private final BatchService batchService;
    private final Map<String, WeightScaleDataRequest> lastReceivedData = new HashMap<>();
    private final Random random = new Random();

    private static final Map<String, Double> STORAGE_TEMPERATURE = Map.of(
            "kulkas", 4.0,
            "refrigerator", 4.0,
            "freezer", -18.0,
            "gudang", 25.0,
            "pantry", 25.0
    );

    public WeightScaleService(BatchService batchService) {
        this.batchService = batchService;
    }

    public MessageResponse receiveWeightData(WeightScaleDataRequest request) {
        log.info("Received weight data from device: {} - {} {} {}",
                request.getDeviceId(), request.getWeight(), request.getUnit(),
                request.getIngredientName());

        if (!isValidDevice(request.getDeviceId())) {
            throw new IllegalArgumentException("Unknown or unauthorized device: " + request.getDeviceId());
        }

        lastReceivedData.put(request.getDeviceId(), request);

        if (request.getIngredientName() != null && !request.getIngredientName().isEmpty()) {
            BatchCreateRequest batchRequest = new BatchCreateRequest();
            batchRequest.setIngredientName(request.getIngredientName());
            batchRequest.setWeight(request.getWeight());
            batchRequest.setUnit(request.getUnit());
            batchRequest.setSource(request.getSource());
            batchRequest.setStorageLocation(request.getStorageLocation());

            if (request.getTemperature() != null) {
                batchRequest.setNotes("Auto-created from scale. Temperature: " + request.getTemperature() + "°C. " +
                        (request.getNotes() != null ? request.getNotes() : ""));
            } else {
                batchRequest.setNotes("Auto-created from digital scale. " +
                        (request.getNotes() != null ? request.getNotes() : ""));
            }

            BatchResponse batch = batchService.createBatch(batchRequest);
            log.info("Batch created automatically from scale data: {}", batch.getBatchCode());

            return new MessageResponse("Weight data received and batch created successfully: " + batch.getBatchCode());
        }

        return new MessageResponse("Weight data received successfully from device: " + request.getDeviceId());
    }

    public WeightScaleDataRequest getLatestWeightData(String deviceId) {
        if (!isValidDevice(deviceId)) {
            throw new IllegalArgumentException("Unknown device: " + deviceId);
        }

        WeightScaleDataRequest data = lastReceivedData.get(deviceId);
        if (data == null) {
            throw new IllegalArgumentException("No data available for device: " + deviceId);
        }

        return data;
    }

    public WeightScaleDataRequest generateSimulatedData() {
        String deviceId = "SCALE-SIM-001";

        WeightScaleDataRequest simulatedData = new WeightScaleDataRequest();
        simulatedData.setDeviceId(deviceId);
        simulatedData.setIngredientName(getRandomIngredient());
        simulatedData.setWeight(generateRandomWeight());
        simulatedData.setUnit("kg");
        simulatedData.setSource("Supplier Testing");
        simulatedData.setStorageLocation(getRandomStorageLocation());
        simulatedData.setNotes("Simulated data for testing");

        String location = simulatedData.getStorageLocation().toLowerCase();
        Double baseTemp = STORAGE_TEMPERATURE.getOrDefault(location, 25.0);
        simulatedData.setTemperature(baseTemp + (random.nextGaussian() * 2.0));

        lastReceivedData.put(deviceId, simulatedData);

        log.info("Generated simulated weight data: {} {} of {}",
                simulatedData.getWeight(), simulatedData.getUnit(), simulatedData.getIngredientName());

        return simulatedData;
    }

    public BatchResponse createBatchFromScale(String deviceId, String ingredientName, String source, String storageLocation) {
        WeightScaleDataRequest scaleData = getLatestWeightData(deviceId);

        BatchCreateRequest batchRequest = new BatchCreateRequest();
        batchRequest.setIngredientName(ingredientName);
        batchRequest.setWeight(scaleData.getWeight());
        batchRequest.setUnit(scaleData.getUnit());
        batchRequest.setSource(source);
        batchRequest.setStorageLocation(storageLocation);

        String notes = "Created from scale data (Device: " + deviceId + ")";
        if (scaleData.getTemperature() != null) {
            notes += ". Received temp: " + scaleData.getTemperature() + "°C";
        }
        batchRequest.setNotes(notes);

        return batchService.createBatch(batchRequest);
    }


    private boolean isValidDevice(String deviceId) {
        return deviceId != null && (deviceId.startsWith("SCALE-") || deviceId.startsWith("BLE-") ||
                deviceId.startsWith("WIFI-") || deviceId.startsWith("SIM-"));
    }

    private String getRandomIngredient() {
        String[] ingredients = {
                "Bayam", "Kangkung", "Sawi", "Tomat", "Timun", "Wortel",
                "Kentang", "Bawang Merah", "Ayam", "Daging Sapi", "Ikan", "Telur"
        };
        return ingredients[random.nextInt(ingredients.length)];
    }

    private BigDecimal generateRandomWeight() {
        double minWeight = BusinessConstants.StockThresholds.MINIMUM_WEIGHT_THRESHOLD;
        double maxWeight = 10.0;
        double weight = minWeight + (random.nextDouble() * (maxWeight - minWeight));
        return BigDecimal.valueOf(Math.round(weight * 100.0) / 100.0);
    }


    private String getRandomStorageLocation() {
        String[] locations = {"kulkas", "freezer", "gudang", "pantry"};
        return locations[random.nextInt(locations.length)];
    }

    public Map<String, Object> getDeviceStatus(String deviceId) {
        Map<String, Object> status = new HashMap<>();
        status.put("deviceId", deviceId);
        status.put("connected", isValidDevice(deviceId));
        status.put("lastDataReceived", lastReceivedData.containsKey(deviceId) ?
                java.time.Instant.now().toString() : "Never");
        status.put("dataAvailable", lastReceivedData.containsKey(deviceId));

        return status;
    }
}