package com.example.kulkita.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BatchStatusUpdateRequest {

    @NotBlank(message = "Batch code is required")
    private String batchCode;

    @NotBlank(message = "Status is required")
    private String status;

    @Size(max = 500, message = "Notes must not exceed 500 characters")
    private String notes;
}