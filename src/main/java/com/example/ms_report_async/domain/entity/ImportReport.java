package com.example.ms_report_async.domain.entity;

import java.time.LocalDateTime;
import java.util.List;

public class ImportReport {
    private String jobId;
    private LocalDateTime processedAt;
    private int totalRows;
    private List<ImportRow> top5CheapProducts;
    private List<ImportRow> top5BiggestVolume;
    private List<ImportRow> top5CheapFreight;

    public ImportReport() {
    }

    public ImportReport(String jobId, LocalDateTime processedAt, int totalRows,
                        List<ImportRow> top5CheapProducts, List<ImportRow> top5BiggestVolume, List<ImportRow> top5CheapFreight) {
        this.jobId = jobId;
        this.processedAt = processedAt;
        this.totalRows = totalRows;
        this.top5CheapProducts = top5CheapProducts;
        this.top5BiggestVolume = top5BiggestVolume;
        this.top5CheapFreight = top5CheapFreight;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    public int getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }

    public List<ImportRow> getTop5CheapProducts() {
        return top5CheapProducts;
    }

    public void setTop5CheapProducts(List<ImportRow> top5CheapProducts) {
        this.top5CheapProducts = top5CheapProducts;
    }

    public List<ImportRow> getTop5BiggestVolume() {
        return top5BiggestVolume;
    }

    public void setTop5BiggestVolume(List<ImportRow> top5BiggestVolume) {
        this.top5BiggestVolume = top5BiggestVolume;
    }

    public List<ImportRow> getTop5CheapFreight() {
        return top5CheapFreight;
    }

    public void setTop5CheapFreight(List<ImportRow> top5CheapFreight) {
        this.top5CheapFreight = top5CheapFreight;
    }
}
