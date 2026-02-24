package com.example.ms_report_async.application.usecase;

import com.example.ms_report_async.domain.entity.ImportReport;
import com.example.ms_report_async.domain.entity.ImportRow;
import com.example.ms_report_async.domain.repository.CsvParserPort;
import com.example.ms_report_async.domain.repository.S3Port;
import com.example.ms_report_async.domain.service.GeneratePdfReportUseCase;
import com.example.ms_report_async.domain.service.ProcessImportFileUseCase;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class ProcessImportFileUseCaseImpl implements ProcessImportFileUseCase {

    private final S3Port s3;
    private final CsvParserPort csv;
    private final GeneratePdfReportUseCase pdf;

    public ProcessImportFileUseCaseImpl(S3Port s3, CsvParserPort csv, GeneratePdfReportUseCase pdf) {
        this.s3 = s3;
        this.csv = csv;
        this.pdf = pdf;
    }

    @Override
    public String execute(String fileKey) {
        try (InputStream in = s3.download("my-bucket", fileKey)) {
            List<ImportRow> rows = csv.parse(in);

            ImportReport report = consolidate(rows);

            return pdf.execute(report);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ImportReport consolidate(List<ImportRow> rows) {
        BigDecimal sumVolume = rows.stream()
                .map(ImportRow::getVolumeTotal)
                .filter(v -> v != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal sumValor = rows.stream()
                .map(ImportRow::getValorTotal)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        ImportReport report = new ImportReport();
        report.setProcessedAt(LocalDateTime.now());
        report.setRows(rows);

        return report;
    }
}
