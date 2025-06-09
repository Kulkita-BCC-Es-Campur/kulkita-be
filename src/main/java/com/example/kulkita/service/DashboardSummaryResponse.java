package com.example.kulkita.service;

import com.example.kulkita.dto.response.FreshnessStatusSummaryDto;
import com.example.kulkita.dto.response.IngredientSummaryDto;
import com.example.kulkita.dto.response.StorageLocationSummaryDto;

import java.util.List;

public class DashboardSummaryResponse {
    private long totalActiveBatches;
    private double totalActiveWeight;
    private List<IngredientSummaryDto> ingredientSummaries;
    private List<StorageLocationSummaryDto> storageLocationSummaries;
    private List<FreshnessStatusSummaryDto> freshnessStatusSummaries;

    public DashboardSummaryResponse(long totalActiveBatches, double totalActiveWeight,
                                    List<IngredientSummaryDto> ingredientSummaries,
                                    List<StorageLocationSummaryDto> storageLocationSummaries,
                                    List<FreshnessStatusSummaryDto> freshnessStatusSummaries) {
        this.totalActiveBatches = totalActiveBatches;
        this.totalActiveWeight = totalActiveWeight;
        this.ingredientSummaries = ingredientSummaries;
        this.storageLocationSummaries = storageLocationSummaries;
        this.freshnessStatusSummaries = freshnessStatusSummaries;
    }

    public long getTotalActiveBatches() { return totalActiveBatches; }
    public double getTotalActiveWeight() { return totalActiveWeight; }
    public List<IngredientSummaryDto> getIngredientSummaries() { return ingredientSummaries; }
    public List<StorageLocationSummaryDto> getStorageLocationSummaries() { return storageLocationSummaries; }
    public List<FreshnessStatusSummaryDto> getFreshnessStatusSummaries() { return freshnessStatusSummaries; }
}
