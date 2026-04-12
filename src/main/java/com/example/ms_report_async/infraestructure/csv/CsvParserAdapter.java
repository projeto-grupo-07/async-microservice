package com.example.ms_report_async.infraestructure.csv;

import com.example.ms_report_async.domain.entity.ImportRow;
import com.example.ms_report_async.domain.repository.CsvParserPort;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class CsvParserAdapter implements CsvParserPort {

    private static final Logger logger = LoggerFactory.getLogger(CsvParserAdapter.class);
    private static final Set<String> REQUIRED_HEADERS = Set.of(
            "ncm",
            "pais_origem",
            "peso_liq_soma_truncado",
            "vmle_soma_dolar_truncado",
            "fob_kg_media_dolar_truncado",
            "frete_media_dolar_truncado",
            "seguro_media_dolar_truncado"
    );

    @Override
    public List<ImportRow> parse(InputStream input) {
        logger.info("Iniciando parsing do arquivo CSV");
        long startTime = System.currentTimeMillis();

        try (var reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {

            CSVParser parser = CSVFormat.DEFAULT
                    .withIgnoreEmptyLines()
                    .withTrim()
                    .withAllowMissingColumnNames()
                    .withFirstRecordAsHeader()
                    .parse(reader);

            validateHeaders(parser.getHeaderMap());

            List<ImportRow> rows = new ArrayList<>();
            for (CSVRecord record : parser) {
                rows.add(buildImportRow(record));
            }

            long duration = System.currentTimeMillis() - startTime;
            logger.info("CSV parsing concluído com sucesso. TotalLinhas: {}, DuracaoMs: {}",
                    rows.size(), duration);
            return rows;

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("Erro ao fazer parsing do CSV. DuracaoMs: {}", duration, e);
            throw new RuntimeException("Error parsing CSV: " + e.getMessage(), e);
        }
    }

    private void validateHeaders(Map<String, Integer> headerMap) {
        logger.debug("Validando headers do CSV. Headers encontrados: {}", headerMap.keySet());

        Set<String> incoming = headerMap.keySet().stream()
                .map(h -> (h == null ? "" : h.trim().toLowerCase(Locale.ROOT)))
                .collect(Collectors.toSet());

        List<String> missing = REQUIRED_HEADERS.stream()
                .filter(req -> !incoming.contains(req))
                .toList();

        if (!missing.isEmpty()) {
            logger.error("CSV com headers faltantes: {}", missing);
            throw new IllegalArgumentException("CSV missing required headers: " + missing);
        }

        logger.debug("Headers validados com sucesso");
    }

    private ImportRow buildImportRow(CSVRecord record) {
        try {
            ImportRow row = new ImportRow();

            // Textos
            row.setNcm(getTrimmedValue(record, "ncm"));
            row.setPaisOrigem(getTrimmedValue(record, "pais_origem"));

            // Totais
            row.setVolumeTotal(getDecimal(record, "peso_liq_soma_truncado"));
            row.setValorTotal(getDecimal(record, "vmle_soma_dolar_truncado"));

            // Médias
            row.setPrecoMedio(getDecimal(record, "fob_kg_media_dolar_truncado"));
            row.setFreteMedio(getDecimal(record, "frete_media_dolar_truncado"));
            row.setSeguroMedio(getDecimal(record, "seguro_media_dolar_truncado"));

            return row;
        } catch (Exception e) {
            logger.error("Erro ao construir ImportRow do record: {}", record, e);
            throw e;
        }
    }
    private String getTrimmedValue(CSVRecord record, String header) {
        String value = record.get(header);
        return value == null ? null : value.trim();
    }

    private BigDecimal getDecimal(CSVRecord record, String header) {
        String raw = getTrimmedValue(record, header);
        if (raw == null || raw.isEmpty()) return null;

        try {
            return new BigDecimal(normalizeDecimal(raw));
        } catch (NumberFormatException e) {
            logger.error("Valor decimal inválido para coluna '{}': {}", header, raw, e);
            throw new IllegalArgumentException("Invalid decimal value for column '" + header + "': " + raw, e);
        }
    }

    private String normalizeDecimal(String s) {
        return s.replace(",", "");
    }
}