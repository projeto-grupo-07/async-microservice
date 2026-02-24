package com.example.ms_report_async.domain.repository;

import com.example.ms_report_async.domain.entity.ImportRow;

import java.io.InputStream;
import java.util.List;

public interface CsvParserPort {
    List<ImportRow> parse(InputStream input);
}
