package com.example.kulkita.repository;

import com.example.kulkita.entity.Notification;
import com.example.kulkita.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    List<Notification> findByUserAndReadFalseOrderByCreatedAtDesc(User user);
    Page<Notification> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    long countByUserAndReadFalse(User user);
    long countByUser(User user);
    long countByUserAndPriority(User user, Notification.NotificationPriority priority);

    List<Notification> findByUserAndTypeOrderByCreatedAtDesc(User user, Notification.NotificationType type);

    @Modifying
    @Query("UPDATE Notification n SET n.read = true, n.readAt = :readAt WHERE n.id IN :ids AND n.user = :user")
    int markAsReadByIds(@Param("ids") List<UUID> ids, @Param("user") User user, @Param("readAt") Instant readAt);

    @Modifying
    @Query("UPDATE Notification n SET n.read = true, n.readAt = :readAt WHERE n.user = :user AND n.read = false")
    int markAllAsReadForUser(@Param("user") User user, @Param("readAt") Instant readAt);

    @Query("SELECT n FROM Notification n WHERE n.user = :user " +
            "AND (LOWER(n.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(n.message) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "ORDER BY n.createdAt DESC")
    Page<Notification> searchNotifications(@Param("user") User user, @Param("query") String query, Pageable pageable);

    @Modifying
    @Query("DELETE FROM Notification n WHERE n.createdAt < :cutoffDate")
    int deleteOldNotifications(@Param("cutoffDate") Instant cutoffDate);
}