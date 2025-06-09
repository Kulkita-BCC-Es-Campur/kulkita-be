package com.example.kulkita.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuIngredientResponse {
    private String id;
    private String ingredientName;
    private BigDecimal weightPerPortion;
    private String unit;
    private String notes;
}