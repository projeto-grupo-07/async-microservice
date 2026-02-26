package com.example.ms_report_async.domain.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ImportReport {
    private String jobId;
    private LocalDateTime processedAt;
    private List<ImportRow> rows;

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    public List<ImportRow> getRows() {
        return rows;
    }

    public void setRows(List<ImportRow> rows) {
        this.rows = rows;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public ImportReport() {
    }

    public ImportReport(LocalDateTime processedAt, List<ImportRow> rows, String jobId) {
        this.processedAt = processedAt;
        this.rows = rows;
        this.jobId = jobId;
    }
}
