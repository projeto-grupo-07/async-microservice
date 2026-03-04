package com.example.ms_report_async.application.usecase;

import com.example.ms_report_async.domain.repository.S3Port;
import com.example.ms_report_async.domain.service.GetPdfReportUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GetPdfReportUseCaseImpl implements GetPdfReportUseCase {

    private static final Logger logger = LoggerFactory.getLogger(GetPdfReportUseCaseImpl.class);
    private final S3Port s3Port;

    public GetPdfReportUseCaseImpl(S3Port s3Port) {
        this.s3Port = s3Port;
    }

    @Override
    public byte[] execute(String jobId) {
        logger.info("Iniciando busca do PDF para o JobId: {}", jobId);
        byte[] pdfContent = s3Port.getPdfFromBucket2(jobId);
        
        if (pdfContent == null) {
            logger.warn("PDF não encontrado para o JobId: {}", jobId);
            throw new RuntimeException("PDF não encontrado para o JobId: " + jobId);
        }

        logger.info("PDF encontrado com sucesso. JobId: {}, Tamanho: {} bytes", jobId, pdfContent.length);
        return pdfContent;
    }
}
