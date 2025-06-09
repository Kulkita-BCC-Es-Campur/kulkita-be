package com.example.kulkita.constants;

public final class StorageConstants {

    private StorageConstants() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static final class LocationNames {
        private LocationNames() {}

        public static final String REFRIGERATOR = "kulkas";
        public static final String REFRIGERATOR_EN = "refrigerator";
        public static final String FREEZER = "freezer";
        public static final String PANTRY = "gudang";
        public static final String PANTRY_EN = "pantry";
        public static final String OTHER = "lainnya";
    }

    public static final class LocationCodes {
        private LocationCodes() {}

        public static final String REFRIGERATOR_CODE = "RFG";
        public static final String FREEZER_CODE = "FRZ";
        public static final String PANTRY_CODE = "PNT";
        public static final String OTHER_CODE = "OTR";
    }

    public static final class CapacityLimits {
        private CapacityLimits() {}

        public static final double STANDARD_REFRIGERATOR_CAPACITY_KG = 50.0;
        public static final double STANDARD_FREEZER_CAPACITY_KG = 30.0;
        public static final double STANDARD_PANTRY_CAPACITY_KG = 100.0;
    }
}
