package com.example.kulkita.dto.response;

public class DailyUsageDto {
    private String ingredientName;
    private double totalUsed;

    public DailyUsageDto(String ingredientName, double totalUsed) {
        this.ingredientName = ingredientName;
        this.totalUsed = totalUsed;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public double getTotalUsed() {
        return totalUsed;
    }
}
