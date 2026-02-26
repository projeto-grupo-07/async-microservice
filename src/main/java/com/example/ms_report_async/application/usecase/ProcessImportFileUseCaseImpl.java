package com.example.ms_report_async.application.usecase;

import com.example.ms_report_async.domain.entity.ImportReport;
import com.example.ms_report_async.domain.entity.ImportRow;
import com.example.ms_report_async.domain.repository.CsvParserPort;
import com.example.ms_report_async.domain.repository.S3Port;
import com.example.ms_report_async.domain.service.GeneratePdfReportUseCase;
import com.example.ms_report_async.domain.service.ProcessImportFileUseCase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ProcessImportFileUseCaseImpl implements ProcessImportFileUseCase {

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

        try (InputStream in = s3.download(bucketName, fileKey)) {
            System.out.println("bucket name: " + bucketName);
            List<ImportRow> rows = csv.parse(in);

            ImportReport report = consolidate(rows, jobId);
            return pdf.execute(report);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ImportReport consolidate(List<ImportRow> rows, String jobId) {
        ImportReport report = new ImportReport();
        report.setProcessedAt(LocalDateTime.now());
        report.setJobId(jobId);
        report.setRows(rows);

        return report;
    }
}
