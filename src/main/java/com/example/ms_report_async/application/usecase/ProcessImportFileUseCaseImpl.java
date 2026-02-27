package com.example.ms_report_async.application.usecase;

import com.example.ms_report_async.domain.entity.ImportReport;
import com.example.ms_report_async.domain.entity.ImportRow;
import com.example.ms_report_async.domain.repository.CsvParserPort;
import com.example.ms_report_async.domain.repository.S3Port;
import com.example.ms_report_async.domain.service.GeneratePdfReportUseCase;
import com.example.ms_report_async.domain.service.ProcessImportFileUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ProcessImportFileUseCaseImpl implements ProcessImportFileUseCase {

    private static final Logger logger = LoggerFactory.getLogger(ProcessImportFileUseCaseImpl.class);
    private final S3Port s3;
    private final CsvParserPort csv;
    private final GeneratePdfReportUseCase pdf;

    @Value("${aws.s3.bucket-name}")
    String bucketName;

    public ProcessImportFileUseCaseImpl(S3Port s3, CsvParserPort csv, GeneratePdfReportUseCase pdf) {
        this.s3 = s3;
        this.csv = csv;
        this.pdf = pdf;
    }

    @Override
    public String execute(String fileKey, String jobId) {
        logger.info("Iniciando processamento do arquivo. JobId: {}, FileKey: {}, Bucket: {}", jobId, fileKey, bucketName);
        long startTime = System.currentTimeMillis();

        try (InputStream in = s3.download(bucketName, fileKey)) {
            logger.debug("Arquivo baixado com sucesso do S3. JobId: {}, FileKey: {}", jobId, fileKey);

            List<ImportRow> rows = csv.parse(in);
            logger.info("CSV parseado com sucesso. JobId: {}, TotalLinhas: {}", jobId, rows.size());

            ImportReport report = consolidate(rows, jobId);
            logger.debug("Relatório consolidado. JobId: {}, Linhas: {}", jobId, report.getRows().size());

            String pdfKey = pdf.execute(report);

            long duration = System.currentTimeMillis() - startTime;
            logger.info("Processamento concluído com sucesso. JobId: {}, PdfKey: {}, DuracaoMs: {}",
                    jobId, pdfKey, duration);

            return pdfKey;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("Erro ao processar arquivo. JobId: {}, FileKey: {}, DuracaoMs: {}",
                    jobId, fileKey, duration, e);
            throw new RuntimeException("Falha ao processar arquivo: " + fileKey, e);
        }
    }

    private ImportReport consolidate(List<ImportRow> rows, String jobId) {
        logger.debug("Consolidando relatório de importação. JobId: {}, TotalLinhas: {}", jobId, rows.size());

        ImportReport report = new ImportReport();
        report.setProcessedAt(LocalDateTime.now());
        report.setJobId(jobId);
        report.setRows(rows);

        return report;
    }
}
