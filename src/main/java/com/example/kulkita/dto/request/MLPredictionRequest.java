package com.example.kulkita.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MLPredictionRequest {
    private String ingredientName;
    private String storageLocation;
    private Instant entryDate;
    private Double temperature;
    private String category;
    private Double humidity;
}