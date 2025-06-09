package com.example.kulkita.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponse {
    private String id;
    private String title;
    private String message;
    private String type;
    private String priority;
    private boolean read;
    private Instant readAt;
    private String referenceId;
    private String referenceType;
    private boolean sentViaFirebase;
    private Instant createdAt;

    public String getTypeIcon() {
        return switch (type) {
            case "EXPIRY_ALERT" -> "🚨";
            case "LOW_STOCK" -> "📉";
            case "BATCH_CREATED" -> "📦";
            case "FRESHNESS_CHANGED" -> "🔄";
            case "MENU_USAGE" -> "🍽️";
            case "WASTE_ALERT" -> "🗑️";
            case "FIFO_VIOLATION" -> "⚠️";
            case "ACHIEVEMENT" -> "🏆";
            case "REMINDER" -> "⏰";
            default -> "📢";
        };
    }

    public String getPriorityColor() {
        return switch (priority) {
            case "CRITICAL" -> "#dc3545";
            case "HIGH" -> "#fd7e14";
            case "MEDIUM" -> "#0d6efd";
            case "LOW" -> "#6c757d";
            default -> "#6c757d";
        };
    }
}
