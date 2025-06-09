package com.example.kulkita.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsageHistoryResponse {
    private String id;
    private String batchCode;
    private String ingredientName;
    private BigDecimal usedWeight;
    private String usageType;
    private String menuName;
    private Integer portionCount;
    private String notes;
    private Instant usageDate;
    private String userName;
}