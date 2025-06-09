package com.example.kulkita.dto.response;

import java.math.BigDecimal;

public class IngredientSummaryDto {
    private String ingredientName;
    private long batchCount;
    private BigDecimal totalWeight;

    public IngredientSummaryDto(String ingredientName, long batchCount, BigDecimal totalWeight) {
        this.ingredientName = ingredientName;
        this.batchCount = batchCount;
        this.totalWeight = totalWeight;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public long getBatchCount() {
        return batchCount;
    }

    public BigDecimal getTotalWeight() {
        return totalWeight;
    }
}
