package com.example.ms_report_async.infraestructure.pdf;

import com.example.ms_report_async.domain.entity.ImportReport;
import com.example.ms_report_async.domain.repository.S3Port;
import com.example.ms_report_async.domain.service.GeneratePdfReportUseCase;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.util.Locale;

@Component
@SuppressWarnings("unused")
public class PdfGeneratorAdapter implements GeneratePdfReportUseCase {

    private static final Logger logger = LoggerFactory.getLogger(PdfGeneratorAdapter.class);
    private static final int COLUMNS_COUNT = 7;
    private static final String REPORT_TITLE = "Import Report";
    private static final String PROCESSED_AT_LABEL = "Processed At: ";
    private static final String PDF_CONTENT_TYPE = "application/pdf";
    private static final String PDF_KEY_FORMAT = "reports/import-report-%s.pdf";

    private static final String[] TABLE_HEADERS = {
            "NCM", "Country", "Volume Total", "Value Total",
            "Average Price", "Average Freight", "Average Insurance"
    };

    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getNumberInstance(Locale.US);

    private final S3Port s3Port;

    @Value("${aws.s3.output-bucket:brinks-bucket-2}")
    private String outputBucket;

    public PdfGeneratorAdapter(S3Port s3Port) {
        this.s3Port = s3Port;
    }

    @Override
    public String execute(ImportReport report) {
        logger.info("Iniciando geração do PDF. JobId: {}, TotalLinhas: {}",
                report.getJobId(), report.getRows().size());
        long startTime = System.currentTimeMillis();

        try {
            byte[] pdf = buildPdf(report);
            logger.debug("PDF gerado com sucesso. JobId: {}, TamanhoBytes: {}",
                    report.getJobId(), pdf.length);

            String pdfKey = String.format(PDF_KEY_FORMAT, report.getJobId());
            s3Port.upload(outputBucket, pdfKey, pdf, PDF_CONTENT_TYPE);

            long duration = System.currentTimeMillis() - startTime;
            logger.info("PDF salvo no S3 com sucesso. JobId: {}, PdfKey: {}, DuracaoMs: {}",
                    report.getJobId(), pdfKey, duration);

            return pdfKey;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("Erro ao gerar/salvar PDF. JobId: {}, DuracaoMs: {}",
                    report.getJobId(), duration, e);
            throw new RuntimeException("Failed to generate and save PDF", e);
        }
    }

    private byte[] buildPdf(ImportReport report) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, baos);
            document.open();

            addHeader(document, report);
            addTable(document, report);

            document.close();
            logger.debug("Documento PDF construído em memória. JobId: {}, TamanhoBytes: {}",
                    report.getJobId(), baos.size());
            return baos.toByteArray();
        } catch (Exception e) {
            logger.error("Erro ao construir documento PDF. JobId: {}", report.getJobId(), e);
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }

    private void addHeader(Document document, ImportReport report) {
        document.add(createTitleParagraph());
        document.add(new Paragraph(PROCESSED_AT_LABEL + report.getProcessedAt()));
        document.add(new Paragraph(" "));
    }

    private Paragraph createTitleParagraph() {
        return new Paragraph(REPORT_TITLE, new Font(Font.HELVETICA, 18, Font.BOLD));
    }

    private void addTable(Document document, ImportReport report) {
        PdfPTable table = new PdfPTable(COLUMNS_COUNT);
        table.setWidthPercentage(100);
        addTableHeaders(table);
        addTableRows(table, report);
        document.add(table);
    }

    private void addTableHeaders(PdfPTable table) {
        for (String header : TABLE_HEADERS) {
            table.addCell(header);
        }
    }

    private void addTableRows(PdfPTable table, ImportReport report) {
        report.getRows().forEach(row -> {
            table.addCell(row.getNcm());
            table.addCell(row.getPaisOrigem());
            table.addCell(formatNumber(row.getVolumeTotal()));
            table.addCell(formatNumber(row.getValorTotal()));
            table.addCell(formatNumber(row.getPrecoMedio()));
            table.addCell(formatNumber(row.getFreteMedio()));
            table.addCell(formatNumber(row.getSeguroMedio()));
        });
    }

    private String formatNumber(Number value) {
        return value == null ? "" : NUMBER_FORMAT.format(value);
    }
}