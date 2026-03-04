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
        logger.info("Recebendo requisição de importação. FileKey: {}", fileKey);
        try {
            String jobId = producer.publish(fileKey);
            logger.info("Importação enfileirada com sucesso. JobId: {}, FileKey: {}", jobId, fileKey);
            return ResponseEntity.accepted().body(new JobResponse(jobId));
        } catch (Exception e) {
            logger.error("Erro ao enfileirar importação para FileKey: {}", fileKey, e);
            throw e;
        }
    }

    @Override
    public ResponseEntity<byte[]> getReport(@PathVariable String jobId) {
        logger.info("Recebendo requisição de download de relatório. JobId: {}", jobId);
        try {
            byte[] pdfContent = getPdfReportUseCase.execute(jobId);

            if (pdfContent == null) {
                logger.warn("Relatório não encontrado para JobId: {}", jobId);
                return ResponseEntity.notFound().build();
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", jobId + ".pdf");
            headers.setContentLength(pdfContent.length);

            logger.info("Retornando relatório PDF. JobId: {}, Tamanho: {} bytes", jobId, pdfContent.length);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfContent);
        } catch (Exception e) {
            logger.error("Erro ao recuperar relatório para JobId: {}", jobId, e);
            throw e;
        }
    }
}
