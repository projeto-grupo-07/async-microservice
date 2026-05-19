package com.example.ms_report_async.application.usecase;

import com.example.ms_report_async.application.dto.ReportRequestDto;
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
    public byte[] execute(ReportRequestDto reportRequestDto) {
        Integer ano = reportRequestDto.ano();
        Integer mes = reportRequestDto.mes();
        String jobId = reportRequestDto.jobId();

        logger.info("Iniciando busca do PDF. Ano: {}, Mes: {}, JobId: {}", ano, mes, jobId);
        byte[] pdfContent = s3Port.getPdfFromBucketClient(ano, mes, jobId);

        if (pdfContent == null) {
            logger.warn("PDF não encontrado. Ano: {}, Mes: {}, JobId: {}", ano, mes, jobId);
            throw new RuntimeException("PDF não encontrado para Ano: " + ano + ", Mes: " + mes + ", JobId: " + jobId);
        }

        logger.info("PDF encontrado com sucesso. Ano: {}, Mes: {}, JobId: {}, Tamanho: {} bytes", ano, mes, jobId, pdfContent.length);
        return pdfContent;
    }
}
