package com.example.ms_report_async.infraestructure.rest.impl;

import com.example.ms_report_async.application.dto.ReportRequestDto;
import com.example.ms_report_async.domain.service.GetPdfReportUseCase;
import com.example.ms_report_async.infraestructure.rest.controller.ImportController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class ImportControllerImpl implements ImportController {

    private final GetPdfReportUseCase getPdfReportUseCase;

    public ImportControllerImpl(GetPdfReportUseCase getPdfReportUseCase) {
        this.getPdfReportUseCase = getPdfReportUseCase;
    }

    @Override
    public ResponseEntity<byte[]> getReport(@ModelAttribute ReportRequestDto reportRequestDto) {
        Integer ano = reportRequestDto.ano();
        Integer mes = reportRequestDto.mes();
        String jobId = reportRequestDto.jobId();

        if (jobId == null || jobId.isBlank() || ano == null || mes == null || mes < 1 || mes > 12 || ano <= 0) {
            log.warn("Parametros invalidos. Ano: {}, Mes: {}, JobId: {}", ano, mes, jobId);
            return ResponseEntity.badRequest().build();
        }

        log.info("Recebendo requisição de download. Ano: {}, Mes: {}, JobId: {}", ano, mes, jobId);
        try {
            byte[] pdfContent = getPdfReportUseCase.execute(reportRequestDto);

            if (pdfContent == null || pdfContent.length == 0) {
                log.warn("Relatório não encontrado no S3. Ano: {}, Mes: {}, JobId: {}", ano, mes, jobId);
                return ResponseEntity.notFound().build();
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "relatorio-" + jobId + ".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(pdfContent.length)
                    .body(pdfContent);

        } catch (Exception e) {
            log.error("Erro interno ao recuperar relatório. Ano: {}, Mes: {}, JobId: {}", ano, mes, jobId, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
