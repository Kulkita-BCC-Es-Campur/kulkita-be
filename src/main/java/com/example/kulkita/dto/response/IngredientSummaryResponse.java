package com.example.kulkita.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IngredientSummaryResponse {
    private String ingredientName;
    private long totalBatches;
    private BigDecimal totalWeight;
    private String unit;
    private String category;
    private long greenBatches;
    private long yellowBatches;
    private long redBatches;
}