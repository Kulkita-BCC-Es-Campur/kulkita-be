package com.example.kulkita.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuResponse {
    private String id;
    private String name;
    private String description;
    private String category;
    private boolean active;
    private List<MenuIngredientResponse> ingredients;
    private Instant createdAt;
    private Instant updatedAt;
}