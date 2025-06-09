package com.example.kulkita.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PickingInstructionDto {
    private String batchCode;
    private BigDecimal amountToUse;
    private String unit;
    private Instant expiryDate;
    private String urgencyLevel;
    private String pickingNote;
    private int sequence;
    private String storageLocation;
}