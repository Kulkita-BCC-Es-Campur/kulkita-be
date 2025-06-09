package com.example.kulkita.constants;


public final class BusinessConstants {

    private BusinessConstants() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static final class FreshnessPeriods {
        private FreshnessPeriods() {}

        public static final int CRITICAL_DAYS_THRESHOLD = 1;
        public static final int HIGH_PRIORITY_DAYS_THRESHOLD = 3;
        public static final int MEDIUM_PRIORITY_DAYS_THRESHOLD = 7;

        public static final double GREEN_FRESHNESS_THRESHOLD = 0.7;
        public static final double YELLOW_FRESHNESS_THRESHOLD = 0.3;

        public static final int EXPIRY_TODAY_THRESHOLD = 1;
        public static final int EXPIRY_2_DAYS_THRESHOLD = 2;
        public static final int EXPIRY_3_DAYS_THRESHOLD = 3;
    }

    public static final class ComplianceThresholds {
        private ComplianceThresholds() {}

        public static  final double MINIMUM_BATCHES_FOR_COMPLIANCE = 2.0;
        public static final double PERFECT_COMPLIANCE_SCORE = 100.0;
        public static final double POOR_COMPLIANCE_THRESHOLD = 70.0;
        public static final double MODERATE_COMPLIANCE_THRESHOLD = 85.0;
        public static final double EXCELLENT_COMPLIANCE_THRESHOLD = 95.0;

        public static final double HIGH_CRITICAL_PERCENTAGE_THRESHOLD = 10.0;
        public static final double ACCEPTABLE_WASTE_RATIO = 2.0;
    }

    public static final class StockThresholds {
        private StockThresholds() {}

        public static final double LOW_STOCK_THRESHOLD_KG = 5.0;
        public static final double CRITICAL_STOCK_THRESHOLD_KG = 1.0;

        public static final double MINIMUM_WEIGHT_THRESHOLD = 0.001;

        public static final int MAX_PICKING_INSTRUCTIONS = 10;
        public static final int MAX_CRITICAL_BATCHES_DASHBOARD = 10;
        public static final int MAX_HIGH_PRIORITY_BATCHES_DASHBOARD = 10;
    }

    public static final class StorageTemperatures {
        private StorageTemperatures() {}

        public static final double REFRIGERATOR_TEMPERATURE = 4.0;
        public static final double FREEZER_TEMPERATURE = -18.0;
        public static final double PANTRY_TEMPERATURE = 25.0;
        public static final double ROOM_TEMPERATURE = 25.0;

        public static final double TEMPERATURE_VARIATION_TOLERANCE = 2.0;
    }

    public static final class NotificationSettings {
        private NotificationSettings() {}

        public static final int MAX_OTP_ATTEMPTS_PER_HOUR = 3;
        public static final int OTP_RATE_LIMIT_WINDOW_HOURS = 1;
        public static final int OTP_LENGTH = 6;

        public static final int OTP_EXPIRY_SECONDS = 300;
        public static final int JWT_EXPIRY_MS = 86400000;

        public static final int NOTIFICATION_RETENTION_DAYS = 30;

        public static final String FIREBASE_TOPIC_SPOILAGE_ALERTS = "spoilage_alerts";
    }

    public static final class ScheduleIntervals {
        private ScheduleIntervals() {}

        public static final int FRESHNESS_UPDATE_INTERVAL_MS = 7200000;

        public static final int BATCH_CLEANUP_INTERVAL_MS = 21600000;

        public static final String TOKEN_CLEANUP_CRON = "0 0 2 * * *";

        public static final int OTP_CLEANUP_INTERVAL_HOURS = 24;
    }

    public static final class PaginationDefaults {
        private PaginationDefaults() {}

        public static final int DEFAULT_PAGE_SIZE = 20;
        public static final int DEFAULT_PAGE_NUMBER = 0;
        public static final int MAX_PAGE_SIZE = 100;

        public static final int MAX_EXPORT_RECORDS = 10000;
        public static final int AUTOCOMPLETE_LIMIT = 10;
    }

    public static final class ValidationConstraints {
        private ValidationConstraints() {}

        public static final int MAX_INGREDIENT_NAME_LENGTH = 100;
        public static final int MAX_CATEGORY_LENGTH = 50;
        public static final int MAX_UNIT_LENGTH = 20;
        public static final int MAX_SOURCE_LENGTH = 100;
        public static final int MAX_STORAGE_LOCATION_LENGTH = 50;
        public static final int MAX_NOTES_LENGTH = 500;
        public static final int MAX_USERNAME_LENGTH = 50;
        public static final int MAX_EMAIL_LENGTH = 100;
        public static final int MIN_PASSWORD_LENGTH = 8;

        public static final int BATCH_CODE_SERIAL_LENGTH = 3;
        public static final int BATCH_CODE_MIN_INGREDIENT_LENGTH = 3;
    }

    public static final class EconomicFactors {
        private EconomicFactors() {}

        public static final double AVERAGE_FOOD_COST_PER_KG = 25000.0;

        public static final double FREEZER_SHELF_LIFE_MULTIPLIER = 30.0;
        public static final double REFRIGERATOR_SHELF_LIFE_MULTIPLIER = 3.0;
        public static final double PANTRY_SHELF_LIFE_MULTIPLIER = 1.0;

        public static final double FRONT_POSITION_THRESHOLD = 0.3;
        public static final double MIDDLE_POSITION_THRESHOLD = 0.7;

        public static final double EXCELLENT_ACCESSIBILITY_THRESHOLD = 80.0;
        public static final double GOOD_ACCESSIBILITY_THRESHOLD = 60.0;
        public static final double FAIR_ACCESSIBILITY_THRESHOLD = 40.0;
    }
}

