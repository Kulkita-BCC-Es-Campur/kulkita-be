package com.example.kulkita.repository;

import com.example.kulkita.entity.Batch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BatchRepository extends JpaRepository<Batch, UUID> {

    List<Batch> findByActiveTrue();
    List<Batch> findByActiveTrueOrderByEntryDateAsc();
    List<Batch> findByIngredientNameAndActiveTrueOrderByEntryDateAsc(String ingredientName);
    List<Batch> findByStorageLocationAndActiveTrue(String storageLocation);
    List<Batch> findByStorageLocationAndActiveTrueOrderByEntryDateAsc(String storageLocation);
    List<Batch> findByFreshnessStatusAndActiveTrue(Batch.FreshnessStatus status);
    Optional<Batch> findByBatchCode(String batchCode);

    @Query("SELECT b FROM Batch b WHERE b.active = true " +
            "ORDER BY " +
            "CASE WHEN b.expiryDate IS NULL THEN 1 ELSE 0 END, " +
            "b.expiryDate ASC, " +
            "b.entryDate ASC")
    List<Batch> findActiveBatchesOrderedByFifoPriority();

    @Query("SELECT b FROM Batch b WHERE b.active = true AND b.ingredientName = :ingredientName " +
            "ORDER BY " +
            "CASE WHEN b.expiryDate IS NULL THEN 1 ELSE 0 END, " +
            "b.expiryDate ASC, " +
            "b.entryDate ASC")
    List<Batch> findByIngredientNameOrderedByFifoPriority(@Param("ingredientName") String ingredientName);

    @Query("SELECT b FROM Batch b WHERE b.active = true AND b.storageLocation = :storageLocation " +
            "ORDER BY " +
            "CASE WHEN b.expiryDate IS NULL THEN 1 ELSE 0 END, " +
            "b.expiryDate ASC, " +
            "b.entryDate ASC")
    List<Batch> findByStorageLocationOrderedByFifoPriority(@Param("storageLocation") String storageLocation);

    @Query("SELECT b FROM Batch b WHERE b.active = true " +
            "AND b.ingredientName = :ingredientName AND b.storageLocation = :storageLocation " +
            "ORDER BY " +
            "CASE WHEN b.expiryDate IS NULL THEN 1 ELSE 0 END, " +
            "b.expiryDate ASC, " +
            "b.entryDate ASC")
    List<Batch> findByIngredientAndLocationOrderedByFifoPriority(
            @Param("ingredientName") String ingredientName,
            @Param("storageLocation") String storageLocation);

    @Query("SELECT b FROM Batch b WHERE b.active = true AND b.ingredientName = :ingredientName " +
            "ORDER BY " +
            "CASE WHEN b.expiryDate IS NULL THEN 1 ELSE 0 END, " +
            "b.expiryDate ASC, " +
            "b.entryDate ASC " +
            "LIMIT 1")
    Optional<Batch> findOldestBatchByIngredient(@Param("ingredientName") String ingredientName);

    @Query("SELECT b FROM Batch b WHERE b.active = true " +
            "AND b.expiryDate BETWEEN :now AND :criticalThreshold " +
            "ORDER BY b.expiryDate ASC")
    List<Batch> findCriticalBatches(@Param("now") Instant now, @Param("criticalThreshold") Instant criticalThreshold);

    @Query("SELECT b FROM Batch b WHERE b.active = true AND b.category = :category " +
            "ORDER BY " +
            "CASE WHEN b.expiryDate IS NULL THEN 1 ELSE 0 END, " +
            "b.expiryDate ASC, " +
            "b.entryDate ASC")
    List<Batch> findByCategoryOrderedByFifoPriority(@Param("category") String category);

    @Query("SELECT b FROM Batch b WHERE b.active = true " +
            "AND b.expiryDate IS NOT NULL " +
            "AND b.expiryDate BETWEEN :now AND :futureDate " +
            "ORDER BY b.expiryDate ASC")
    List<Batch> findBatchesExpiringWithinDays(@Param("now") Instant now, @Param("futureDate") Instant futureDate);

    @Query("SELECT COUNT(b) FROM Batch b WHERE b.active = true " +
            "AND b.expiryDate < :now")
    long countExpiredActiveBatches(@Param("now") Instant now);

    @Query("SELECT b.storageLocation, b.ingredientName, COUNT(b) as batchCount, " +
            "MIN(b.entryDate) as oldestEntry, MAX(b.entryDate) as newestEntry " +
            "FROM Batch b WHERE b.active = true " +
            "GROUP BY b.storageLocation, b.ingredientName " +
            "ORDER BY b.storageLocation, oldestEntry")
    List<Object[]> getBatchLayoutSummary();

    @Query("SELECT b FROM Batch b WHERE b.active = true AND b.storageLocation = :storageLocation " +
            "ORDER BY b.ingredientName, " +
            "CASE WHEN b.expiryDate IS NULL THEN 1 ELSE 0 END, " +
            "b.expiryDate ASC, " +
            "b.entryDate ASC")
    List<Batch> findBatchesForStorageLayout(@Param("storageLocation") String storageLocation);

    @Query("SELECT b FROM Batch b WHERE b.active = true AND b.expiryDate BETWEEN :startDate AND :endDate ORDER BY b.expiryDate ASC")
    List<Batch> findExpiringBatches(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);

    @Query("SELECT COUNT(b) FROM Batch b WHERE b.active = true")
    long countActiveBatches();

    @Query("SELECT SUM(b.weight) FROM Batch b WHERE b.active = true")
    Double getTotalActiveWeight();

    @Query("SELECT b.ingredientName, COUNT(b) as batchCount, SUM(b.weight) as totalWeight " +
            "FROM Batch b WHERE b.active = true " +
            "GROUP BY b.ingredientName ORDER BY totalWeight DESC")
    List<Object[]> getIngredientSummary();

    @Query("SELECT b.storageLocation, COUNT(b) as batchCount " +
            "FROM Batch b WHERE b.active = true " +
            "GROUP BY b.storageLocation")
    List<Object[]> getStorageLocationSummary();

    @Query("SELECT b.freshnessStatus, COUNT(b) as count " +
            "FROM Batch b WHERE b.active = true " +
            "GROUP BY b.freshnessStatus")
    List<Object[]> getFreshnessStatusSummary();

    @Query("SELECT MAX(CAST(SUBSTRING(b.batchCode, LENGTH(b.batchCode) - 2, 3) AS int)) " +
            "FROM Batch b WHERE b.batchCode LIKE :prefix%")
    Integer getLastSerialNumber(@Param("prefix") String prefix);

    @Query("SELECT DISTINCT b.ingredientName FROM Batch b WHERE b.active = true ORDER BY b.ingredientName")
    List<String> findAllIngredientNames();

    @Query("SELECT DISTINCT b.storageLocation FROM Batch b WHERE b.active = true ORDER BY b.storageLocation")
    List<String> findAllStorageLocations();

    @Query("SELECT b.ingredientName FROM Batch b WHERE b.active = true AND b.ingredientName LIKE :query% " +
            "GROUP BY b.ingredientName ORDER BY b.ingredientName LIMIT 10")
    List<String> findIngredientNamesForAutocomplete(@Param("query") String query);

    @Query("SELECT DISTINCT b.category FROM Batch b WHERE b.active = true AND b.category IS NOT NULL ORDER BY b.category")
    List<String> findAllCategories();

    List<Batch> findByCategoryAndActiveTrue(String category);
    List<Batch> findByCategoryAndActiveTrueOrderByEntryDateAsc(String category);

    @Query("SELECT b.category, COUNT(b) as batchCount, SUM(b.weight) as totalWeight " +
            "FROM Batch b WHERE b.active = true AND b.category IS NOT NULL " +
            "GROUP BY b.category ORDER BY totalWeight DESC")
    List<Object[]> getCategorySummary();

    @Query("SELECT b FROM Batch b WHERE b.active = true " +
            "AND (:ingredientName IS NULL OR LOWER(b.ingredientName) LIKE LOWER(CONCAT('%', :ingredientName, '%'))) " +
            "AND (:category IS NULL OR b.category = :category) " +
            "AND (:storageLocation IS NULL OR b.storageLocation = :storageLocation) " +
            "AND (:freshnessStatus IS NULL OR b.freshnessStatus = :freshnessStatus) " +
            "ORDER BY " +
            "CASE WHEN :sortBy = 'entryDate' THEN b.entryDate END ASC, " +
            "CASE WHEN :sortBy = 'expiryDate' THEN b.expiryDate END ASC, " +
            "CASE WHEN :sortBy = 'weight' THEN b.weight END ASC, " +
            "CASE WHEN :sortBy = 'category' THEN b.category END ASC, " +
            "CASE WHEN :sortBy = 'fifo' THEN " +
            "  CASE WHEN b.expiryDate IS NULL THEN 1 ELSE 0 END END ASC, " +
            "CASE WHEN :sortBy = 'fifo' THEN b.expiryDate END ASC, " +
            "CASE WHEN :sortBy = 'fifo' THEN b.entryDate END ASC, " +
            "b.entryDate ASC")
    List<Batch> findBatchesWithAdvancedFilter(@Param("ingredientName") String ingredientName,
                                              @Param("category") String category,
                                              @Param("storageLocation") String storageLocation,
                                              @Param("freshnessStatus") Batch.FreshnessStatus freshnessStatus,
                                              @Param("sortBy") String sortBy);

    @Query("SELECT b.ingredientName, COUNT(b) as batchCount, SUM(b.weight) as totalWeight, " +
            "MIN(b.unit) as unit, MIN(b.category) as category " +
            "FROM Batch b WHERE b.active = true " +
            "GROUP BY b.ingredientName " +
            "ORDER BY totalWeight DESC")
    List<Object[]> getIngredientsSummary();

    @Query("SELECT b.freshnessStatus, COUNT(b) as count, COALESCE(SUM(CAST(b.weight AS double)), 0.0) as totalWeight " +
            "FROM Batch b WHERE b.active = true " +
            "GROUP BY b.freshnessStatus")
    List<Object[]> getFreshnessStatusSummaryData();

    @Query("SELECT b.storageLocation, COUNT(b) as batchCount, COALESCE(SUM(CAST(b.weight AS double)), 0.0) as totalWeight " +
            "FROM Batch b WHERE b.active = true " +
            "GROUP BY b.storageLocation " +
            "ORDER BY totalWeight DESC")
    List<Object[]> getStorageLocationSummaryData();

    @Query("SELECT b FROM Batch b WHERE b.active = true AND b.ingredientName = :ingredientName")
    List<Batch> findBatchesByIngredientName(@Param("ingredientName") String ingredientName);

    @Query("SELECT b FROM Batch b WHERE b.active = true AND b.storageLocation = :storageLocation")
    List<Batch> findBatchesByStorageLocationDetailed(@Param("storageLocation") String storageLocation);

    List<Batch> findByIngredientNameAndStorageLocationAndActiveTrueOrderByEntryDateAsc(
            String ingredientName, String storageLocation);
}