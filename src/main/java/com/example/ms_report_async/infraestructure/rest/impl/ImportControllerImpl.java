package com.example.ms_report_async.infraestructure.rest.impl;

import com.example.ms_report_async.application.dto.JobResponse;
import com.example.ms_report_async.domain.service.GetPdfReportUseCase;
import com.example.ms_report_async.infraestructure.async.RabbitImportProducer;
import com.example.ms_report_async.infraestructure.rest.controller.ImportController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ImportControllerImpl implements ImportController {

    private static final Logger logger = LoggerFactory.getLogger(ImportControllerImpl.class);
    private final RabbitImportProducer producer;
    private final GetPdfReportUseCase getPdfReportUseCase;

    public ImportControllerImpl(RabbitImportProducer producer, GetPdfReportUseCase getPdfReportUseCase) {
        this.producer = producer;
        this.getPdfReportUseCase = getPdfReportUseCase;
    }

    @Override
    public ResponseEntity<JobResponse> importFile(@PathVariable String fileKey) {
        String normalizedKey = fileKey.startsWith("/") ? fileKey.substring(1) : fileKey;
        logger.info("Recebendo requisição de importação. FileKey: {}", normalizedKey);
        try {
            String jobId = producer.publish(normalizedKey);
            logger.info("Importação enfileirada com sucesso. JobId: {}, FileKey: {}", jobId, normalizedKey);
            return ResponseEntity.accepted().body(new JobResponse(jobId));
        } catch (Exception e) {
            logger.error("Erro ao enfileirar importação para FileKey: {}", normalizedKey, e);
            throw e;
        }
    }

    @Override
    public ResponseEntity<byte[]> getReport(@PathVariable String jobId) {
        String[] parts = jobId.split(";");
        if (parts.length < 2) {
            logger.warn("Requisição de download com JobId inválido: {}", jobId);
            return ResponseEntity.badRequest().build();
        }

        // 2. Limpeza de barras extras (se o path vier como "/ano=2026...")
        String path = parts[0].startsWith("/") ? parts[0].substring(1) : parts[0];
        String uuid = parts[1];

        String debugKey = "reports/" + path + "import-report-" + uuid + ".pdf";
        logger.info("Recebendo requisição de download. JobId: {}, Tentando S3Key: {}", jobId, debugKey);

        try {
            byte[] pdfContent = getPdfReportUseCase.execute(jobId);

            if (pdfContent == null || pdfContent.length == 0) {
                logger.warn("Relatório não encontrado no S3 para JobId: {}", jobId);
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
            logger.error("Erro interno ao recuperar relatório: {}", jobId, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
