package com.example.ms_report_async.domain.repository;

import java.io.InputStream;

public interface S3Port {
    InputStream download(String bucket, String key);
    void upload(String bucket, String key, byte[] content, String contentType);
}
