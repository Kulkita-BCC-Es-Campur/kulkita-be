package com.example.kulkita.controller;

import com.example.kulkita.dto.response.*;
import com.example.kulkita.service.DashboardService;
import com.example.kulkita.service.DashboardSummaryResponse;
import com.example.kulkita.service.UsageTrendResponse;
import com.example.kulkita.service.WasteAnalysisResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryResponse> getDashboardSummary() {
        DashboardSummaryResponse summary = dashboardService.getDashboardSummary();
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/expiry-alerts")
    public ResponseEntity<ExpiryAlertResponse> getExpiryAlerts() {
        ExpiryAlertResponse alerts = dashboardService.getExpiryAlerts();
        return ResponseEntity.ok(alerts);
    }

    @GetMapping("/usage-trends")
    public ResponseEntity<UsageTrendResponse> getUsageTrends(@RequestParam(defaultValue = "7") int days) {
        UsageTrendResponse trends = dashboardService.getUsageTrends(days);
        return ResponseEntity.ok(trends);
    }

    @GetMapping("/waste-analysis")
    public ResponseEntity<WasteAnalysisResponse> getWasteAnalysis(@RequestParam(defaultValue = "30") int days) {
        WasteAnalysisResponse analysis = dashboardService.getWasteAnalysis(days);
        return ResponseEntity.ok(analysis);
    }

    @PostMapping("/subscribe-notifications")
    public ResponseEntity<MessageResponse> subscribeToNotifications(@RequestParam String deviceToken) {
        dashboardService.subscribeDeviceToNotifications(deviceToken);
        return ResponseEntity.ok(new MessageResponse("Successfully subscribed to notifications"));
    }

    @PostMapping("/unsubscribe-notifications")
    public ResponseEntity<MessageResponse> unsubscribeFromNotifications(@RequestParam String deviceToken) {
        dashboardService.unsubscribeDeviceFromNotifications(deviceToken);
        return ResponseEntity.ok(new MessageResponse("Successfully unsubscribed from notifications"));
    }
}