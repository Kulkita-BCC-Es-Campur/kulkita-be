package com.example.kulkita.repository;

import com.example.kulkita.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MenuRepository extends JpaRepository<Menu, UUID> {

    List<Menu> findByActiveTrueOrderByNameAsc();

    List<Menu> findByCategoryAndActiveTrueOrderByNameAsc(String category);

    @Query("SELECT DISTINCT m.category FROM Menu m WHERE m.active = true ORDER BY m.category")
    List<String> findAllActiveCategories();

    @Query("SELECT m FROM Menu m JOIN m.ingredients mi WHERE mi.ingredientName = :ingredientName AND m.active = true")
    List<Menu> findMenusByIngredient(String ingredientName);
}