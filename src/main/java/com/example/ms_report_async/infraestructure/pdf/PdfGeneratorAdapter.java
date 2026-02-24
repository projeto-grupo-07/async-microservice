package com.example.ms_report_async.infraestructure.pdf;

import com.example.ms_report_async.domain.entity.ImportReport;
import com.example.ms_report_async.domain.entity.ImportRow;
import com.example.ms_report_async.domain.repository.S3Port;
import com.example.ms_report_async.domain.service.GeneratePdfReportUseCase;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.util.Locale;

@Component
public class PdfGeneratorAdapter implements GeneratePdfReportUseCase {
    private final S3Port s3Port;
    private final String outputBucket = "YOUR_OUTPUT_BUCKET";

    public PdfGeneratorAdapter(S3Port s3Port) {
        this.s3Port = s3Port;
    }

    @Override
    public String execute(ImportReport report) {

        byte[] pdf = buildPdf(report);

        String pdfKey = "reports/import-report-" + report.getProcessedAt() + ".pdf";

        s3Port.upload(outputBucket, pdfKey, pdf, "application/pdf");

        return pdfKey;
    }

    private byte[] buildPdf(ImportReport report) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, baos);

            document.open();

            document.add(new Paragraph("Import Report", new Font(Font.HELVETICA, 18, Font.BOLD)));
            document.add(new Paragraph("Processed At: " + report.getProcessedAt()));
            document.add(new Paragraph(" "));

            NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);

            table.addCell("NCM");
            table.addCell("Country");
            table.addCell("Volume Total");
            table.addCell("Value Total");
            table.addCell("Average Price");
            table.addCell("Average Freight");
            table.addCell("Average Insurance");

            for (ImportRow row : report.getRows()) {
                table.addCell(row.getNcm());
                table.addCell(row.getPaisOrigem());
                table.addCell(format(row.getVolumeTotal(), nf));
                table.addCell(format(row.getValorTotal(), nf));
                table.addCell(format(row.getPrecoMedio(), nf));
                table.addCell(format(row.getFreteMedio(), nf));
                table.addCell(format(row.getSeguroMedio(), nf));
            }

            document.add(table);
            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }

    private String format(Number value, NumberFormat nf) {
        return value == null ? "" : nf.format(value);
    }
}

