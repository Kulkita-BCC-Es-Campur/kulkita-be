package com.example.kulkita.controller;

import com.example.kulkita.dto.request.WeightScaleDataRequest;
import com.example.kulkita.dto.response.BatchResponse;
import com.example.kulkita.dto.response.MessageResponse;
import com.example.kulkita.service.WeightScaleService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/weight-scale")
public class WeightScaleController {

    private final WeightScaleService weightScaleService;

    public WeightScaleController(WeightScaleService weightScaleService) {
        this.weightScaleService = weightScaleService;
    }

    /**
     * Receive weight data from BLE/WiFi scale devices
     * IoT integration endpoint
     */
    @PostMapping("/data")
    public ResponseEntity<MessageResponse> receiveWeightData(@Valid @RequestBody WeightScaleDataRequest request) {
        MessageResponse response = weightScaleService.receiveWeightData(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get latest weight data from specific device
     */
    @GetMapping("/data/{deviceId}")
    public ResponseEntity<WeightScaleDataRequest> getLatestWeightData(@PathVariable String deviceId) {
        WeightScaleDataRequest data = weightScaleService.getLatestWeightData(deviceId);
        return ResponseEntity.ok(data);
    }

    /**
     * Generate simulated weight data for testing
     */
    @PostMapping("/simulate")
    public ResponseEntity<WeightScaleDataRequest> generateSimulatedData() {
        WeightScaleDataRequest simulatedData = weightScaleService.generateSimulatedData();
        return ResponseEntity.ok(simulatedData);
    }

    /**
     * Create batch from latest scale data
     */
    @PostMapping("/create-batch/{deviceId}")
    public ResponseEntity<BatchResponse> createBatchFromScale(
            @PathVariable String deviceId,
            @RequestParam String ingredientName,
            @RequestParam String source,
            @RequestParam String storageLocation) {

        BatchResponse batch = weightScaleService.createBatchFromScale(deviceId, ingredientName, source, storageLocation);
        return ResponseEntity.ok(batch);
    }

    /**
     * Get device connection status
     */
    @GetMapping("/status/{deviceId}")
    public ResponseEntity<Map<String, Object>> getDeviceStatus(@PathVariable String deviceId) {
        Map<String, Object> status = weightScaleService.getDeviceStatus(deviceId);
        return ResponseEntity.ok(status);
    }
}