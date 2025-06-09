package com.example.kulkita.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class AvailableIngredientResponse {
    private String ingredientName;
    private BigDecimal totalStock;
    private String unit;
    private String batchCode;
    private int availableBatches;
    private String freshnessStatus;
    private String storageLocation;

    public AvailableIngredientResponse() {}

    public AvailableIngredientResponse(String ingredientName, BigDecimal totalStock, String unit,
                                       String batchCode, int availableBatches, String freshnessStatus,
                                       String storageLocation) {
        this.ingredientName = ingredientName;
        this.totalStock = totalStock;
        this.unit = unit;
        this.batchCode = batchCode;
        this.availableBatches = availableBatches;
        this.freshnessStatus = freshnessStatus;
        this.storageLocation = storageLocation;
    }

}