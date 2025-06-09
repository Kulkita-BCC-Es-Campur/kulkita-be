package com.example.kulkita.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class IngredientSummary {
    private String ingredientName;
    private long batchCount;
    private BigDecimal totalWeight;

    public IngredientSummary() {}

    public IngredientSummary(String ingredientName, long batchCount, BigDecimal totalWeight) {
        this.ingredientName = ingredientName;
        this.batchCount = batchCount;
        this.totalWeight = totalWeight;
    }

}
