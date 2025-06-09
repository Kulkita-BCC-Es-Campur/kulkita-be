package com.example.kulkita.config;

import com.example.kulkita.entity.Batch;
import com.example.kulkita.repository.BatchRepository;
import com.example.kulkita.service.SpoilagePredictionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@Order(1) // Harus dijalankan SEBELUM MenuSeeder (Order 2)
public class BatchSeederMBG implements CommandLineRunner {
    private final BatchRepository batchRepository;
    private final SpoilagePredictionService predictionService;
    private final java.util.Random random = new java.util.Random();

    private static final Logger log = LoggerFactory.getLogger(BatchSeederMBG.class);

    public BatchSeederMBG(BatchRepository batchRepository, SpoilagePredictionService predictionService) {
        this.batchRepository = batchRepository;
        this.predictionService = predictionService;

        log.info("=== BatchSeederMBG CONSTRUCTOR CALLED ===");
        log.info("BatchRepository: {}", batchRepository != null ? "OK" : "NULL");
        log.info("SpoilagePredictionService: {}", predictionService != null ? "OK" : "NULL");
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("=== BatchSeederMBG RUN METHOD CALLED ===");
        log.info("Arguments received: {}", Arrays.toString(args));

        try {
            long existingCount = batchRepository.count();
            log.info("Current batch count: {}", existingCount);

            if (existingCount == 0) {
                log.info("Seeding batch data for MBG menu ingredients...");
                seedBatchData();
                log.info("Batch seeding completed successfully!");
            } else {
                log.info("Batch data already exists, skipping seeding. Count: {}", existingCount);
            }
        } catch (Exception e) {
            log.error("Error during batch seeding: ", e);
            throw e;
        }

        log.info("=== BatchSeederMBG RUN METHOD COMPLETED ===");
    }

    private void seedBatchData() {
        List<Batch> batches = new ArrayList<>();

        // Protein Hewani - Harus ada untuk menu
        batches.addAll(createProteinHewaniBatches());

        // Protein Nabati - Untuk menu vegetarian
        batches.addAll(createProteinNabatiBatches());

        // Sayuran - Untuk semua menu sayur
        batches.addAll(createSAYURANBatches());

        // Buah - Untuk pencuci mulut di setiap menu
        batches.addAll(createBUAHBatches());

        // Karbohidrat - Base untuk semua menu (BERAS sangat penting)
        batches.addAll(createKarbohidratBatches());

        // Bumbu dan bahan pokok - Untuk seasoning dan memasak
        batches.addAll(createBumbuBatches());

        batchRepository.saveAll(batches);
        log.info("Successfully seeded {} batch items for MBG menu ingredients", batches.size());
    }

    private List<Batch> createProteinHewaniBatches() {
        List<Batch> batches = new ArrayList<>();

        // AYAM - Ingredient paling banyak digunakan di menu (12 menu menggunakan ayam)
        batches.add(createBatch("Ayam", "30.0", "kg", "Ayam Potong Fresh", "PT Unggas Nusantara", "kulkas", "PROTEIN", "Ayam segar untuk menu MBG sekolah"));
        batches.add(createBatch("Ayam", "25.0", "kg", "Ayam Kampung", "Peternak Lokal Bogor", "kulkas", "PROTEIN", "Ayam kampung berkualitas"));
        batches.add(createBatch("Ayam", "20.0", "kg", "Ayam Broiler", "CV Ternak Jaya", "kulkas", "PROTEIN", "Untuk menu ayam semur dan goreng"));

        // DAGING SAPI - Untuk menu daging sapi tumis
        batches.add(createBatch("Daging Sapi", "15.0", "kg", "Daging Sapi Segar", "RPH Bandung", "kulkas", "PROTEIN", "Untuk menu sapi tumis"));
        batches.add(createBatch("Daging Sapi", "12.0", "kg", "Daging Has Dalam", "Peternak Jawa Tengah", "kulkas", "PROTEIN", "Daging premium untuk menu berkualitas"));

        // IKAN - Untuk menu ikan goreng dan asam manis
        batches.add(createBatch("Ikan", "18.0", "kg", "Ikan Bandeng", "Nelayan Sidoarjo", "kulkas", "PROTEIN", "Ikan segar untuk menu ikan goreng"));
        batches.add(createBatch("Ikan", "16.0", "kg", "Ikan Nila", "Budidaya Cianjur", "kulkas", "PROTEIN", "Ikan air tawar untuk menu sekolah"));
        batches.add(createBatch("Ikan", "14.0", "kg", "Ikan Lele", "Kolam Rakyat", "kulkas", "PROTEIN", "Untuk menu ikan asam manis"));

        // TELUR - Untuk menu telur dadar dan gado-gado
        batches.add(createBatch("Telur", "15.0", "kg", "Telur Ayam Negeri", "Peternakan Sukabumi", "kulkas", "PROTEIN", "Telur grade A untuk menu sekolah"));
        batches.add(createBatch("Telur", "12.0", "kg", "Telur Omega-3", "PT Charoen Pokphand", "kulkas", "PROTEIN", "Telur dengan kandungan omega-3"));

        // Protein lainnya (opsional untuk variasi)
        batches.add(createBatch("Cumi-Cumi", "6.0", "kg", "Cumi Segar", "Nelayan Maluku", "freezer", "PROTEIN", "Untuk menu seafood variasi"));
        batches.add(createBatch("Bebek", "8.0", "kg", "Bebek Potong", "Peternak Jawa Timur", "kulkas", "PROTEIN", "Bebek untuk menu regional"));

        return batches;
    }

    private List<Batch> createProteinNabatiBatches() {
        List<Batch> batches = new ArrayList<>();

        // TAHU - Sangat penting, digunakan di banyak menu
        batches.add(createBatch("Tahu", "25.0", "kg", "Tahu Putih", "Pabrik Tahu Bandung", "kulkas", "PROTEIN", "Tahu putih segar untuk menu sekolah"));
        batches.add(createBatch("Tahu", "20.0", "kg", "Tahu Sumedang", "UD Tahu Asli", "kulkas", "PROTEIN", "Tahu khas Sumedang"));

        // TEMPE - Untuk menu vegetarian dan protein nabati
        batches.add(createBatch("Tempe", "20.0", "kg", "Tempe Kedelai", "Pengrajin Tempe Malang", "kulkas", "PROTEIN", "Tempe segar dari kedelai lokal"));
        batches.add(createBatch("Tempe", "15.0", "kg", "Tempe Organik", "Koperasi Tani", "kulkas", "PROTEIN", "Tempe organik berkualitas"));

        // KACANG - Untuk bumbu gado-gado
        batches.add(createBatch("Kacang Tanah", "8.0", "kg", "Kacang Tanah Kupas", "Petani Tuban", "gudang", "PROTEIN", "Untuk bumbu gado-gado"));
        batches.add(createBatch("Kacang Panjang", "12.0", "kg", "Kacang Panjang Segar", "Kebun Sayur Lembang", "kulkas", "SAYURAN", "Sayuran segar untuk tumis"));

        return batches;
    }

    private List<Batch> createSAYURANBatches() {
        List<Batch> batches = new ArrayList<>();

        // SAYURAN HIJAU - Untuk menu tumis sayur
        batches.add(createBatch("Kangkung", "15.0", "kg", "Kangkung Darat", "Petani Bogor", "kulkas", "SAYURAN", "Kangkung segar untuk tumis"));
        batches.add(createBatch("Bayam", "12.0", "kg", "Bayam Hijau", "Kebun Organik", "kulkas", "SAYURAN", "Bayam hijau kaya zat besi"));
        batches.add(createBatch("Sawi", "15.0", "kg", "Sawi Hijau", "Petani Dieng", "kulkas", "SAYURAN", "Sawi segar dari dataran tinggi"));

        // SAYURAN UNTUK SUP DAN TUMIS
        batches.add(createBatch("Wortel", "20.0", "kg", "Wortel Orange", "Kebun Batu", "kulkas", "SAYURAN", "Wortel segar kaya vitamin A"));
        batches.add(createBatch("Kentang", "25.0", "kg", "Kentang Granola", "Petani Dieng", "gudang", "SAYURAN", "Kentang kualitas export"));
        batches.add(createBatch("Kubis", "18.0", "kg", "Kubis Putih", "Petani Lembang", "kulkas", "SAYURAN", "Kubis putih untuk sup"));

        // SAYURAN PELENGKAP
        batches.add(createBatch("Tomat", "15.0", "kg", "Tomat Merah", "Kebun Ungaran", "kulkas", "SAYURAN", "Tomat merah segar"));
        batches.add(createBatch("Timun", "10.0", "kg", "Timun Lalap", "Petani Sukabumi", "kulkas", "SAYURAN", "Timun untuk lalapan dan gado-gado"));
        batches.add(createBatch("Terong", "8.0", "kg", "Terong Ungu", "Kebun Sayur", "kulkas", "SAYURAN", "Terong ungu segar"));

        // SAYURAN KHUSUS
        batches.add(createBatch("Buncis", "8.0", "kg", "Buncis Muda", "Kebun Organik", "kulkas", "SAYURAN", "Buncis muda dan segar"));
        batches.add(createBatch("Jagung", "12.0", "kg", "Jagung Manis", "Petani Kediri", "kulkas", "SAYURAN", "Jagung manis untuk sayur"));

        // SAYURAN TRADISIONAL
        batches.add(createBatch("Pakis", "6.0", "kg", "Pakis Muda", "Hutan Rakyat", "kulkas", "SAYURAN", "Pakis muda untuk menu tradisional"));
        batches.add(createBatch("Nangka Muda", "10.0", "kg", "Nangka Muda", "Kebun Yogya", "kulkas", "SAYURAN", "Untuk gudeg anak"));

        return batches;
    }

    private List<Batch> createBUAHBatches() {
        List<Batch> batches = new ArrayList<>();

        // BUAH UTAMA - Untuk pencuci mulut setiap menu
        batches.add(createBatch("Jeruk", "20.0", "kg", "Jeruk Manis", "Kebun Batu", "gudang", "BUAH", "Jeruk manis untuk vitamin C"));
        batches.add(createBatch("Pisang", "25.0", "kg", "Pisang Ambon", "Kebun Lampung", "gudang", "BUAH", "Pisang manis untuk anak"));
        batches.add(createBatch("Pepaya", "20.0", "kg", "Pepaya California", "Kebun Bogor", "kulkas", "BUAH", "Pepaya manis kaya papain"));
        batches.add(createBatch("Mangga", "18.0", "kg", "Mangga Harum Manis", "Kebun Cirebon", "kulkas", "BUAH", "Mangga harum manis"));
        batches.add(createBatch("Semangka", "30.0", "kg", "Semangka Kuning", "Petani Demak", "kulkas", "BUAH", "Semangka manis kuning"));

        // BUAH PREMIUM
        batches.add(createBatch("Apel", "15.0", "kg", "Apel Fuji", "Kebun Malang", "kulkas", "BUAH", "Apel fuji import quality"));
        batches.add(createBatch("Melon", "12.0", "kg", "Melon Hijau", "Kebun Gresik", "kulkas", "BUAH", "Melon hijau segar"));
        batches.add(createBatch("Anggur", "8.0", "kg", "Anggur Hijau", "Kebun Probolinggo", "kulkas", "BUAH", "Anggur hijau seedless"));

        // BUAH LOKAL
        batches.add(createBatch("Salak", "10.0", "kg", "Salak Pondoh", "Petani Sleman", "gudang", "BUAH", "Salak pondoh manis"));
        batches.add(createBatch("Rambutan", "8.0", "kg", "Rambutan Merah", "Kebun Riau", "kulkas", "BUAH", "Rambutan merah segar"));
        batches.add(createBatch("Nanas", "12.0", "kg", "Nanas Madu", "Petani Subang", "kulkas", "BUAH", "Nanas madu untuk menu asam manis"));

        // BUAH KHUSUS DAN BUMBU
        batches.add(createBatch("Jeruk Nipis", "3.0", "kg", "Jeruk Nipis", "Petani Purworejo", "kulkas", "BUAH", "Untuk bumbu mie ayam"));
        batches.add(createBatch("BUAH Merah", "3.0", "kg", "BUAH Merah Papua", "Petani Papua", "freezer", "BUAH", "BUAH merah khas Papua"));

        return batches;
    }

    private List<Batch> createKarbohidratBatches() {
        List<Batch> batches = new ArrayList<>();

        // BERAS - SANGAT PENTING! Digunakan di hampir semua menu sebagai "Nasi"
        batches.add(createBatch("Beras", "100.0", "kg", "Beras Premium IR64", "Bulog", "gudang", "BAHAN_POKOK", "Beras putih premium untuk nasi"));
        batches.add(createBatch("Beras", "80.0", "kg", "Beras Ciherang", "Gapoktan Sukabumi", "gudang", "BAHAN_POKOK", "Beras varietas ciherang"));
        batches.add(createBatch("Beras", "60.0", "kg", "Beras Organik", "Petani Organik Jawa", "gudang", "BAHAN_POKOK", "Beras organik berkualitas"));

        // MIE - Untuk menu mie ayam
        batches.add(createBatch("Mie", "15.0", "kg", "Mie Telur", "Pabrik Mie Bandung", "gudang", "BAHAN_POKOK", "Mie telur untuk mie ayam"));

        // KARBOHIDRAT ALTERNATIF
        batches.add(createBatch("Ubi Jalar", "15.0", "kg", "Ubi Kuning", "Petani Papua", "gudang", "BAHAN_POKOK", "Ubi jalar kuning manis"));

        // TEPUNG - Untuk ayam goreng tepung
        batches.add(createBatch("Tepung Terigu", "25.0", "kg", "Tepung Protein Sedang", "PT Bogasari", "gudang", "BAHAN_POKOK", "Untuk ayam goreng tepung"));
        batches.add(createBatch("Tepung Tapioka", "8.0", "kg", "Tepung Kanji", "Pabrik Singkong", "gudang", "BAHAN_POKOK", "Untuk pangsit dan bakso"));

        return batches;
    }

    private List<Batch> createBumbuBatches() {
        List<Batch> batches = new ArrayList<>();

        // BUMBU DASAR - Sangat penting untuk semua masakan
        batches.add(createBatch("Bawang Merah", "12.0", "kg", "Bawang Merah Brebes", "Petani Brebes", "gudang", "BAHAN_POKOK", "Bawang merah kualitas export"));
        batches.add(createBatch("Bawang Putih", "8.0", "kg", "Bawang Putih Tegal", "Importir", "gudang", "BAHAN_POKOK", "Bawang putih segar"));
        batches.add(createBatch("Bawang Bombay", "6.0", "kg", "Bawang Bombay", "Importir Jakarta", "kulkas", "BAHAN_POKOK", "Bawang bombay untuk sup"));

        // REMPAH TRADISIONAL - Untuk menu bumbu kuning
        batches.add(createBatch("Kunyit", "3.0", "kg", "Kunyit Segar", "Petani Sukabumi", "kulkas", "BAHAN_POKOK", "Kunyit segar untuk bumbu kuning"));
        batches.add(createBatch("Jahe", "4.0", "kg", "Jahe Merah", "Petani Dieng", "gudang", "BAHAN_POKOK", "Jahe merah berkhasiat"));
        batches.add(createBatch("Serai", "3.0", "kg", "Serai Wangi", "Kebun Rempah", "kulkas", "BAHAN_POKOK", "Serai untuk bumbu tradisional"));

        // CABAI DAN BUMBU PEDAS
        batches.add(createBatch("Cabai Merah", "5.0", "kg", "Cabai Keriting", "Petani Garut", "kulkas", "BAHAN_POKOK", "Cabai merah untuk sambal"));

        // PEMANIS
        batches.add(createBatch("Gula", "15.0", "kg", "Gula Kristal Putih", "Pabrik Gula", "gudang", "BAHAN_POKOK", "Gula kristal putih"));
        batches.add(createBatch("Gula Merah", "8.0", "kg", "Gula Aren", "Pengrajin Tasikmalaya", "gudang", "BAHAN_POKOK", "Gula aren untuk tahu tempe bacem"));

        // MINYAK DAN SANTAN - Untuk memasak
        batches.add(createBatch("Minyak Goreng", "30.0", "kg", "Minyak Sawit", "PT Sinar Mas", "gudang", "BAHAN_POKOK", "Minyak goreng untuk semua menu"));
        batches.add(createBatch("Santan", "12.0", "kg", "Santan Kelapa", "Pabrik Santan", "kulkas", "BAHAN_POKOK", "Santan kental untuk gudeg"));

        // BUMBU PELENGKAP
        batches.add(createBatch("Kecap Manis", "5.0", "kg", "Kecap Bango", "PT Unilever", "gudang", "BAHAN_POKOK", "Kecap manis untuk tempe bacem dan mie"));

        // REMPAH KHUSUS
        batches.add(createBatch("Kemiri", "2.0", "kg", "Kemiri Kupas", "Petani Sulawesi", "gudang", "BAHAN_POKOK", "Kemiri untuk bumbu"));
        batches.add(createBatch("Daun Salam", "1.0", "kg", "Daun Salam Kering", "Petani Rempah", "gudang", "BAHAN_POKOK", "Daun salam untuk gudeg"));
        batches.add(createBatch("Asam Jawa", "3.0", "kg", "Asam Jawa Tanpa Biji", "Petani Jawa Tengah", "gudang", "BAHAN_POKOK", "Asam jawa untuk ikan asam manis"));

        // INGREDIENT KHUSUS MENU
        batches.add(createBatch("Pangsit", "5.0", "kg", "Pangsit Siap Pakai", "Pabrik Pangsit", "freezer", "PROTEIN", "Pangsit untuk mie ayam"));
        batches.add(createBatch("Kulit Sapi", "3.0", "kg", "Krecek Kering", "Pengrajin Solo", "gudang", "PROTEIN", "Krecek untuk gudeg"));
        batches.add(createBatch("Ikan Asin", "2.0", "kg", "Ikan Asin Peda", "Nelayan Jepara", "gudang", "PROTEIN", "Ikan asin untuk sayur asem"));

        return batches;
    }

    private Batch createBatch(String ingredientName, String weight, String unit, String source,
                              String supplier, String storageLocation, String category, String notes) {

        String batchCode = generateBatchCode(ingredientName, storageLocation);

        Batch batch = new Batch();
        batch.setBatchCode(batchCode);
        batch.setIngredientName(ingredientName);
        batch.setCategory(Batch.Category.valueOf(category));
        batch.setWeight(new BigDecimal(weight));
        batch.setUnit(unit);
        batch.setSource(supplier);
        batch.setStorageLocation(storageLocation);
        batch.setNotes(notes + " - " + source);
        batch.setActive(true);
        batch.setEntryDate(Instant.now().minusSeconds(random.nextInt(259200)));

        SpoilagePredictionService.PredictionResult prediction = predictionService.predictSpoilage(
                ingredientName, storageLocation, batch.getEntryDate(), null);

        batch.setExpiryDate(prediction.getExpiryDate());
        batch.setFreshnessStatus(prediction.getFreshnessStatus());

        return batch;
    }

    private String generateBatchCode(String ingredientName, String storageLocation) {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String locationCode = getLocationCode(storageLocation);
        String ingredientCode = ingredientName.substring(0, Math.min(3, ingredientName.length())).toUpperCase();
        int randomSerial = 100 + random.nextInt(900);

        return ingredientCode + "-" + today + "-" + locationCode + "-" + String.format("%03d", randomSerial);
    }

    private String getLocationCode(String storageLocation) {
        return switch (storageLocation.toLowerCase()) {
            case "kulkas", "refrigerator" -> "RFG";
            case "freezer" -> "FRZ";
            case "gudang", "pantry" -> "PNT";
            default -> "OTR";
        };
    }
}