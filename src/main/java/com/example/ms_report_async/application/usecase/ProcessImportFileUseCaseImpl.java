package com.example.ms_report_async.application.usecase;

import com.example.ms_report_async.application.dto.ReportResponseDTO;
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
import java.util.Comparator;
import java.util.List;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
public class ProcessImportFileUseCaseImpl implements ProcessImportFileUseCase {

    private static final Logger logger = LoggerFactory.getLogger(ProcessImportFileUseCaseImpl.class);
    private final S3Port s3Port;
    private final CsvParserPort csvParserPort;
    private final GeneratePdfReportUseCase generatePdfReportUseCase;

    @Value("${aws.s3.bucket-trusted}")
    String bucketTrusted;

    public ProcessImportFileUseCaseImpl(S3Port s3Port, CsvParserPort csvParserPort, GeneratePdfReportUseCase generatePdfReportUseCase) {
        this.s3Port = s3Port;
        this.csvParserPort = csvParserPort;
        this.generatePdfReportUseCase = generatePdfReportUseCase;
    }

    @Override
    public String execute(String fileKey, String compositeJobId) {
        String[] parts = compositeJobId.split("__");
        String path = parts[0];       // "ano=2026/mes=01/"
        String realUuid = parts[1];   // "550e8400..."

        logger.info("Iniciando processamento do arquivo. JobId: {}, FileKey: {}, Bucket: {}", compositeJobId, fileKey, bucketTrusted);
        long startTime = System.currentTimeMillis();

        try (InputStream inputStream = s3Port.download(bucketTrusted, fileKey)) {
            logger.debug("Arquivo baixado com sucesso do S3 trusted. JobId: {}, FileKey: {}", compositeJobId, fileKey);

            List<ImportRow> rows = csvParserPort.parse(inputStream);
            logger.info("CSV parseado com sucesso. JobId: {}, TotalLinhas: {}", compositeJobId, rows.size());

            ReportResponseDTO report = consolidate(rows, compositeJobId);
            logger.debug("Relatório consolidado. JobId: {}, Linhas: {}", compositeJobId, report.totalRows());

            String pdfKey = generatePdfReportUseCase.execute(report, path);

            long duration = System.currentTimeMillis() - startTime;
            logger.info("Processamento concluído com sucesso. JobId: {}, Path: {}, PdfKey: {}, DuracaoMs: {}",
                    compositeJobId, path, pdfKey, duration);

            return pdfKey;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("Erro ao processar arquivo. JobId: {}, Path: {}, FileKey: {}, DuracaoMs: {}",
                    compositeJobId, path, fileKey, duration, e);
            throw new RuntimeException("Falha ao processar arquivo: " + fileKey, e);
        }
    }

    private ReportResponseDTO consolidate(List<ImportRow> rows, String jobId) {
        logger.debug("Consolidando relatório de importação. JobId: {}, TotalLinhas: {}", jobId, rows.size());

        ImportReport report = new ImportReport();
        report.setProcessedAt(LocalDateTime.now());
        report.setJobId(jobId);
        report.setTop5CheapProducts(top5CheapAveragePrice(rows));
        report.setTop5BiggestVolume(top5BiggestVolume(rows));
        report.setTop5CheapFreight(top5CheapAverageFreight(rows));

        return new ReportResponseDTO(
                jobId,
                LocalDateTime.now(),
                rows.size(),
                top5CheapAveragePrice(rows),
                top5BiggestVolume(rows),
                top5CheapAverageFreight(rows)
        );
    }

    private List<ImportRow> top5CheapAveragePrice(List<ImportRow> rows){
        logger.info("Realizando tratamento de top 5 produtos mais baratos");
        if (rows == null) {
            logger.warn("Lista de rows é nula ao calcular top5CheapAveragePrice; retornando lista vazia");
            return Collections.emptyList();
        }

        try{
            return rows.stream()
                    .filter(row -> row.getPrecoMedio() != null && row.getPrecoMedio().doubleValue() > 0)
                    .sorted(Comparator.comparing(ImportRow::getPrecoMedio))
                    .limit(5)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Nao foi possível realizar top 5 produtos mais baratos", e);
            throw new RuntimeException("Erro ao calcular top5CheapAveragePrice", e);
        }

    }

    private List<ImportRow> top5BiggestVolume(List<ImportRow> rows){
        logger.info("Realizando tratamento de top 5 produtos maior volume");
        if (rows == null) {
            logger.warn("Lista de rows é nula ao calcular top5BiggestVolume; retornando lista vazia");
            return Collections.emptyList();
        }

        try{
            return rows.stream()
                    .filter(row -> row.getVolumeTotal() != null)
                    .sorted(Comparator.comparing(ImportRow::getVolumeTotal).reversed())
                    .limit(5)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Nao foi possível realizar top 5 produtos maior volume", e);
            throw new RuntimeException("Erro ao calcular top5BiggestVolume", e);
        }
    }

    private List<ImportRow> top5CheapAverageFreight(List<ImportRow> rows){
        logger.info("Realizando tratamento de top 5 produtos com frete medio mais barato");
        if (rows == null) {
            logger.warn("Lista de rows é nula ao calcular top5CheapAverageFreight; retornando lista vazia");
            return Collections.emptyList();
        }

        try{
            return rows.stream()
                    .filter(row -> row.getFreteMedio() != null && row.getFreteMedio().doubleValue() > 0)
                    .sorted(Comparator.comparing(ImportRow::getFreteMedio))
                    .limit(5)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Nao foi possível realizar top 5 produtos com frete medio mais barato", e);
            throw new RuntimeException("Erro ao calcular top5CheapAverageFreight", e);
        }
    }
}
