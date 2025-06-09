package com.example.kulkita.constants;

public final class ApiConstants {

    private ApiConstants() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static final class Endpoints {
        private Endpoints() {}

        public static final String API_BASE = "/api";
        public static final String AUTH_BASE = "/api/auth";
        public static final String BATCHES_BASE = "/api/batches";
        public static final String MENUS_BASE = "/api/menus";
        public static final String NOTIFICATIONS_BASE = "/api/notifications";
        public static final String DASHBOARD_BASE = "/api/dashboard";
        public static final String FIFO_BASE = "/api/fifo";
        public static final String USAGE_BASE = "/api/usage";
        public static final String WEIGHT_SCALE_BASE = "/api/weight-scale";
    }

    public static final class Headers {
        private Headers() {}

        public static final String AUTHORIZATION = "Authorization";
        public static final String CONTENT_TYPE = "Content-Type";
        public static final String ACCEPT = "accept";
        public static final String BEARER_PREFIX = "Bearer ";

        public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
        public static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
        public static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
        public static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
        public static final String ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";
        public static final String ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age";
    }

    public static final class Timeouts {
        private Timeouts() {}

        public static final int AZURE_ML_TIMEOUT_MS = 10000;

        public static final int EMAIL_SERVICE_TIMEOUT_MS = 5000;

        public static final int FIREBASE_TIMEOUT_MS = 3000;

        public static final int DEFAULT_HTTP_TIMEOUT_MS = 5000;
    }

    public static final class ResponseFormats {
        private ResponseFormats() {}

        public static final String CSV_FORMAT = "csv";
        public static final String EXCEL_FORMAT = "excel";
        public static final String JSON_FORMAT = "json";
        public static final String PDF_FORMAT = "pdf";
    }

    public static final class FileTypes {
        private FileTypes() {}

        public static final String CSV_EXTENSION = ".csv";
        public static final String EXCEL_EXTENSION = ".xls";
        public static final String PDF_EXTENSION = ".pdf";

        public static final String CSV_CONTENT_TYPE = "text/csv";
        public static final String EXCEL_CONTENT_TYPE = "application/vnd.ms-excel";
        public static final String PDF_CONTENT_TYPE = "application/pdf";
        public static final String JSON_CONTENT_TYPE = "application/json";
    }
}
