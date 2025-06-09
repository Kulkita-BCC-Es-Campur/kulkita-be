package com.example.kulkita.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "batch_usage_history")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BatchUsageHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id", nullable = false)
    private Batch batch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "used_weight", nullable = false, precision = 10, scale = 3)
    private BigDecimal usedWeight;

    @Enumerated(EnumType.STRING)
    @Column(name = "usage_type", nullable = false)
    private UsageType usageType;

    @Column(name = "menu_name")
    private String menuName;

    @Column(name = "portion_count")
    private Integer portionCount;

    @Column(name = "notes", length = 500)
    private String notes;

    @CreationTimestamp
    @Column(name = "usage_date", nullable = false)
    private Instant usageDate;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public enum UsageType {
        MENU_COOKING,
        MANUAL_USE,
        WASTE,
        EXPIRED,
        REDISTRIBUTED
    }

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (usageDate == null) {
            usageDate = Instant.now();
        }
    }
}