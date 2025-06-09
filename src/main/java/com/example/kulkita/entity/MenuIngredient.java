package com.example.kulkita.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "menu_ingredients")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    @Column(name = "ingredient_name", nullable = false, length = 100)
    private String ingredientName;

    @Column(name = "weight_per_portion", nullable = false, precision = 10, scale = 3)
    private BigDecimal weightPerPortion;

    @Column(name = "unit", nullable = false, length = 20)
    private String unit;

    @Column(name = "notes", length = 200)
    private String notes;
}