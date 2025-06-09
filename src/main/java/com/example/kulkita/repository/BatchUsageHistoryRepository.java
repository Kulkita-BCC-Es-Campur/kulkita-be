package com.example.kulkita.repository;

import com.example.kulkita.entity.BatchUsageHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface BatchUsageHistoryRepository extends JpaRepository<BatchUsageHistory, UUID> {

    List<BatchUsageHistory> findByBatchIdOrderByUsageDateDesc(UUID batchId);

    List<BatchUsageHistory> findByUserIdOrderByUsageDateDesc(UUID userId);

    @Query("SELECT buh FROM BatchUsageHistory buh WHERE buh.usageDate BETWEEN :startDate AND :endDate ORDER BY buh.usageDate DESC")
    List<BatchUsageHistory> findByUsageDateBetween(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);

    @Query("SELECT buh.batch.ingredientName, SUM(buh.usedWeight) as totalUsed " +
            "FROM BatchUsageHistory buh " +
            "WHERE buh.usageDate BETWEEN :startDate AND :endDate " +
            "GROUP BY buh.batch.ingredientName " +
            "ORDER BY totalUsed DESC")
    List<Object[]> getDailyUsageTrend(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);

    @Query("SELECT buh.menuName, COUNT(buh) as usageCount, SUM(buh.portionCount) as totalPortions " +
            "FROM BatchUsageHistory buh " +
            "WHERE buh.usageType = 'MENU_COOKING' AND buh.usageDate BETWEEN :startDate AND :endDate " +
            "GROUP BY buh.menuName " +
            "ORDER BY usageCount DESC")
    List<Object[]> getPopularMenus(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);

    @Query("SELECT buh.usageType, SUM(buh.usedWeight) as totalWeight " +
            "FROM BatchUsageHistory buh " +
            "WHERE buh.usageDate BETWEEN :startDate AND :endDate " +
            "GROUP BY buh.usageType")
    List<Object[]> getUsageByType(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
}