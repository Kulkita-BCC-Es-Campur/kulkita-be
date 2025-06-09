package com.example.kulkita.service;

import com.example.kulkita.dto.request.UsageRecordRequest;
import com.example.kulkita.dto.response.MessageResponse;
import com.example.kulkita.dto.response.UsageHistoryResponse;
import com.example.kulkita.entity.Batch;
import com.example.kulkita.entity.BatchUsageHistory;
import com.example.kulkita.entity.User;
import com.example.kulkita.exception.ResourceNotFoundException;
import com.example.kulkita.repository.BatchRepository;
import com.example.kulkita.repository.BatchUsageHistoryRepository;
import com.example.kulkita.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsageService {

    private static final Logger log = LoggerFactory.getLogger(UsageService.class);

    private final BatchRepository batchRepository;
    private final BatchUsageHistoryRepository usageHistoryRepository;
    private final UserRepository userRepository;
    private final UserLookupService userLookupService;

    public UsageService(BatchRepository batchRepository,
                        BatchUsageHistoryRepository usageHistoryRepository,
                        UserRepository userRepository,
                        UserLookupService userLookupService) {
        this.batchRepository = batchRepository;
        this.usageHistoryRepository = usageHistoryRepository;
        this.userRepository = userRepository;
        this.userLookupService = userLookupService;
    }

    @Transactional
    public MessageResponse recordUsage(UsageRecordRequest request, String userIdentifier) {
        log.info("Recording usage for batch: {} by user: {}", request.getBatchCode(), userIdentifier);

        User user = userLookupService.findByIdentifier(userIdentifier);

        Batch batch = batchRepository.findByBatchCode(request.getBatchCode())
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found with code: " + request.getBatchCode()));

        if (!batch.isActive()) {
            throw new IllegalArgumentException("Batch is not active");
        }

        if (batch.getWeight().compareTo(request.getUsedWeight()) < 0) {
            throw new IllegalArgumentException("Insufficient stock. Available: " + batch.getWeight() + " " + batch.getUnit());
        }

        BigDecimal newWeight = batch.getWeight().subtract(request.getUsedWeight());
        batch.setWeight(newWeight);

        if (newWeight.compareTo(BigDecimal.ZERO) == 0) {
            batch.setActive(false);
        }

        batchRepository.save(batch);

        BatchUsageHistory usage = new BatchUsageHistory();
        usage.setBatch(batch);
        usage.setUser(user);
        usage.setUsedWeight(request.getUsedWeight());
        usage.setUsageType(BatchUsageHistory.UsageType.valueOf(request.getUsageType()));
        usage.setMenuName(request.getMenuName());
        usage.setPortionCount(request.getPortionCount());
        usage.setNotes(request.getNotes());

        usageHistoryRepository.save(usage);

        log.info("Usage recorded successfully for batch: {}", request.getBatchCode());
        return new MessageResponse("Usage recorded successfully");
    }

    public List<UsageHistoryResponse> getUsageHistory(String batchCode) {
        Batch batch = batchRepository.findByBatchCode(batchCode)
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found with code: " + batchCode));

        return usageHistoryRepository.findByBatchIdOrderByUsageDateDesc(batch.getId())
                .stream()
                .map(this::convertToUsageHistoryResponse)
                .collect(Collectors.toList());
    }

    public List<UsageHistoryResponse> getUsageHistoryByDateRange(Instant startDate, Instant endDate) {
        return usageHistoryRepository.findByUsageDateBetween(startDate, endDate)
                .stream()
                .map(this::convertToUsageHistoryResponse)
                .collect(Collectors.toList());
    }

    private UsageHistoryResponse convertToUsageHistoryResponse(BatchUsageHistory usage) {
        return new UsageHistoryResponse(
                usage.getId().toString(),
                usage.getBatch().getBatchCode(),
                usage.getBatch().getIngredientName(),
                usage.getUsedWeight(),
                usage.getUsageType().toString(),
                usage.getMenuName(),
                usage.getPortionCount(),
                usage.getNotes(),
                usage.getUsageDate(),
                usage.getUser().getUsername()
        );
    }
}