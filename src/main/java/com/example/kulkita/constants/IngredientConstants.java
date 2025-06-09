package com.example.kulkita.constants;

public final class IngredientConstants {

    private IngredientConstants() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static final class CategoryNames {
        private CategoryNames() {}

        public static final String VEGETABLES = "SAYURAN";
        public static final String FRUITS = "BUAH";
        public static final String PROTEIN = "PROTEIN";
        public static final String STAPLES = "BAHAN_POKOK";
        public static final String OTHER = "LAINNYA";
    }

    public static final class CategoryDisplayNames {
        private CategoryDisplayNames() {}

        public static final String VEGETABLES_DISPLAY = "Sayuran";
        public static final String FRUITS_DISPLAY = "Buah";
        public static final String PROTEIN_DISPLAY = "Protein";
        public static final String STAPLES_DISPLAY = "Bahan Pokok";
        public static final String OTHER_DISPLAY = "Lainnya";
    }

    public static final class DefaultShelfLife {
        private DefaultShelfLife() {}

        public static final int BAYAM_DAYS = 3;
        public static final int KANGKUNG_DAYS = 3;
        public static final int SAWI_DAYS = 5;
        public static final int TOMAT_DAYS = 7;
        public static final int WORTEL_DAYS = 14;
        public static final int KENTANG_DAYS = 30;
        public static final int BAWANG_MERAH_DAYS = 30;

        public static final int PISANG_DAYS = 5;
        public static final int APEL_DAYS = 14;
        public static final int JERUK_DAYS = 14;
        public static final int MANGGA_DAYS = 7;
        public static final int PEPAYA_DAYS = 5;
        public static final int SEMANGKA_DAYS = 10;

        public static final int AYAM_DAYS = 2;
        public static final int DAGING_SAPI_DAYS = 3;
        public static final int IKAN_DAYS = 1;
        public static final int TELUR_DAYS = 21;
        public static final int TAHU_DAYS = 5;
        public static final int TEMPE_DAYS = 3;

        public static final int DEFAULT_DAYS = 7;
    }

    public static final class Units {
        private Units() {}

        public static final String KILOGRAM = "kg";
        public static final String GRAM = "g";
        public static final String LITER = "l";
        public static final String MILLILITER = "ml";
        public static final String PIECE = "pcs";
        public static final String PACK = "pack";
    }

    public static final class IngredientPatterns {
        private IngredientPatterns() {}

        public static final String VEGETABLE_PATTERN =
                ".*(bayam|kangkung|sawi|selada|tomat|timun|wortel|kentang|bawang|cabai|terong|kubis|buncis|jagung|pakis).*";

        public static final String FRUIT_PATTERN =
                ".*(pisang|apel|jeruk|mangga|pepaya|semangka|anggur|strawberry|melon|nanas|salak|rambutan).*";

        public static final String PROTEIN_PATTERN =
                ".*(ayam|daging|ikan|udang|telur|tahu|tempe|cumi|bebek).*";

        public static final String STAPLES_PATTERN =
                ".*(beras|tepung|gula|garam|minyak|roti|pasta|mie|ubi|santan|kecap).*";
    }
}
