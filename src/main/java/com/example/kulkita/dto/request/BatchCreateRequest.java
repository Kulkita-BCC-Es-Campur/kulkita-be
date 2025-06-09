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
public class BatchCreateRequest {

    @NotBlank(message = "Ingredient name is required")
    @Size(max = 100, message = "Ingredient name must not exceed 100 characters")
    private String ingredientName;

    @Size(max = 50, message = "Category must not exceed 50 characters")
    private String category;

    @NotNull(message = "Weight is required")
    @DecimalMin(value = "0.001", message = "Weight must be greater than 0")
    private BigDecimal weight;

    @NotBlank(message = "Unit is required")
    @Size(max = 20, message = "Unit must not exceed 20 characters")
    private String unit;

    @NotBlank(message = "Source is required")
    @Size(max = 100, message = "Source must not exceed 100 characters")
    private String source;

    @NotBlank(message = "Storage location is required")
    @Size(max = 50, message = "Storage location must not exceed 50 characters")
    private String storageLocation;

    @Size(max = 500, message = "Notes must not exceed 500 characters")
    private String notes;
}