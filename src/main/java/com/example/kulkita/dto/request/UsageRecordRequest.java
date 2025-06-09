package com.example.kulkita.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsageRecordRequest {

    @NotBlank(message = "Batch code is required")
    private String batchCode;

    @NotNull(message = "Used weight is required")
    @DecimalMin(value = "0.001", message = "Used weight must be greater than 0")
    private BigDecimal usedWeight;

    @NotBlank(message = "Usage type is required")
    private String usageType;

    private String menuName;

    private Integer portionCount;

    @Size(max = 500, message = "Notes must not exceed 500 characters")
    private String notes;
}