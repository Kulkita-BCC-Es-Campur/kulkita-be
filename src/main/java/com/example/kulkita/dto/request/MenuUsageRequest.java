package com.example.kulkita.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuUsageRequest {

    @NotNull(message = "Menu ID is required")
    private String menuId;

    @NotNull(message = "Portion count is required")
    @DecimalMin(value = "1", message = "Portion count must be at least 1")
    private Integer portionCount;

    @Size(max = 500, message = "Notes must not exceed 500 characters")
    private String notes;
}