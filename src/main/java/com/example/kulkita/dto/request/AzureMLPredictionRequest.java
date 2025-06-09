
package com.example.kulkita.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class AzureMLPredictionRequest {

    @JsonProperty("input_data")
    private InputData inputData;

    public AzureMLPredictionRequest() {}

    public static class InputData {
        private List<String> columns;
        private List<Integer> index;
        private List<List<Object>> data;

        public InputData() {}

        public List<String> getColumns() { return columns; }
        public void setColumns(List<String> columns) { this.columns = columns; }

        public List<Integer> getIndex() { return index; }
        public void setIndex(List<Integer> index) { this.index = index; }

        public List<List<Object>> getData() { return data; }
        public void setData(List<List<Object>> data) { this.data = data; }
    }
}



