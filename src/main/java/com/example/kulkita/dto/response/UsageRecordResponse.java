package com.example.kulkita.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Setter
@Getter
public class UsageRecordResponse {
    private String message;
    private List<String> batchesUsed;
    private BigDecimal totalAmountUsed;
    private String ingredientName;
    private Instant usageTime;
    private boolean fifoCompliant;

    public UsageRecordResponse() {}

    public UsageRecordResponse(String message, List<String> batchesUsed, BigDecimal totalAmountUsed,
                               String ingredientName, Instant usageTime, boolean fifoCompliant) {
        this.message = message;
        this.batchesUsed = batchesUsed;
        this.totalAmountUsed = totalAmountUsed;
        this.ingredientName = ingredientName;
        this.usageTime = usageTime;
        this.fifoCompliant = fifoCompliant;
    }

}