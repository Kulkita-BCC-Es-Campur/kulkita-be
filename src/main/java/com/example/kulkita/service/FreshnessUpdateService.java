package com.example.kulkita.service;

import com.example.kulkita.constants.BusinessConstants;
import com.example.kulkita.entity.Batch;
import com.example.kulkita.repository.BatchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class FreshnessUpdateService {

    private static final Logger log = LoggerFactory.getLogger(FreshnessUpdateService.class);

    private final BatchRepository batchRepository;
    private final SpoilagePredictionService predictionService;
    private final NotificationService notificationService;

    public FreshnessUpdateService(BatchRepository batchRepository,
                                  SpoilagePredictionService predictionService,
                                  NotificationService notificationService) {
        this.batchRepository = batchRepository;
        this.predictionService = predictionService;
        this.notificationService = notificationService;
    }

    @Scheduled(fixedRate = BusinessConstants.ScheduleIntervals.FRESHNESS_UPDATE_INTERVAL_MS)
    @Transactional
    public void updateFreshnessStatus() {
        List<Batch> activeBatches = batchRepository.findByActiveTrue();

        for (Batch batch : activeBatches) {
            if (batch.getExpiryDate() != null) {
                Batch.FreshnessStatus oldStatus = batch.getFreshnessStatus();

                predictionService.updateFreshnessStatus(batch);
                Batch.FreshnessStatus newStatus = batch.getFreshnessStatus();

                if (oldStatus != newStatus) {
                    batchRepository.save(batch);

                    notificationService.sendFreshnessStatusAlert(batch, oldStatus, newStatus);

                    if (newStatus == Batch.FreshnessStatus.RED) {
                        long daysUntilExpiry = ChronoUnit.DAYS.between(Instant.now(), batch.getExpiryDate());
                        notificationService.sendExpiryAlert(batch, (int) daysUntilExpiry);
                    }
                }
            }
        }
    }

    @Scheduled(fixedRate = BusinessConstants.ScheduleIntervals.BATCH_CLEANUP_INTERVAL_MS)
    @Transactional
    public void cleanupExpiredBatches() {
        log.info("Starting cleanup of expired batches...");

        List<Batch> activeBatches = batchRepository.findByActiveTrue();
        int expiredCount = 0;

        for (Batch batch : activeBatches) {
            if (batch.getFreshnessStatus() == Batch.FreshnessStatus.RED &&
                    batch.getExpiryDate() != null &&
                    batch.getExpiryDate().isBefore(java.time.Instant.now())) {

                batch.setActive(false);
                batchRepository.save(batch);
                expiredCount++;

                log.debug("Marked expired batch as inactive: {}", batch.getBatchCode());
            }
        }

        log.info("Expired batch cleanup completed. Marked {} batches as inactive", expiredCount);
    }

    private boolean shouldNotifyStatusChange(Batch.FreshnessStatus oldStatus, Batch.FreshnessStatus newStatus) {
        if (oldStatus == Batch.FreshnessStatus.GREEN && newStatus == Batch.FreshnessStatus.YELLOW) {
            return true;
        }
        if ((oldStatus == Batch.FreshnessStatus.GREEN || oldStatus == Batch.FreshnessStatus.YELLOW)
                && newStatus == Batch.FreshnessStatus.RED) {
            return true;
        }
        return false;
    }
}