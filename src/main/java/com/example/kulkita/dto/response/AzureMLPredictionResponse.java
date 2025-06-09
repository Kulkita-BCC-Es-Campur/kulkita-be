package com.example.kulkita.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AzureMLPredictionResponse {

    private List<Double> predictions;

    public AzureMLPredictionResponse() {}

    public AzureMLPredictionResponse(List<Double> predictions) {
        this.predictions = predictions;
    }

    public List<Double> getPredictions() {
        return predictions;
    }

    public void setPredictions(List<Double> predictions) {
        this.predictions = predictions;
    }

    public List<Double> getResult() {
        return predictions;
    }

    public void setResult(List<Double> result) {
        this.predictions = result;
    }

    @Override
    public String toString() {
        return "AzureMLPredictionResponse{predictions=" + predictions + "}";
    }
}