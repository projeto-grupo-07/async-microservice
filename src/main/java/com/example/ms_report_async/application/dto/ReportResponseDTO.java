package com.example.ms_report_async.application.dto;

import com.example.ms_report_async.domain.entity.ImportRow;

import java.time.LocalDateTime;
import java.util.List;

public record ReportResponseDTO(
        String jobId,
        LocalDateTime processedAt,
        int totalRows,
        List<ImportRow> top5CheapProduct,
        List<ImportRow> top5BiggestVolume,
        List<ImportRow> top5CheapFreight
) {
}
