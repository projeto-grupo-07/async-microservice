package com.example.ms_report_async.domain.service;

import com.example.ms_report_async.application.dto.ReportRequestDto;

public interface GetPdfReportUseCase {
    byte[] execute(ReportRequestDto reportRequestDto);
}
