package com.example.kulkita.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FifoPickingRecommendationResponse {
    private String ingredientName;
    private BigDecimal requestedAmount;
    private String unit;
    private boolean canFulfill;
    private BigDecimal totalAvailable;
    private BigDecimal shortage;
    private String message;
    private List<PickingInstructionDto> pickingInstructions;
    private List<String> warnings;
}