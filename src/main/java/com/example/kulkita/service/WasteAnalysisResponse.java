package com.example.kulkita.service;

import com.example.kulkita.dto.response.WasteCategoryDto;

import java.util.List;

public class WasteAnalysisResponse {
    private double totalWasteWeight;
    private double economicLoss;
    private List<WasteCategoryDto> wasteCategories;

    public WasteAnalysisResponse(double totalWasteWeight, double economicLoss, List<WasteCategoryDto> wasteCategories) {
        this.totalWasteWeight = totalWasteWeight;
        this.economicLoss = economicLoss;
        this.wasteCategories = wasteCategories;
    }

    public double getTotalWasteWeight() { return totalWasteWeight; }
    public double getEconomicLoss() { return economicLoss; }
    public List<WasteCategoryDto> getWasteCategories() { return wasteCategories; }
}
