package com.example.kulkita.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FifoStorageRecommendationResponse {
    private String storageLocation;
    private List<BatchPositionDto> recommendedPositions;
    private String organizationTips;
    private String fifoFlowDirection;
    private List<String> improvementSuggestions;
}