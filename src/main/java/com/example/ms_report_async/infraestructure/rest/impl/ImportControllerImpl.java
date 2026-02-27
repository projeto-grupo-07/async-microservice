package com.example.ms_report_async.infraestructure.rest.impl;

import com.example.ms_report_async.application.dto.JobResponse;
import com.example.ms_report_async.infraestructure.async.RabbitImportProducer;
import com.example.ms_report_async.infraestructure.rest.controller.ImportController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ImportControllerImpl implements ImportController {

    private static final Logger logger = LoggerFactory.getLogger(ImportControllerImpl.class);
    private final RabbitImportProducer producer;

    public ImportControllerImpl(RabbitImportProducer producer) {
        this.producer = producer;
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
}
