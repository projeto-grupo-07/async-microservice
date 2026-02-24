package com.example.ms_report_async.domain.entity;

import java.time.LocalDateTime;
import java.util.List;

public class ImportReport {
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

    public ImportReport() {
    }

    public ImportReport(LocalDateTime processedAt, List<ImportRow> rows) {
        this.processedAt = processedAt;
        this.rows = rows;
    }
}
