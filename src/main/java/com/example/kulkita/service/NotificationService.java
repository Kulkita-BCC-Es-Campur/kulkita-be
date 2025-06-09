package com.example.kulkita.service;

import com.example.kulkita.constants.BusinessConstants;
import com.example.kulkita.entity.Batch;
import com.example.kulkita.entity.Notification;
import com.example.kulkita.entity.User;
import com.example.kulkita.dto.response.NotificationResponse;
import com.example.kulkita.dto.response.NotificationSummaryResponse;
import com.example.kulkita.dto.response.MessageResponse;
import com.example.kulkita.exception.ResourceNotFoundException;
import com.example.kulkita.repository.NotificationRepository;
import com.example.kulkita.repository.UserRepository;
import com.google.firebase.messaging.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final FirebaseMessaging firebaseMessaging;

    private final Map<String, Set<String>> userDeviceTokens = new HashMap<>();

    public NotificationService(NotificationRepository notificationRepository,
                               UserRepository userRepository,
                               @Autowired(required = false) FirebaseMessaging firebaseMessaging) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.firebaseMessaging = firebaseMessaging;
    }

    public Page<NotificationResponse> getUserNotifications(String userId, int page, int size) {
        User user = getUserById(userId);
        Pageable pageable = PageRequest.of(page, size);

        Page<Notification> notifications = notificationRepository.findByUserOrderByCreatedAtDesc(user, pageable);
        return notifications.map(this::convertToResponse);
    }

    public List<NotificationResponse> getUnreadNotifications(String userId) {
        User user = getUserById(userId);
        List<Notification> notifications = notificationRepository.findByUserAndReadFalseOrderByCreatedAtDesc(user);

        return notifications.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public NotificationSummaryResponse getNotificationSummary(String userId) {
        User user = getUserById(userId);

        long totalNotifications = notificationRepository.countByUser(user);
        long unreadCount = notificationRepository.countByUserAndReadFalse(user);
        long criticalCount = notificationRepository.countByUserAndPriority(user, Notification.NotificationPriority.CRITICAL);
        long highPriorityCount = notificationRepository.countByUserAndPriority(user, Notification.NotificationPriority.HIGH);

        return new NotificationSummaryResponse(totalNotifications, unreadCount, criticalCount, highPriorityCount);
    }

    @Transactional
    public MessageResponse markNotificationsAsRead(String userId, List<String> notificationIds) {
        User user = getUserById(userId);

        List<UUID> ids = notificationIds.stream()
                .map(UUID::fromString)
                .collect(Collectors.toList());

        int updatedCount = notificationRepository.markAsReadByIds(ids, user, Instant.now());

        return new MessageResponse(updatedCount + " notifications marked as read");
    }

    @Transactional
    public MessageResponse markAllAsRead(String userId) {
        User user = getUserById(userId);

        int updatedCount = notificationRepository.markAllAsReadForUser(user, Instant.now());

        return new MessageResponse(updatedCount + " notifications marked as read");
    }

    public Page<NotificationResponse> searchNotifications(String userId, String query, int page, int size) {
        User user = getUserById(userId);
        Pageable pageable = PageRequest.of(page, size);

        Page<Notification> notifications = notificationRepository.searchNotifications(user, query, pageable);
        return notifications.map(this::convertToResponse);
    }

    @Transactional
    public MessageResponse deleteNotification(String userId, String notificationId) {
        User user = getUserById(userId);

        Notification notification = notificationRepository.findById(UUID.fromString(notificationId))
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        if (!notification.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Notification does not belong to user");
        }

        notificationRepository.delete(notification);
        return new MessageResponse("Notification deleted successfully");
    }

    public MessageResponse registerDeviceToken(String userId, String deviceToken) {
        userDeviceTokens.computeIfAbsent(userId, k -> new HashSet<>()).add(deviceToken);
        log.info("Registered device token for user: {}", userId);
        return new MessageResponse("Device token registered successfully");
    }

    public MessageResponse unregisterDeviceToken(String userId, String deviceToken) {
        Set<String> tokens = userDeviceTokens.get(userId);
        if (tokens != null) {
            tokens.remove(deviceToken);
            if (tokens.isEmpty()) {
                userDeviceTokens.remove(userId);
            }
        }
        log.info("Unregistered device token for user: {}", userId);
        return new MessageResponse("Device token unregistered successfully");
    }

    @Async
    public void sendBatchCreatedNotification(Batch batch) {
        try {
            List<User> users = userRepository.findAll();

            for (User user : users) {
                Notification notification = createNotification(
                        user,
                        "📦 Batch Baru Ditambahkan",
                        String.format("Batch %s untuk %s telah berhasil ditambahkan ke inventory",
                                batch.getBatchCode(), batch.getIngredientName()),
                        Notification.NotificationType.BATCH_CREATED,
                        Notification.NotificationPriority.LOW,
                        batch.getBatchCode(),
                        "BATCH",
                        false
                );

                notificationRepository.save(notification);
            }
        } catch (Exception e) {
            log.error("Failed to send batch created notification", e);
        }
    }

    @Async
    public void sendFreshnessStatusAlert(Batch batch, Batch.FreshnessStatus oldStatus, Batch.FreshnessStatus newStatus) {
        try {
            String emoji = switch (newStatus) {
                case RED -> "🚨";
                case YELLOW -> "⚠️";
                case GREEN -> "✅";
            };

            String statusName = switch (newStatus) {
                case RED -> "Kritis";
                case YELLOW -> "Waspada";
                case GREEN -> "Aman";
            };

            boolean sendFirebase = newStatus == Batch.FreshnessStatus.RED;
            Notification.NotificationPriority priority = newStatus == Batch.FreshnessStatus.RED ?
                    Notification.NotificationPriority.HIGH : Notification.NotificationPriority.MEDIUM;

            List<User> users = userRepository.findAll();

            for (User user : users) {
                Notification notification = createNotification(
                        user,
                        emoji + " Status Kesegaran Berubah",
                        String.format("Batch %s (%s) berubah dari %s ke %s - %s",
                                batch.getBatchCode(), batch.getIngredientName(), oldStatus, newStatus, statusName),
                        Notification.NotificationType.FRESHNESS_CHANGED,
                        priority,
                        batch.getBatchCode(),
                        "BATCH",
                        sendFirebase
                );

                notificationRepository.save(notification);

                if (sendFirebase) {
                    sendFirebaseNotification(user, notification);
                }
            }
        } catch (Exception e) {
            log.error("Failed to send freshness status alert", e);
        }
    }

    @Async
    public void sendLowStockNotification(String ingredientName, double totalWeight) {
        try {
            List<User> users = userRepository.findAll();

            for (User user : users) {
                Notification notification = createNotification(
                        user,
                        "📉 Stok Rendah",
                        String.format("Stok %s hampir habis. Tersisa %.2f kg. Pertimbangkan untuk menambah stok.",
                                ingredientName, totalWeight),
                        Notification.NotificationType.LOW_STOCK,
                        Notification.NotificationPriority.MEDIUM,
                        ingredientName,
                        "INGREDIENT",
                        true
                );

                notificationRepository.save(notification);
                sendFirebaseNotification(user, notification);
            }
        } catch (Exception e) {
            log.error("Failed to send low stock notification", e);
        }
    }

    @Async
    public void sendExpiryAlert(Batch batch, int daysUntilExpiry) {
        try {
            String urgency = daysUntilExpiry <= 1 ? "HARI INI" : "DALAM " + daysUntilExpiry + " HARI";
            Notification.NotificationPriority priority = daysUntilExpiry <= 1 ?
                    Notification.NotificationPriority.CRITICAL : Notification.NotificationPriority.HIGH;

            List<User> users = userRepository.findAll();

            for (User user : users) {
                Notification notification = createNotification(
                        user,
                        "🚨 Peringatan Expired",
                        String.format("Batch %s (%s) akan expired %s. Gunakan segera!",
                                batch.getBatchCode(), batch.getIngredientName(), urgency),
                        Notification.NotificationType.EXPIRY_ALERT,
                        priority,
                        batch.getBatchCode(),
                        "BATCH",
                        true
                );

                notificationRepository.save(notification);
                sendFirebaseNotification(user, notification);
            }
        } catch (Exception e) {
            log.error("Failed to send expiry alert", e);
        }
    }

    @Async
    public void sendFifoViolationAlert(String ingredientName, String violatingBatch, String shouldUseBatch) {
        try {
            List<User> users = userRepository.findAll();

            for (User user : users) {
                Notification notification = createNotification(
                        user,
                        "⚠️ Pelanggaran FIFO",
                        String.format("Batch %s digunakan tetapi batch %s untuk %s seharusnya digunakan lebih dulu. Gunakan prinsip FIFO!",
                                violatingBatch, shouldUseBatch, ingredientName),
                        Notification.NotificationType.FIFO_VIOLATION,
                        Notification.NotificationPriority.MEDIUM,
                        ingredientName,
                        "INGREDIENT",
                        false
                );

                notificationRepository.save(notification);
            }
        } catch (Exception e) {
            log.error("Failed to send FIFO violation alert", e);
        }
    }

    @Async
    public void sendAchievementNotification(String userId, String achievementTitle, String achievementDescription) {
        try {
            User user = getUserById(userId);

            Notification notification = createNotification(
                    user,
                    "🏆 " + achievementTitle,
                    achievementDescription,
                    Notification.NotificationType.ACHIEVEMENT,
                    Notification.NotificationPriority.LOW,
                    null,
                    "ACHIEVEMENT",
                    true
            );

            notificationRepository.save(notification);
            sendFirebaseNotification(user, notification);
        } catch (Exception e) {
            log.error("Failed to send achievement notification", e);
        }
    }

    @Scheduled(cron = BusinessConstants.ScheduleIntervals.TOKEN_CLEANUP_CRON)
    @Transactional
    public void cleanupOldNotifications() {
        log.info("Cleaning up old notifications...");

        Instant cutoffDate = Instant.now().minus(30, ChronoUnit.DAYS);
        int deletedCount = notificationRepository.deleteOldNotifications(cutoffDate);

        log.info("Cleaned up {} old notifications", deletedCount);
    }

    private Notification createNotification(User user, String title, String message,
                                            Notification.NotificationType type,
                                            Notification.NotificationPriority priority,
                                            String referenceId, String referenceType,
                                            boolean sendViaFirebase) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setPriority(priority);
        notification.setReferenceId(referenceId);
        notification.setReferenceType(referenceType);
        notification.setSentViaFirebase(sendViaFirebase);

        return notification;
    }

    @Async
    protected void sendFirebaseNotification(User user, Notification notification) {
        if (firebaseMessaging == null) {
            log.warn("Firebase not configured, skipping Firebase notification");
            return;
        }

        try {
            String userId = user.getId().toString();
            Set<String> deviceTokens = userDeviceTokens.get(userId);

            if (deviceTokens == null || deviceTokens.isEmpty()) {
                log.debug("No device tokens found for user: {}", userId);
                return;
            }

            for (String deviceToken : deviceTokens) {
                try {
                    Message message = Message.builder()
                            .setToken(deviceToken)
                            .setNotification(com.google.firebase.messaging.Notification.builder()
                                    .setTitle(notification.getTitle())
                                    .setBody(notification.getMessage())
                                    .build())
                            .putData("notificationId", notification.getId().toString())
                            .putData("type", notification.getType().toString())
                            .putData("priority", notification.getPriority().toString())
                            .putData("referenceId", notification.getReferenceId() != null ? notification.getReferenceId() : "")
                            .putData("referenceType", notification.getReferenceType() != null ? notification.getReferenceType() : "")
                            .build();

                    String response = firebaseMessaging.send(message);
                    log.debug("Firebase notification sent successfully: {}", response);

                } catch (FirebaseMessagingException e) {
                    log.error("Failed to send Firebase notification to token: {}", deviceToken, e);
                    if (e.getErrorCode().equals("INVALID_ARGUMENT") || e.getErrorCode().equals("UNREGISTERED")) {
                        deviceTokens.remove(deviceToken);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to send Firebase notification", e);
        }
    }

    private User getUserById(String userId) {
        return userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
    }

    private NotificationResponse convertToResponse(Notification notification) {
        NotificationResponse response = new NotificationResponse();
        response.setId(notification.getId().toString());
        response.setTitle(notification.getTitle());
        response.setMessage(notification.getMessage());
        response.setType(notification.getType().toString());
        response.setPriority(notification.getPriority().toString());
        response.setRead(notification.isRead());
        response.setReadAt(notification.getReadAt());
        response.setReferenceId(notification.getReferenceId());
        response.setReferenceType(notification.getReferenceType());
        response.setSentViaFirebase(notification.isSentViaFirebase());
        response.setCreatedAt(notification.getCreatedAt());
        return response;
    }
}