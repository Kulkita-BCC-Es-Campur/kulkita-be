package com.example.kulkita.constants;

public class ConstantsUsageExample {

    public String calculateUrgencyLevelOld(long daysUntilExpiry) {
        if (daysUntilExpiry <= 1) {
            return "CRITICAL";
        } else if (daysUntilExpiry <= 3) {
            return "HIGH";
        } else if (daysUntilExpiry <= 7) {
            return "MEDIUM";
        } else {
            return "LOW";
        }
    }

    public String calculateUrgencyLevelNew(long daysUntilExpiry) {
        if (daysUntilExpiry <= BusinessConstants.FreshnessPeriods.CRITICAL_DAYS_THRESHOLD) {
            return "CRITICAL";
        } else if (daysUntilExpiry <= BusinessConstants.FreshnessPeriods.HIGH_PRIORITY_DAYS_THRESHOLD) {
            return "HIGH";
        } else if (daysUntilExpiry <= BusinessConstants.FreshnessPeriods.MEDIUM_PRIORITY_DAYS_THRESHOLD) {
            return "MEDIUM";
        } else {
            return "LOW";
        }
    }

    public String getLocationCodeOld(String storageLocation) {
        return switch (storageLocation.toLowerCase()) {
            case "kulkas", "refrigerator" -> "RFG";
            case "freezer" -> "FRZ";
            case "gudang", "pantry" -> "PNT";
            default -> "OTR";
        };
    }

    public String getLocationCodeNew(String storageLocation) {
        return switch (storageLocation.toLowerCase()) {
            case StorageConstants.LocationNames.REFRIGERATOR,
                 StorageConstants.LocationNames.REFRIGERATOR_EN ->
                    StorageConstants.LocationCodes.REFRIGERATOR_CODE;
            case StorageConstants.LocationNames.FREEZER ->
                    StorageConstants.LocationCodes.FREEZER_CODE;
            case StorageConstants.LocationNames.PANTRY,
                 StorageConstants.LocationNames.PANTRY_EN ->
                    StorageConstants.LocationCodes.PANTRY_CODE;
            default -> StorageConstants.LocationCodes.OTHER_CODE;
        };
    }

    public Double getDefaultTemperatureOld(String storageLocation) {
        return switch (storageLocation.toLowerCase()) {
            case "kulkas", "refrigerator" -> 4.0;
            case "freezer" -> -18.0;
            case "gudang", "pantry" -> 25.0;
            default -> 25.0;
        };
    }

    public Double getDefaultTemperatureNew(String storageLocation) {
        return switch (storageLocation.toLowerCase()) {
            case StorageConstants.LocationNames.REFRIGERATOR,
                 StorageConstants.LocationNames.REFRIGERATOR_EN ->
                    BusinessConstants.StorageTemperatures.REFRIGERATOR_TEMPERATURE;
            case StorageConstants.LocationNames.FREEZER ->
                    BusinessConstants.StorageTemperatures.FREEZER_TEMPERATURE;
            case StorageConstants.LocationNames.PANTRY,
                 StorageConstants.LocationNames.PANTRY_EN ->
                    BusinessConstants.StorageTemperatures.PANTRY_TEMPERATURE;
            default -> BusinessConstants.StorageTemperatures.ROOM_TEMPERATURE;
        };
    }

    public String assessComplianceOld(double complianceScore) {
        if (complianceScore < 70) {
            return "Poor FIFO compliance";
        } else if (complianceScore < 85) {
            return "Moderate FIFO compliance";
        } else {
            return "Good FIFO compliance";
        }
    }

    public String assessComplianceNew(double complianceScore) {
        if (complianceScore < BusinessConstants.ComplianceThresholds.POOR_COMPLIANCE_THRESHOLD) {
            return "Poor FIFO compliance";
        } else if (complianceScore < BusinessConstants.ComplianceThresholds.MODERATE_COMPLIANCE_THRESHOLD) {
            return "Moderate FIFO compliance";
        } else {
            return "Good FIFO compliance";
        }
    }
}
