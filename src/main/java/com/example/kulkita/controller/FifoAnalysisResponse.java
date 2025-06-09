package com.example.kulkita.controller;

import java.util.List;

public class FifoAnalysisResponse {
    private String message;
    private double fifoComplianceScore;
    private int totalBatches;
    private int criticalBatches;
    private int highPriorityBatches;
    private int mediumPriorityBatches;
    private int lowPriorityBatches;
    private List<String> recommendations;

    public FifoAnalysisResponse() {}

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public double getFifoComplianceScore() { return fifoComplianceScore; }
    public void setFifoComplianceScore(double fifoComplianceScore) { this.fifoComplianceScore = fifoComplianceScore; }

    public int getTotalBatches() { return totalBatches; }
    public void setTotalBatches(int totalBatches) { this.totalBatches = totalBatches; }

    public int getCriticalBatches() { return criticalBatches; }
    public void setCriticalBatches(int criticalBatches) { this.criticalBatches = criticalBatches; }

    public int getHighPriorityBatches() { return highPriorityBatches; }
    public void setHighPriorityBatches(int highPriorityBatches) { this.highPriorityBatches = highPriorityBatches; }

    public int getMediumPriorityBatches() { return mediumPriorityBatches; }
    public void setMediumPriorityBatches(int mediumPriorityBatches) { this.mediumPriorityBatches = mediumPriorityBatches; }

    public int getLowPriorityBatches() { return lowPriorityBatches; }
    public void setLowPriorityBatches(int lowPriorityBatches) { this.lowPriorityBatches = lowPriorityBatches; }

    public List<String> getRecommendations() { return recommendations; }
    public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }
}
