package com.example.kulkita.dto.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UsageStatisticsResponse {
    private int penggunaanHariIni;           // 24 item bahan digunakan
    private int totalMingguIni;              // 156 +12% dari minggu lalu
    private double efisiensiPenggunaan;      // 94% sesuai rencana menu
    private double wasteRatio;               // 2.1% -0.5% dari target
    private String trendPenggunaanHarian;   // "+12% dari minggu lalu"
    private String trendEfisiensi;          // "sesuai rencana menu"
    private String trendWasteRatio;         // "-0.5% dari target"

    public UsageStatisticsResponse() {}

    public UsageStatisticsResponse(int penggunaanHariIni, int totalMingguIni,
                                   double efisiensiPenggunaan, double wasteRatio,
                                   String trendPenggunaanHarian, String trendEfisiensi,
                                   String trendWasteRatio) {
        this.penggunaanHariIni = penggunaanHariIni;
        this.totalMingguIni = totalMingguIni;
        this.efisiensiPenggunaan = efisiensiPenggunaan;
        this.wasteRatio = wasteRatio;
        this.trendPenggunaanHarian = trendPenggunaanHarian;
        this.trendEfisiensi = trendEfisiensi;
        this.trendWasteRatio = trendWasteRatio;
    }

}

