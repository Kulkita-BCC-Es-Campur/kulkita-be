package com.example.kulkita.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategorySummaryResponse {
    private String category;
    private long batchCount;
    private BigDecimal totalWeight;
    private String unit;
}