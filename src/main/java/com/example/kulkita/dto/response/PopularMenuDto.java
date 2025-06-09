package com.example.kulkita.dto.response;

public class PopularMenuDto {
    private String menuName;
    private long usageCount;
    private long totalPortions;

    public PopularMenuDto(String menuName, long usageCount, long totalPortions) {
        this.menuName = menuName;
        this.usageCount = usageCount;
        this.totalPortions = totalPortions;
    }

    public String getMenuName() {
        return menuName;
    }

    public long getUsageCount() {
        return usageCount;
    }

    public long getTotalPortions() {
        return totalPortions;
    }
}
