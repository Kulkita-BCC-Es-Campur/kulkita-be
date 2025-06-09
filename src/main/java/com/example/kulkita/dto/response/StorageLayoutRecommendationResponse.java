package com.example.kulkita.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StorageLayoutRecommendationResponse {
    private String storageLocation;
    private List<IngredientLayoutDto> ingredientLayouts;
}