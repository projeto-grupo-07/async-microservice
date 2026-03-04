package com.example.ms_report_async.domain.service;

public interface GetPdfReportUseCase {
    byte[] execute(String jobId);
}
