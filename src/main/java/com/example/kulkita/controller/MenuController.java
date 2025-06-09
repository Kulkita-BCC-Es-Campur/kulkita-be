package com.example.kulkita.controller;

import com.example.kulkita.dto.response.MenuResponse;
import com.example.kulkita.service.MenuService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menus")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping
    public ResponseEntity<List<MenuResponse>> getAllActiveMenus() {
        List<MenuResponse> menus = menuService.getAllActiveMenus();
        return ResponseEntity.ok(menus);
    }

    @GetMapping("/{menuId}")
    public ResponseEntity<MenuResponse> getMenuById(@PathVariable String menuId) {
        MenuResponse menu = menuService.getMenuById(menuId);
        return ResponseEntity.ok(menu);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getAllCategories() {
        List<String> categories = menuService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/by-category/{category}")
    public ResponseEntity<List<MenuResponse>> getMenusByCategory(@PathVariable String category) {
        List<MenuResponse> menus = menuService.getMenusByCategory(category);
        return ResponseEntity.ok(menus);
    }

    @GetMapping("/by-ingredient/{ingredientName}")
    public ResponseEntity<List<MenuResponse>> getMenusByIngredient(@PathVariable String ingredientName) {
        List<MenuResponse> menus = menuService.getMenusByIngredient(ingredientName);
        return ResponseEntity.ok(menus);
    }
}