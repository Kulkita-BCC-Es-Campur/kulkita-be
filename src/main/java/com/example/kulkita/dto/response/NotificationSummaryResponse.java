package com.example.kulkita.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationSummaryResponse {
    private long totalNotifications;
    private long unreadCount;
    private long criticalCount;
    private long highPriorityCount;
}