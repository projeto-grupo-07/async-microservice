package com.example.ms_report_async.application.dto;

public record ReportRequestDto(
        Integer mes,
        Integer ano,
        String jobId
) {
}
