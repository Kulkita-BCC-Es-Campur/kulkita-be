package com.example.kulkita.service;

import com.example.kulkita.dto.response.MenuIngredientResponse;
import com.example.kulkita.dto.response.MenuResponse;
import com.example.kulkita.entity.Menu;
import com.example.kulkita.entity.MenuIngredient;
import com.example.kulkita.exception.ResourceNotFoundException;
import com.example.kulkita.repository.MenuRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MenuService {

    private final MenuRepository menuRepository;

    public MenuService(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    public List<MenuResponse> getAllActiveMenus() {
        return menuRepository.findByActiveTrueOrderByNameAsc()
                .stream()
                .map(this::convertToMenuResponse)
                .collect(Collectors.toList());
    }

    public MenuResponse getMenuById(String menuId) {
        Menu menu = menuRepository.findById(UUID.fromString(menuId))
                .orElseThrow(() -> new ResourceNotFoundException("Menu not found with ID: " + menuId));

        if (!menu.isActive()) {
            throw new ResourceNotFoundException("Menu is not active");
        }

        return convertToMenuResponse(menu);
    }

    public List<String> getAllCategories() {
        return menuRepository.findAllActiveCategories();
    }

    public List<MenuResponse> getMenusByCategory(String category) {
        return menuRepository.findByCategoryAndActiveTrueOrderByNameAsc(category)
                .stream()
                .map(this::convertToMenuResponse)
                .collect(Collectors.toList());
    }

    public List<MenuResponse> getMenusByIngredient(String ingredientName) {
        return menuRepository.findMenusByIngredient(ingredientName)
                .stream()
                .map(this::convertToMenuResponse)
                .collect(Collectors.toList());
    }

    private MenuResponse convertToMenuResponse(Menu menu) {
        MenuResponse response = new MenuResponse();
        response.setId(menu.getId().toString());
        response.setName(menu.getName());
        response.setDescription(menu.getDescription());
        response.setCategory(menu.getCategory());
        response.setActive(menu.isActive());

        List<MenuIngredientResponse> ingredientResponses = menu.getIngredients()
                .stream()
                .map(this::convertToMenuIngredientResponse)
                .collect(Collectors.toList());

        response.setIngredients(ingredientResponses);
        response.setCreatedAt(menu.getCreatedAt());
        response.setUpdatedAt(menu.getUpdatedAt());

        return response;
    }

    private MenuIngredientResponse convertToMenuIngredientResponse(MenuIngredient ingredient) {
        MenuIngredientResponse response = new MenuIngredientResponse();
        response.setId(ingredient.getId().toString());
        response.setIngredientName(ingredient.getIngredientName());
        response.setWeightPerPortion(ingredient.getWeightPerPortion());
        response.setUnit(ingredient.getUnit());
        response.setNotes(ingredient.getNotes());
        return response;
    }
}