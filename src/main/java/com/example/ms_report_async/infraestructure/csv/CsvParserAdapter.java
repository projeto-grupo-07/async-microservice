package com.example.ms_report_async.infraestructure.csv;

import com.example.ms_report_async.domain.entity.ImportRow;
import com.example.ms_report_async.domain.repository.CsvParserPort;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class CsvParserAdapter implements CsvParserPort {

    private static final List<String> REQUIRED_HEADERS = List.of(
            "ncm", "pais_origem", "volume_total", "valor_total",
            "preco_medio", "frete_medio", "seguro_medio"
    );

    @Override
    public List<ImportRow> parse(InputStream input) {
        try (var reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {

            CSVParser parser = CSVFormat.DEFAULT
                    .withIgnoreEmptyLines()
                    .withTrim()
                    .withFirstRecordAsHeader()
                    .parse(reader);

            validateHeaders(parser.getHeaderMap());

            List<ImportRow> rows = new ArrayList<>();
            for (CSVRecord record : parser) {
                String ncm = getString(record, "ncm");
                String paisOrigem = getString(record, "pais_origem");

                ImportRow row = new ImportRow();
                row.setNcm(ncm);
                row.setPaisOrigem(paisOrigem);
                row.setVolumeTotal(getDecimal(record, "volume_total"));
                row.setValorTotal(getDecimal(record, "valor_total"));
                row.setPrecoMedio(getDecimal(record, "preco_medio"));
                row.setFreteMedio(getDecimal(record, "frete_medio"));
                row.setSeguroMedio(getDecimal(record, "seguro_medio"));

                rows.add(row);
            }
            return rows;

        } catch (Exception e) {
            throw new RuntimeException("Error parsing CSV: " + e.getMessage(), e);
        }
    }

    private void validateHeaders(Map<String, Integer> headerMap) {
        // Normalize to lowercase for comparison
        Set<String> incoming = new HashSet<>();
        for (String h : headerMap.keySet()) {
            incoming.add(h == null ? "" : h.trim().toLowerCase(Locale.ROOT));
        }

        List<String> missing = new ArrayList<>();
        for (String req : REQUIRED_HEADERS) {
            if (!incoming.contains(req)) {
                missing.add(req);
            }
        }
        if (!missing.isEmpty()) {
            throw new IllegalArgumentException("CSV missing required headers: " + missing);
        }
    }

    private String getString(CSVRecord record, String header) {
        String value = record.get(header);
        return value == null ? null : value.trim();
    }

    private BigDecimal getDecimal(CSVRecord record, String header) {
        String raw = getString(record, header);
        if (raw == null || raw.isEmpty()) return null;

        // Normalize decimal separators if needed (e.g., "1.234,56" -> "1234.56")
        String normalized = normalizeDecimal(raw);
        return new BigDecimal(normalized);
    }

    private String normalizeDecimal(String s) {
        // If both '.' and ',' exist, assume thousand separator + decimal comma -> remove '.' and replace ',' with '.'
        if (s.contains(".") && s.contains(",")) {
            return s.replace(".", "").replace(",", ".");
        }
        // If only comma, assume decimal comma
        if (s.contains(",") && !s.contains(".")) {
            return s.replace(",", ".");
        }
        // Else already dot-decimal
        return s;
    }
}