package com.example.kulkita.controller;

import com.example.kulkita.dto.response.*;
import com.example.kulkita.security.services.UserPrincipal;
import com.example.kulkita.service.NotificationService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<Page<NotificationResponse>> getUserNotifications(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        String userId = getUserIdFromAuthentication(authentication);
        Page<NotificationResponse> notifications = notificationService.getUserNotifications(userId, page, size);

        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponse>> getUnreadNotifications(Authentication authentication) {
        String userId = getUserIdFromAuthentication(authentication);
        List<NotificationResponse> notifications = notificationService.getUnreadNotifications(userId);

        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/summary")
    public ResponseEntity<NotificationSummaryResponse> getNotificationSummary(Authentication authentication) {
        String userId = getUserIdFromAuthentication(authentication);
        NotificationSummaryResponse summary = notificationService.getNotificationSummary(userId);

        return ResponseEntity.ok(summary);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<NotificationResponse>> searchNotifications(
            Authentication authentication,
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        String userId = getUserIdFromAuthentication(authentication);
        Page<NotificationResponse> notifications = notificationService.searchNotifications(userId, query, page, size);

        return ResponseEntity.ok(notifications);
    }

    @PutMapping("/mark-read")
    public ResponseEntity<MessageResponse> markNotificationsAsRead(
            Authentication authentication,
            @RequestBody List<String> notificationIds) {

        String userId = getUserIdFromAuthentication(authentication);
        MessageResponse response = notificationService.markNotificationsAsRead(userId, notificationIds);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/mark-all-read")
    public ResponseEntity<MessageResponse> markAllAsRead(Authentication authentication) {
        String userId = getUserIdFromAuthentication(authentication);
        MessageResponse response = notificationService.markAllAsRead(userId);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<MessageResponse> deleteNotification(
            Authentication authentication,
            @PathVariable String notificationId) {

        String userId = getUserIdFromAuthentication(authentication);
        MessageResponse response = notificationService.deleteNotification(userId, notificationId);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register-token")
    public ResponseEntity<MessageResponse> registerDeviceToken(
            Authentication authentication,
            @RequestParam String token) {

        String userId = getUserIdFromAuthentication(authentication);
        MessageResponse response = notificationService.registerDeviceToken(userId, token);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/unregister-token")
    public ResponseEntity<MessageResponse> unregisterDeviceToken(
            Authentication authentication,
            @RequestParam String token) {

        String userId = getUserIdFromAuthentication(authentication);
        MessageResponse response = notificationService.unregisterDeviceToken(userId, token);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/types")
    public ResponseEntity<List<NotificationTypeInfo>> getNotificationTypes() {
        List<NotificationTypeInfo> types = List.of(
                new NotificationTypeInfo("EXPIRY_ALERT", "Peringatan Expired", "🚨", "#dc3545"),
                new NotificationTypeInfo("LOW_STOCK", "Stok Rendah", "📉", "#fd7e14"),
                new NotificationTypeInfo("BATCH_CREATED", "Batch Baru", "📦", "#28a745"),
                new NotificationTypeInfo("FRESHNESS_CHANGED", "Status Kesegaran", "🔄", "#17a2b8"),
                new NotificationTypeInfo("MENU_USAGE", "Penggunaan Menu", "🍽️", "#6f42c1"),
                new NotificationTypeInfo("WASTE_ALERT", "Peringatan Limbah", "🗑️", "#dc3545"),
                new NotificationTypeInfo("FIFO_VIOLATION", "Pelanggaran FIFO", "⚠️", "#ffc107"),
                new NotificationTypeInfo("ACHIEVEMENT", "Pencapaian", "🏆", "#ffc107"),
                new NotificationTypeInfo("REMINDER", "Pengingat", "⏰", "#6c757d")
        );

        return ResponseEntity.ok(types);
    }

    @PostMapping("/test")
    public ResponseEntity<MessageResponse> sendTestNotification(
            Authentication authentication,
            @RequestParam(defaultValue = "🧪 Test Notification") String title,
            @RequestParam(defaultValue = "This is a test notification") String message,
            @RequestParam(defaultValue = "false") boolean sendFirebase) {

        String userId = getUserIdFromAuthentication(authentication);

        try {
            notificationService.sendAchievementNotification(userId, title.replace("🧪 ", ""), message);
            return ResponseEntity.ok(new MessageResponse("Test notification sent successfully"));
        } catch (Exception e) {
            return ResponseEntity.ok(new MessageResponse("Failed to send test notification: " + e.getMessage()));
        }
    }

    private String getUserIdFromAuthentication(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalArgumentException("Authentication is required");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserPrincipal userPrincipal) {
            return userPrincipal.getId().toString();
        }

        return authentication.getName();
    }

    @Setter
    @Getter
    public static class NotificationTypeInfo {
        private String type;
        private String name;
        private String icon;
        private String color;

        public NotificationTypeInfo(String type, String name, String icon, String color) {
            this.type = type;
            this.name = name;
            this.icon = icon;
            this.color = color;
        }

    }
}