package com.example.ms_report_async.infraestructure.rest.impl;

import com.example.ms_report_async.application.dto.JobResponse;
import com.example.ms_report_async.domain.service.GetPdfReportUseCase;
import com.example.ms_report_async.infraestructure.rest.controller.ImportController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class ImportControllerImpl implements ImportController {

    private final GetPdfReportUseCase getPdfReportUseCase;

    public ImportControllerImpl(GetPdfReportUseCase getPdfReportUseCase) {
        this.getPdfReportUseCase = getPdfReportUseCase;
    }

    @Override
    public ResponseEntity<byte[]> getReport(@PathVariable String jobId) {
        String[] parts = jobId.split("__");
        if (parts.length < 2) {
            log.warn("Requisição de download com JobId inválido: {}", jobId);
            return ResponseEntity.badRequest().build();
        }

        // 2. Limpeza de barras extras (se o path vier como "/ano=2026...")
        String path = parts[0].startsWith("/") ? parts[0].substring(1) : parts[0];
        String uuid = parts[1];

        String debugKey = "reports/" + path + "import-report-" + uuid + ".pdf";
        log.info("Recebendo requisição de download. JobId: {}, Tentando S3Key: {}", jobId, debugKey);

        try {
            byte[] pdfContent = getPdfReportUseCase.execute(jobId);

            if (pdfContent == null || pdfContent.length == 0) {
                log.warn("Relatório não encontrado no S3 para JobId: {}", jobId);
                return ResponseEntity.notFound().build();
            }

            // 4. Headers para o navegador entender que é um PDF
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            // Nome amigável para o arquivo que o usuário vai baixar
            headers.setContentDispositionFormData("attachment", "relatorio-" + uuid + ".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(pdfContent.length)
                    .body(pdfContent);

        } catch (Exception e) {
            log.error("Erro interno ao recuperar relatório: {}", jobId, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
