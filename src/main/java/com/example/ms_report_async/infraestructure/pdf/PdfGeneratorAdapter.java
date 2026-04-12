package com.example.ms_report_async.infraestructure.pdf;

import com.example.ms_report_async.application.dto.ReportResponseDTO;
import com.example.ms_report_async.domain.entity.ImportRow;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Component
public class PdfGeneratorAdapter implements GeneratePdfReportUseCase {

    private static final Logger logger = LoggerFactory.getLogger(PdfGeneratorAdapter.class);
    private static final int COLUMNS_COUNT = 7;
    private static final String REPORT_TITLE = "Relatório Estratégico de Importação - Brink Calçados";
    private static final String PDF_CONTENT_TYPE = "application/pdf";

    // Cabeçalhos traduzidos para maior clareza de negócio
    private static final String[] TABLE_HEADERS = {
            "NCM", "País Origem", "Volume Total", "Valor Total",
            "Preço Médio", "Frete Médio", "Seguro Médio"
    };

    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getNumberInstance(Locale.US);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final S3Port s3Port;

    @Value("${aws.s3.bucket-client}")
    private String outputBucket;

    public PdfGeneratorAdapter(S3Port s3Port) {
        this.s3Port = s3Port;
    }

    @Override
    public String execute(ReportResponseDTO report, String path) {
        logger.info("Iniciando geração do PDF. JobId: {}, Path: {}, TotalLinhas Analisadas: {}",
                report.jobId(), path, report.totalRows());
        long startTime = System.currentTimeMillis();

        try {
            byte[] pdf = buildPdf(report);

            String uuid = report.jobId().split("__")[1];

            String pdfKey = "reports/" + path + "import-report-" + uuid + ".pdf";

            s3Port.upload(outputBucket, pdfKey, pdf, PDF_CONTENT_TYPE);

            long duration = System.currentTimeMillis() - startTime;
            logger.info("PDF salvo no S3 com sucesso. JobId: {}, PdfKey: {}, DuracaoMs: {}",
                    report.jobId(), pdfKey, duration);

            return pdfKey;
        } catch (Exception e) {
            logger.error("Erro ao gerar/salvar PDF. JobId: {}", report.jobId(), e);
            throw new RuntimeException("Failed to generate and save PDF", e);
        }
    }

    private byte[] buildPdf(ReportResponseDTO report) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, baos);
            document.open();

            addHeader(document, report);

            addSection(document, "1. Top 5 - Menor Preço Médio (Custo-Benefício)", report.top5CheapProduct());
            addSection(document, "2. Top 5 - Maior Volume de Importação (Concorrência)", report.top5BiggestVolume());
            addSection(document, "3. Top 5 - Menor Custo de Frete (Logística)", report.top5CheapFreight());

            document.close();

            logger.debug("Documento PDF construído em memória. JobId: {}, TamanhoBytes: {}",
                    report.jobId(), baos.size());
            return baos.toByteArray();
        } catch (Exception e) {
            logger.error("Erro ao construir documento PDF. JobId: {}", report.jobId(), e);
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }

    private void addHeader(Document document, ReportResponseDTO report) throws DocumentException {
        Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
        Font subtitleFont = new Font(Font.HELVETICA, 12, Font.NORMAL);

        document.add(new Paragraph(REPORT_TITLE, titleFont));
        document.add(new Paragraph("Base de dados analisada: " + report.totalRows() + " registros da Receita Federal.", subtitleFont));

        String formattedDate = report.processedAt() != null ? report.processedAt().format(DATE_FORMATTER) : "N/A";
        document.add(new Paragraph("Processado em: " + formattedDate, subtitleFont));

        document.add(new Paragraph(" ")); // Quebra de linha
    }

    private void addSection(Document document, String sectionTitle, List<ImportRow> rows) throws DocumentException {
        Font sectionFont = new Font(Font.HELVETICA, 14, Font.BOLD);
        Paragraph title = new Paragraph(sectionTitle, sectionFont);
        title.setSpacingAfter(10f); // Dá um espaço entre o título e a tabela
        document.add(title);

        if (rows == null || rows.isEmpty()) {
            document.add(new Paragraph("Nenhum dado disponível para esta categoria."));
        } else {
            addTable(document, rows);
        }

        document.add(new Paragraph(" ")); // Quebra de linha para a próxima seção
    }

    private void addTable(Document document, List<ImportRow> rows) throws DocumentException {
        PdfPTable table = new PdfPTable(COLUMNS_COUNT);
        table.setWidthPercentage(100);

        addTableHeaders(table);
        addTableRows(table, rows);

        document.add(table);
    }

    private void addTableHeaders(PdfPTable table) {
        Font headerFont = new Font(Font.HELVETICA, 10, Font.BOLD);
        for (String header : TABLE_HEADERS) {
            table.addCell(new Phrase(header, headerFont));
        }
    }

    private void addTableRows(PdfPTable table, List<ImportRow> rows) {
        Font rowFont = new Font(Font.HELVETICA, 9, Font.NORMAL);

        for (ImportRow row : rows) {
            table.addCell(new Phrase(row.getNcm(), rowFont));
            table.addCell(new Phrase(row.getPaisOrigem(), rowFont));
            table.addCell(new Phrase(formatNumber(row.getVolumeTotal()), rowFont));
            table.addCell(new Phrase(formatNumber(row.getValorTotal()), rowFont));
            table.addCell(new Phrase(formatNumber(row.getPrecoMedio()), rowFont));
            table.addCell(new Phrase(formatNumber(row.getFreteMedio()), rowFont));
            table.addCell(new Phrase(formatNumber(row.getSeguroMedio()), rowFont));
        }
    }

    private String formatNumber(Number value) {
        return value == null ? "-" : NUMBER_FORMAT.format(value);
    }
}