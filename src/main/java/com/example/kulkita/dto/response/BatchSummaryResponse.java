package com.example.kulkita.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class BatchSummaryResponse {
    private long totalActiveBatches;
    private Double totalActiveWeight;
    private List<IngredientSummary> ingredientSummaries;
    private List<StorageLocationSummary> storageLocationSummaries;
    private List<FreshnessStatusSummary> freshnessStatusSummaries;

    public BatchSummaryResponse() {}

    public BatchSummaryResponse(long totalActiveBatches, Double totalActiveWeight,
                                List<IngredientSummary> ingredientSummaries,
                                List<StorageLocationSummary> storageLocationSummaries,
                                List<FreshnessStatusSummary> freshnessStatusSummaries) {
        this.totalActiveBatches = totalActiveBatches;
        this.totalActiveWeight = totalActiveWeight;
        this.ingredientSummaries = ingredientSummaries;
        this.storageLocationSummaries = storageLocationSummaries;
        this.freshnessStatusSummaries = freshnessStatusSummaries;
    }

}
