package com.example.kulkita.config;

import com.example.kulkita.entity.Menu;
import com.example.kulkita.entity.MenuIngredient;
import com.example.kulkita.repository.MenuRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
@Order(2)
public class MenuSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(MenuSeeder.class);

    private final MenuRepository menuRepository;

    public MenuSeeder(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (menuRepository.count() == 0) {
            log.info("Seeding menu data for Makan Bergizi Gratis (MBG) program...");
            seedMenuData();
            log.info("Menu seeding completed successfully!");
        } else {
            log.info("Menu data already exists, skipping seeding");
        }
    }

    private void seedMenuData() {
        List<Menu> menus = new ArrayList<>();

        Menu menu1 = createMenu(
                "Ayam Semur + Tumis Kacang Panjang + Nasi + Jeruk",
                "Menu bergizi lengkap dengan protein hewani, sayuran, karbohidrat, dan buah",
                "Protein Hewani"
        );
        menu1.setIngredients(List.of(
                createIngredient(menu1, "Ayam", new BigDecimal("0.08"), "kg"),
                createIngredient(menu1, "Kacang Panjang", new BigDecimal("0.05"), "kg"),
                createIngredient(menu1, "Tahu", new BigDecimal("0.03"), "kg"),
                createIngredient(menu1, "Beras", new BigDecimal("0.08"), "kg"),
                createIngredient(menu1, "Jeruk", new BigDecimal("0.1"), "kg"),
                createIngredient(menu1, "Bawang Merah", new BigDecimal("0.01"), "kg"),
                createIngredient(menu1, "Bawang Putih", new BigDecimal("0.005"), "kg"),
                createIngredient(menu1, "Minyak Goreng", new BigDecimal("0.01"), "kg")
        ));
        menus.add(menu1);

        Menu menu2 = createMenu(
                "Tumis Ayam + Tahu Goreng + Tumis Sayur + Nasi + Semangka",
                "Menu bergizi dengan ayam tumis, tahu, sayuran, dan buah semangka",
                "Protein Hewani"
        );
        menu2.setIngredients(List.of(
                createIngredient(menu2, "Ayam", new BigDecimal("0.07"), "kg"),
                createIngredient(menu2, "Tahu", new BigDecimal("0.04"), "kg"),
                createIngredient(menu2, "Sayur Campuran", new BigDecimal("0.06"), "kg"),
                createIngredient(menu2, "Beras", new BigDecimal("0.08"), "kg"),
                createIngredient(menu2, "Semangka", new BigDecimal("0.1"), "kg"),
                createIngredient(menu2, "Minyak Goreng", new BigDecimal("0.01"), "kg")
        ));
        menus.add(menu2);

        Menu menu3 = createMenu(
                "Ayam Goreng Tepung + Telur + Tumis Pakis Jagung + Nasi + Semangka",
                "Menu lengkap dengan protein ganda (ayam dan telur), sayuran lokal, dan buah",
                "Protein Hewani"
        );
        menu3.setIngredients(List.of(
                createIngredient(menu3, "Ayam", new BigDecimal("0.08"), "kg"),
                createIngredient(menu3, "Telur", new BigDecimal("0.06"), "kg"),
                createIngredient(menu3, "Pakis", new BigDecimal("0.05"), "kg"),
                createIngredient(menu3, "Jagung", new BigDecimal("0.04"), "kg"),
                createIngredient(menu3, "Tepung Terigu", new BigDecimal("0.02"), "kg"),
                createIngredient(menu3, "Beras", new BigDecimal("0.08"), "kg"),
                createIngredient(menu3, "Semangka", new BigDecimal("0.1"), "kg"),
                createIngredient(menu3, "Minyak Goreng", new BigDecimal("0.015"), "kg")
        ));
        menus.add(menu3);

        Menu menu4 = createMenu(
                "Ayam Goreng Tepung + Tahu + Sayur Campuran + Nasi",
                "Menu bergizi dengan ayam crispy, tahu, dan sayuran campuran",
                "Protein Hewani"
        );
        menu4.setIngredients(List.of(
                createIngredient(menu4, "Ayam", new BigDecimal("0.08"), "kg"),
                createIngredient(menu4, "Tahu", new BigDecimal("0.04"), "kg"),
                createIngredient(menu4, "Wortel", new BigDecimal("0.03"), "kg"),
                createIngredient(menu4, "Kentang", new BigDecimal("0.04"), "kg"),
                createIngredient(menu4, "Jagung", new BigDecimal("0.03"), "kg"),
                createIngredient(menu4, "Tepung Terigu", new BigDecimal("0.02"), "kg"),
                createIngredient(menu4, "Beras", new BigDecimal("0.08"), "kg"),
                createIngredient(menu4, "Minyak Goreng", new BigDecimal("0.015"), "kg")
        ));
        menus.add(menu4);

        Menu menu5 = createMenu(
                "Ayam Goreng + Tumis Wortel + Tempe + Nasi + Pepaya",
                "Menu tradisional dengan protein nabati dan hewani, sayuran, dan buah lokal",
                "Protein Campuran"
        );
        menu5.setIngredients(List.of(
                createIngredient(menu5, "Ayam", new BigDecimal("0.08"), "kg"),
                createIngredient(menu5, "Tempe", new BigDecimal("0.05"), "kg"),
                createIngredient(menu5, "Wortel", new BigDecimal("0.05"), "kg"),
                createIngredient(menu5, "Beras", new BigDecimal("0.08"), "kg"),
                createIngredient(menu5, "Pepaya", new BigDecimal("0.1"), "kg"),
                createIngredient(menu5, "Minyak Goreng", new BigDecimal("0.01"), "kg")
        ));
        menus.add(menu5);

        Menu menu6 = createMenu(
                "Tahu Tempe Bacem + Tumis Kangkung + Nasi + Pisang",
                "Menu vegetarian bergizi dengan protein nabati lengkap",
                "Protein Nabati"
        );
        menu6.setIngredients(List.of(
                createIngredient(menu6, "Tahu", new BigDecimal("0.06"), "kg"),
                createIngredient(menu6, "Tempe", new BigDecimal("0.06"), "kg"),
                createIngredient(menu6, "Kangkung", new BigDecimal("0.06"), "kg"),
                createIngredient(menu6, "Beras", new BigDecimal("0.08"), "kg"),
                createIngredient(menu6, "Pisang", new BigDecimal("0.1"), "kg"),
                createIngredient(menu6, "Gula Merah", new BigDecimal("0.01"), "kg"),
                createIngredient(menu6, "Kecap Manis", new BigDecimal("0.01"), "kg")
        ));
        menus.add(menu6);

        Menu menu7 = createMenu(
                "Ikan Goreng + Tumis Bayam + Nasi + Mangga",
                "Menu bergizi untuk daerah pesisir dengan protein ikan",
                "Protein Ikan"
        );
        menu7.setIngredients(List.of(
                createIngredient(menu7, "Ikan", new BigDecimal("0.1"), "kg"),
                createIngredient(menu7, "Bayam", new BigDecimal("0.06"), "kg"),
                createIngredient(menu7, "Beras", new BigDecimal("0.08"), "kg"),
                createIngredient(menu7, "Mangga", new BigDecimal("0.1"), "kg"),
                createIngredient(menu7, "Tepung Terigu", new BigDecimal("0.015"), "kg"),
                createIngredient(menu7, "Minyak Goreng", new BigDecimal("0.015"), "kg")
        ));
        menus.add(menu7);

        Menu menu8 = createMenu(
                "Daging Sapi Tumis + Tahu + Sayur Sop + Nasi + Apel",
                "Menu bergizi dengan daging sapi, sayuran sup, dan buah",
                "Protein Hewani"
        );
        menu8.setIngredients(List.of(
                createIngredient(menu8, "Daging Sapi", new BigDecimal("0.07"), "kg"),
                createIngredient(menu8, "Tahu", new BigDecimal("0.04"), "kg"),
                createIngredient(menu8, "Wortel", new BigDecimal("0.03"), "kg"),
                createIngredient(menu8, "Kubis", new BigDecimal("0.04"), "kg"),
                createIngredient(menu8, "Kentang", new BigDecimal("0.03"), "kg"),
                createIngredient(menu8, "Beras", new BigDecimal("0.08"), "kg"),
                createIngredient(menu8, "Apel", new BigDecimal("0.1"), "kg")
        ));
        menus.add(menu8);

        Menu menu9 = createMenu(
                "Telur Dadar + Tumis Kacang Panjang + Nasi + Jeruk",
                "Menu ekonomis dengan telur sebagai protein utama",
                "Protein Telur"
        );
        menu9.setIngredients(List.of(
                createIngredient(menu9, "Telur", new BigDecimal("0.12"), "kg"),
                createIngredient(menu9, "Kacang Panjang", new BigDecimal("0.06"), "kg"),
                createIngredient(menu9, "Beras", new BigDecimal("0.08"), "kg"),
                createIngredient(menu9, "Jeruk", new BigDecimal("0.1"), "kg"),
                createIngredient(menu9, "Bawang Merah", new BigDecimal("0.01"), "kg"),
                createIngredient(menu9, "Minyak Goreng", new BigDecimal("0.01"), "kg")
        ));
        menus.add(menu9);

        Menu menu10 = createMenu(
                "Gado-Gado Sayuran + Telur Rebus + Tahu + Nasi + Pepaya",
                "Menu tradisional Indonesia yang kaya sayuran dan protein",
                "Tradisional Sehat"
        );
        menu10.setIngredients(List.of(
                createIngredient(menu10, "Telur", new BigDecimal("0.06"), "kg"),
                createIngredient(menu10, "Tahu", new BigDecimal("0.04"), "kg"),
                createIngredient(menu10, "Kangkung", new BigDecimal("0.04"), "kg"),
                createIngredient(menu10, "Kacang Panjang", new BigDecimal("0.04"), "kg"),
                createIngredient(menu10, "Tomat", new BigDecimal("0.03"), "kg"),
                createIngredient(menu10, "Timun", new BigDecimal("0.03"), "kg"),
                createIngredient(menu10, "Beras", new BigDecimal("0.08"), "kg"),
                createIngredient(menu10, "Pepaya", new BigDecimal("0.1"), "kg"),
                createIngredient(menu10, "Kacang Tanah", new BigDecimal("0.02"), "kg")
        ));
        menus.add(menu10);

        Menu menu11 = createMenu(
                "Sup Ayam Sayuran + Nasi + Pisang",
                "Menu sup hangat yang cocok untuk segala cuaca",
                "Sup Bergizi"
        );
        menu11.setIngredients(List.of(
                createIngredient(menu11, "Ayam", new BigDecimal("0.08"), "kg"),
                createIngredient(menu11, "Wortel", new BigDecimal("0.04"), "kg"),
                createIngredient(menu11, "Kentang", new BigDecimal("0.04"), "kg"),
                createIngredient(menu11, "Kubis", new BigDecimal("0.04"), "kg"),
                createIngredient(menu11, "Beras", new BigDecimal("0.08"), "kg"),
                createIngredient(menu11, "Pisang", new BigDecimal("0.1"), "kg"),
                createIngredient(menu11, "Bawang Bombay", new BigDecimal("0.02"), "kg")
        ));
        menus.add(menu11);

        Menu menu12 = createMenu(
                "Ayam Bumbu Kuning + Tumis Sawi + Nasi + Semangka",
                "Menu dengan bumbu tradisional Indonesia yang kaya rempah",
                "Tradisional"
        );
        menu12.setIngredients(List.of(
                createIngredient(menu12, "Ayam", new BigDecimal("0.08"), "kg"),
                createIngredient(menu12, "Sawi", new BigDecimal("0.06"), "kg"),
                createIngredient(menu12, "Beras", new BigDecimal("0.08"), "kg"),
                createIngredient(menu12, "Semangka", new BigDecimal("0.1"), "kg"),
                createIngredient(menu12, "Kunyit", new BigDecimal("0.005"), "kg"),
                createIngredient(menu12, "Jahe", new BigDecimal("0.005"), "kg"),
                createIngredient(menu12, "Serai", new BigDecimal("0.01"), "kg")
        ));
        menus.add(menu12);

        menuRepository.saveAll(menus);
        log.info("Successfully seeded {} menu items", menus.size());
    }

    private Menu createMenu(String name, String description, String category) {
        Menu menu = new Menu();
        menu.setName(name);
        menu.setDescription(description);
        menu.setCategory(category);
        menu.setServingSize(1);
        menu.setActive(true);
        menu.setCreatedAt(Instant.now());
        menu.setUpdatedAt(Instant.now());
        return menu;
    }

    private MenuIngredient createIngredient(Menu menu, String ingredientName, BigDecimal weight, String unit) {
        MenuIngredient ingredient = new MenuIngredient();
        ingredient.setMenu(menu);
        ingredient.setIngredientName(ingredientName);
        ingredient.setWeightPerPortion(weight);
        ingredient.setUnit(unit);
        ingredient.setNotes("Sesuai standar AKG (Angka Kecukupan Gizi) untuk anak sekolah");
        return ingredient;
    }
}