package com.example.kulkita.dto.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StorageLocationSummary {
    private String location;
    private long batchCount;

    public StorageLocationSummary() {}

    public StorageLocationSummary(String location, long batchCount) {
        this.location = location;
        this.batchCount = batchCount;
    }

}
