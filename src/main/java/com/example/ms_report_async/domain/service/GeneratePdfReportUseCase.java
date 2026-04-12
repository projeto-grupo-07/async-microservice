package com.example.ms_report_async.domain.service;

import com.example.ms_report_async.application.dto.ReportResponseDTO;
import com.example.ms_report_async.domain.entity.ImportReport;

public interface GeneratePdfReportUseCase {
    String execute(ReportResponseDTO report, String path);
}
