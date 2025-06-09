package com.example.kulkita.dto.response;

public class WasteCategoryDto {
    private String category;
    private double weight;

    public WasteCategoryDto(String category, double weight) {
        this.category = category;
        this.weight = weight;
    }

    public String getCategory() {
        return category;
    }

    public double getWeight() {
        return weight;
    }
}
